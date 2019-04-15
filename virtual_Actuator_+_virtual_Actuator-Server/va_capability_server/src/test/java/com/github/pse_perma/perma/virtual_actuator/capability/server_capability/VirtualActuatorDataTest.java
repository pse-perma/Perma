package com.github.pse_perma.perma.virtual_actuator.capability.server_capability;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;


public class VirtualActuatorDataTest {
    @Test
    void storeLoadTest() {
        VirtualActuatorData va = new VirtualActuatorData(
                "exampleHttpServerUrl",
                "exampleMqttURL",
                "v1.0/",
                "exampleActuatorName",
                "exampleActuatorDescription",
                "exampleThingName",
                "exampleThingDescription"
        );

        va.setJarFileList(Arrays.asList("capabilityJar1", "capabilityJar2"));

        Assertions.assertEquals("exampleHttpServerUrl", va.getStHttpServerUrl());
        Assertions.assertEquals("exampleMqttURL", va.getStMqttServerUrl());
        Assertions.assertEquals("v1.0/", va.getMqttRessourcePathPrefix());
        Assertions.assertEquals("exampleActuatorName", va.getActuatorName ());
        Assertions.assertEquals("exampleActuatorDescription", va.getActuatorDescription());
        Assertions.assertEquals("exampleThingName", va.getThingName());
        Assertions.assertEquals("exampleThingDescription", va.getThingDescription());
        Assertions.assertIterableEquals(Arrays.asList("capabilityJar1", "capabilityJar2"), va.getJarFileList());
    }

    @Test
    void setGetTest() {
        VirtualActuatorData va = new VirtualActuatorData(
                "exampleHttpServerUrl",
                "exampleMqttURL",
                "v1.0/",
                "exampleActuatorName",
                "exampleActuatorDescription",
                "exampleThingName",
                "exampleThingDescription"
        );

        va.setJarFileList(Arrays.asList("new_capabilityJar1", "new_capabilityJar2"));
        va.setStHttpServerUrl("new_exampleHttpServerUrl");
        va.setStMqttServerUrl("new_exampleMqttURL");
        va.setMqttRessourcePathPrefix("new_v1.0/");
        va.setActuatorName("new_exampleActuatorName");
        va.setActuatorDescription("new_exampleActuatorDescription");
        va.setThingName("new_exampleThingName");
        va.setThingDescription("new_exampleThingDescription");

        Assertions.assertEquals("new_exampleHttpServerUrl", va.getStHttpServerUrl());
        Assertions.assertEquals("new_exampleMqttURL", va.getStMqttServerUrl());
        Assertions.assertEquals("new_v1.0/", va.getMqttRessourcePathPrefix());
        Assertions.assertEquals("new_exampleActuatorName", va.getActuatorName ());
        Assertions.assertEquals("new_exampleActuatorDescription", va.getActuatorDescription());
        Assertions.assertEquals("new_exampleThingName", va.getThingName());
        Assertions.assertEquals("new_exampleThingDescription", va.getThingDescription());
        Assertions.assertIterableEquals(Arrays.asList("new_capabilityJar1", "new_capabilityJar2"), va.getJarFileList());
    }

    @Test
    void getActuatorHashString() {
        VirtualActuatorData va1 = new VirtualActuatorData(
                "exampleHttpServerUrl",
                "exampleMqttURL",
                "v1.0/",
                "exampleActuatorName",
                "exampleActuatorDescription",
                "exampleThingName",
                "exampleThingDescription"
        );

        VirtualActuatorData va2 = new VirtualActuatorData(
                "exampleHttpServerUrl",
                "exampleMqttURL",
                "v1.0/",
                "exampleActuatorName",
                "exampleActuatorDescription",
                "exampleThingName",
                "exampleThingDescription"
        );

        String str1 = va1.getActuatorHashString();
        String str2 = va2.getActuatorHashString();

        Assertions.assertNotNull(str1);
        Assertions.assertNotNull(str2);
        Assertions.assertNotEquals(str1, str2);
    }
}
