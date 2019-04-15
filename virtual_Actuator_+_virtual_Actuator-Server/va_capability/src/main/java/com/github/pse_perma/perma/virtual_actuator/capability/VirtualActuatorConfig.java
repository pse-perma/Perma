package com.github.pse_perma.perma.virtual_actuator.capability;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.sta.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VirtualActuatorConfig {

    public static final String ACTUATOR_ENTITY_INTERNAL_NAME = "main_actuator";
    public static final String SENSOR_ENTITY_INTERNAL_NAME   = "main_sensor";
    public static final String THING_ENTITY_INTERNAL_NAME    = "main_thing";

    private static Logger logger = LoggerFactory.getLogger(VirtualActuatorConfig.class);

    private final URL stHttpServerUrl;
    private String stMqttServerUrl;

    private final Actuator configActuator;
    private final Thing configThing;
    private List<Location> locations;
    private List<CapabilityProvider> capabilityProviders;

    private Map<String, Id> entityIdMap;
    private File idMapFile;
    private String mqttTopicPrefix;


    public VirtualActuatorConfig(URL stHttpServerUrl, String stMqttServerUrl, String mqttTopicPrefix,
                                 Actuator configActuator, Thing configThing, List<Location> locations,
                                 CapabilityProvider[] capabilityProviders,
                                 Map<String, Id> entityIdMap, File idMapFile) {
        this.stHttpServerUrl = stHttpServerUrl;
        this.stMqttServerUrl = stMqttServerUrl;
        this.mqttTopicPrefix = mqttTopicPrefix;

        this.configActuator = configActuator;
        this.configThing = configThing;
        this.locations = locations;
        this.capabilityProviders = Arrays.asList(capabilityProviders);

        this.entityIdMap = entityIdMap;
        this.idMapFile = idMapFile;
    }



    public List<CapabilityProvider> getCapabilityProviders() {
        return capabilityProviders;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public URL getStHttpServerUrl() {
        return stHttpServerUrl;
    }

    public String getStMqttServerUrl() {
        return stMqttServerUrl;
    }

    public Thing getConfigThing() {
        return configThing;
    }

    public Actuator getConfigActuator() {
        Map<String, Object> actuatorMetadata = new HashMap<>();
        actuatorMetadata.put("sensorId", getEntityId(getConfigSensor(), SENSOR_ENTITY_INTERNAL_NAME));
        this.configActuator.setMetadata(actuatorMetadata);
        return this.configActuator;
    }

    public Sensor getConfigSensor() {
        Map<String, Object> sensorMetadata = new HashMap<>();
        if (this.configActuator.getId() != null) {
            sensorMetadata.put("actuatorId", this.configActuator.getId());
        }

        Sensor sensor = new Sensor(configActuator.getName(), configActuator.getDescription(),
                configActuator.getEncodingType(), sensorMetadata);
        sensor.setId(getEntityId(sensor, SENSOR_ENTITY_INTERNAL_NAME));

        return sensor;
    }

    public String getMqttRessourcePathPrefix() {
        if (this.mqttTopicPrefix != null)
            return this.mqttTopicPrefix;

        String path = this.getStHttpServerUrl().getPath();
        if (path.isEmpty()) return path;

        if (path.charAt(0) == '/') path = path.substring(1);
        return path;
    }


    private String getEntityKeyFormat(Entity entity, String name) {
        return String.format("%s-%s", entity.getClass().getCanonicalName(), name);
    }

    public Id getEntityId(Entity entity, String name) {
        return entityIdMap.get(getEntityKeyFormat(entity, name));
    }

    public void setEntityId(Entity entity, String name) {
        entityIdMap.put(getEntityKeyFormat(entity, name), entity.getId());
    }

    public void writeIdMap() throws IOException {
        if (idMapFile != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(idMapFile, entityIdMap);
        }
    }
}
