package com.github.pse_perma.perma.virtual_actuator.capability.server_capability;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigCompose {
    private static class JsonConfig {
        public static class JsonConfigThing {
            public String name;
            public String description;
        }

        public static class CapabilityJar {
            public String file;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public Map<String, Object> parameters; // optional

            public CapabilityJar(String file, Map<String, Object> parameters) {
                this.file = file;
                this.parameters = parameters;
            }
        }

        public String stHttpServerUrl;
        public String stMqttServerUrl;
        public String stMqttTopicPrefix;
        public String actuatorName;
        public String actuatorDescription;
        public List<CapabilityJar> capabilityJars;
        public JsonConfigThing commonActuatorThing;
    }

    final Logger logger = LoggerFactory.getLogger(ConfigCompose.class);

    private ObjectMapper objectMapper;
    private JsonConfig jsonConfig;

    public ConfigCompose() {
        objectMapper = new ObjectMapper();
    }

    public void setValues(String stHttpServerUrl,
                          String stMqttServerUrl,
                          String stMqttTopicPrefix,
                          String actuatorName,
                          String actuatorDescription,
                          List<String> capabilityJars,
                          String thingName,
                          String thingDescription) {

        jsonConfig = new JsonConfig();
        JsonConfig.JsonConfigThing jsonConfigThing = new JsonConfig.JsonConfigThing();

        jsonConfig.stHttpServerUrl = stHttpServerUrl;
        jsonConfig.stMqttServerUrl = stMqttServerUrl;
        jsonConfig.stMqttTopicPrefix = stMqttTopicPrefix;
        jsonConfig.actuatorName = actuatorName;
        jsonConfig.capabilityJars = capabilityJars.stream().map(
                s -> new JsonConfig.CapabilityJar(s, null)  // TODO: extend createVA-capability to support parameters
        ).collect(Collectors.toList());
        jsonConfig.actuatorDescription = actuatorDescription;

        jsonConfigThing.name = thingName;
        jsonConfigThing.description = thingDescription;
        jsonConfig.commonActuatorThing = jsonConfigThing;
    }

    public void composeFile(File file) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, jsonConfig);
    }
}
