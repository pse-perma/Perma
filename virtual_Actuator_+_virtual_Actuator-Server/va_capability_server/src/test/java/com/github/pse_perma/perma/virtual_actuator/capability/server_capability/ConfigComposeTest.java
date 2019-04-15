package com.github.pse_perma.perma.virtual_actuator.capability.server_capability;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

class ConfigComposeTest {

    @Test
    void composeFile_writeAndReadTest() throws IOException {
        String expectedResult = "{\n" +
                "  \"stHttpServerUrl\" : \"exampleHttpURL\",\n" +
                "  \"stMqttServerUrl\" : \"exampleMqttURL\",\n" +
                "  \"stMqttTopicPrefix\" : \"v1.0/\",\n" +
                "  \"actuatorName\" : \"exampleActuatorName\",\n" +
                "  \"actuatorDescription\" : \"exampleActuatorDescription\",\n" +
                "  \"capabilityJars\" : [ {\n" +
                "    \"file\" : \"capabilityJar1\"\n" +
                "  }, {\n" +
                "    \"file\" : \"capabilityJar2\"\n" +
                "  } ],\n" +
                "  \"commonActuatorThing\" : {\n" +
                "    \"name\" : \"exampleThingName\",\n" +
                "    \"description\" : \"exampleThingDescription\"\n" +
                "  }\n" +
                "}";



        ConfigCompose configCompose = new ConfigCompose();

        List<String> capList = new LinkedList<>();
        capList.add("capabilityJar1");
        capList.add("capabilityJar2");

        configCompose.setValues(
                "exampleHttpURL",
                "exampleMqttURL",
                "v1.0/",
                "exampleActuatorName",
                "exampleActuatorDescription",
                capList,
                "exampleThingName",
                "exampleThingDescription"
        );

        configCompose.composeFile(new File("config_compose_test_file.json"));
        File file = new File("config_compose_test_file.json");

        String actualText = new String();
        actualText = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

        Assertions.assertEquals(expectedResult, actualText);
        file.delete();
    }
}