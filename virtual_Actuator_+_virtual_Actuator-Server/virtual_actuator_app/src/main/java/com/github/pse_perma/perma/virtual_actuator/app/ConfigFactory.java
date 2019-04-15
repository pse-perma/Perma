package com.github.pse_perma.perma.virtual_actuator.app;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pse_perma.perma.virtual_actuator.capability.CapabilityProvider;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorConfig;
import de.fraunhofer.iosb.ilt.sta.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ConfigFactory {

    private static final String DEFAULT_ENCODING_TYPE = "application/perma.virtualactuator";

    private static Logger logger = LoggerFactory.getLogger(ConfigFactory.class);


    public static class InvalidConfig extends Exception {
        public InvalidConfig(String msg) {
            super(msg);
        }
    }


    private static class ConfigValue <T> {
        private String name;

        private T value;
        private boolean fromEnvironment = false;

        public ConfigValue(String name) {
            this.name = name;
        }


        private void throwIfNull(T value, String source) throws InvalidConfig {
            if (value == null) {
                throw new InvalidConfig("The " + name + " value from " + source + " is invalid.");
            }
        }


        public void setFromConfigFile(T value) throws InvalidConfig {
            throwIfNull(value, "config-file");
            if (!this.fromEnvironment) this.value = value;
        }

        public void setFromEnvironment(T value) throws InvalidConfig {
            throwIfNull(value, "environment");
            this.value = value;
            this.fromEnvironment = true;
        }

        public T getValue() {
            logger.info("Setting " + this.name + " was retrieved from "
                    + (this.fromEnvironment ? "environment - config-file-value is ignored" : "config-file")
                    + ". Value is '" + value + "'.");
            return value;
        }
    }


    private static class JsonConfig {
        public static class JsonConfigThing {
            public String name;
            public String description;

            public List<Location> locations = new ArrayList<>(); // optional
        }

        public static class CapabilityJar {
            public String file;
            public Map<String, Object> parameters = new HashMap<>(); // optional
        }

        public String stHttpServerUrl;
        public String stMqttServerUrl;
        public String stMqttTopicPrefix;

        public String actuatorName;
        public String actuatorDescription;
        public String actuatorEncodingType;


        public List<CapabilityJar> capabilityJars = new ArrayList<>(); // optional
        public String idMapFile;

        public JsonConfigThing commonActuatorThing;
    }


    private ConfigValue<URL> stHttpServerUrl = new ConfigValue<>("Sensorthings-HTTP-server-URL");
    private ConfigValue<URI> stMqttServerUrl = new ConfigValue<>("Sensorthings-MQTT-server-URL");
    private ConfigValue<String> stMqttTopicPrefix = new ConfigValue<>("Sensorthings-MQTT-topic-prefix");
    private Actuator configActuator;
    private Thing configThing;
    private CapabilityProvider[] providers;
    private List<Location> commonActuatorThingLocations;
    private Map<String, Id> idMap;
    private File idMapFile;



    public void parseFile(File configFile) throws IOException, InvalidConfig {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        //mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        JsonConfig config;
        try {
            config = mapper.readValue(configFile, JsonConfig.class);
        } catch (JsonParseException | JsonMappingException e) {
            throw new InvalidConfig("Config-file couldn't be parsed.");
        }

        if (config.actuatorName == null || config.stHttpServerUrl == null || config.stMqttServerUrl == null) {
            throw new InvalidConfig("One of the required values is not set in the config-file.");
        }

        List<JarCapabilityProvider> capabilityProviders = new ArrayList<>();

        for (JsonConfig.CapabilityJar capabilityJar : config.capabilityJars) {
            if (capabilityJar.file == null) {
                throw new InvalidConfig("A JAR-capability-entry is invalid.");
            }
            File jarFile = new File(capabilityJar.file);
            if (!jarFile.isFile()) {
                throw new InvalidConfig("One of the provided JAR-files does not exist.");
            }
            capabilityProviders.add(new JarCapabilityProvider(jarFile, capabilityJar.parameters));
        }

        URL httpServerUrl;
        try { httpServerUrl = new URL(config.stHttpServerUrl); }
        catch (MalformedURLException e) { httpServerUrl = null; }
        this.stHttpServerUrl.setFromConfigFile(httpServerUrl);

        URI mqttServerUri;
        try { mqttServerUri = new URI(config.stMqttServerUrl); }
        catch (URISyntaxException e) { mqttServerUri = null; }
        this.stMqttServerUrl.setFromConfigFile(mqttServerUri);

        this.stMqttTopicPrefix.setFromConfigFile(config.stMqttTopicPrefix);

        if (config.idMapFile == null) idMapFile = new File("./idMap.json");
        else  {
            if (config.idMapFile.isEmpty()) { // no idMapFile is used
                this.idMapFile = null;
                logger.info("No id-map used.");
            }
            else idMapFile = new File("./" + config.idMapFile);
        }

        if (idMapFile != null) this.idMap = parseIdMapFile(mapper, idMapFile);
        else this.idMap = new HashMap<>();

        this.configActuator = new Actuator(config.actuatorName, config.actuatorDescription,
                config.actuatorEncodingType != null ? config.actuatorEncodingType : DEFAULT_ENCODING_TYPE, null);

        this.configThing = new Thing(config.commonActuatorThing.name, config.commonActuatorThing.description);

        this.providers = new CapabilityProvider[capabilityProviders.size()];
        this.providers = capabilityProviders.toArray(providers);
        this.commonActuatorThingLocations = config.commonActuatorThing.locations;
    }


    public void loadFromEnvironment() throws InvalidConfig {
        String va_http_server = System.getenv("VA_HTTP_SERVER");
        if (va_http_server != null) {
            URL httpServerUrl;
            try { httpServerUrl = new URL(va_http_server); }
            catch (MalformedURLException e) { httpServerUrl = null; }
            this.stHttpServerUrl.setFromEnvironment(httpServerUrl);
        }

        String va_mqtt_server = System.getenv("VA_MQTT_SERVER");
        if (va_mqtt_server != null) {
            URI mqttServerUri;
            try { mqttServerUri = new URI(va_mqtt_server); }
            catch (URISyntaxException e) { mqttServerUri = null; }
            this.stMqttServerUrl.setFromEnvironment(mqttServerUri);
        }

        String va_mqtt_prefix = System.getenv("VA_MQTT_PREFIX");
        if (va_mqtt_prefix != null) {
            this.stMqttTopicPrefix.setFromEnvironment(va_mqtt_prefix);
        }
    }


    public VirtualActuatorConfig build() {
        return new VirtualActuatorConfig(this.stHttpServerUrl.getValue(),
                this.stMqttServerUrl.getValue().toString(), this.stMqttTopicPrefix.getValue(),
                this.configActuator, this.configThing, this.commonActuatorThingLocations,
                this.providers, this.idMap, this.idMapFile);
    }



    private static Map<String, Id> parseIdMapFile(ObjectMapper mapper, File idMapFile) throws IOException {
        if (!idMapFile.exists()) {
            File parentFile = idMapFile.getParentFile();
            parentFile.mkdirs();
            PrintWriter writer = new PrintWriter(idMapFile, "UTF-8");
            writer.println("{}");
            writer.close();
        }

        Map<String, Integer> rawIdMap = mapper.readValue(idMapFile, Map.class);
        Map<String, Id> idMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : rawIdMap.entrySet()) {
            idMap.put(entry.getKey(), new IdLong(entry.getValue().longValue()));
        }
        return idMap;
    }
}
