package com.github.pse_perma.perma.virtual_actuator.app;

import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorConfig;
import de.fraunhofer.iosb.ilt.sta.model.Actuator;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ConfigFactoryTest {

    @Test(expected = IOException.class)
    public void parseFile_noexist() throws IOException, ConfigFactory.InvalidConfig {
        File file = new File("invalid/path/file");
        ConfigFactory configFactory = new ConfigFactory();
        configFactory.parseFile(file);
    }

    @Test(expected = ConfigFactory.InvalidConfig.class)
    public void parseFile_empty() throws IOException, ConfigFactory.InvalidConfig {
        File file = new File("src/test/resources/config_empty.json");
        ConfigFactory configFactory = new ConfigFactory();
        configFactory.parseFile(file);
    }

    @Test
    public void parseFile_valid_no_capabilities() throws IOException, ConfigFactory.InvalidConfig {
        File file = new File("src/test/resources/config_valid_no_capabilities.json");
        ConfigFactory configFactory = new ConfigFactory();
        configFactory.parseFile(file);
        VirtualActuatorConfig config = configFactory.build();

        assertEquals(new URL("http://example.invalid/"), config.getStHttpServerUrl());
        assertEquals("tcp://example.invalid:30000", config.getStMqttServerUrl());

        Actuator actuator = config.getConfigActuator();
        assertEquals("TestActuator", actuator.getName());
        assertEquals("Actuator for unit-testing.", actuator.getDescription());

        Thing thing = config.getConfigThing();
        assertEquals("TestActuator-Thing", thing.getName());
        assertEquals("A thing for TestActuator.", thing.getDescription());
    }

    @Test(expected = ConfigFactory.InvalidConfig.class)
    public void parseFile_invalid_capabilities() throws IOException, ConfigFactory.InvalidConfig {
        File file = new File("src/test/resources/config_invalid_capabilities.json");
        ConfigFactory configFactory = new ConfigFactory();
        configFactory.parseFile(file);
    }
}