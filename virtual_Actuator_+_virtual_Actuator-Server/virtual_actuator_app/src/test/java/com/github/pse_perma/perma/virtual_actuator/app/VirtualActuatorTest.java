package com.github.pse_perma.perma.virtual_actuator.app;

import com.github.pse_perma.perma.virtual_actuator.capability.CapabilityProvider;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorConfig;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Actuator;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class VirtualActuatorTest {

    private static final Actuator CONFIG_ACTUATOR
            = new Actuator("UnitTestActuator", "foobardesc", "random", "string");
    private static final Thing CONFIG_THING
            = new Thing("UnitTestThing", "foobardesc", new HashMap<>());


    @Rule
    public DockerComposeContainer perma =
            new DockerComposeContainer(new File("/mnt/Data/Studium/5. Semester/PSE/Implementierung/implementierung-Compound/docker-compose.yml"))
                    .withExposedService("frost-server", 8080,
                            Wait.forHttp("/FROST-Server/v1.0/Actuators").forStatusCode(200));

    private URL stHttpUrl;
    private String stMqttUrl;

    @Before
    public void setUp() throws Exception {
        String address = perma.getServiceHost("frost-server", 8080);
        int httpPort = perma.getServicePort("frost-server", 8080);
        int mqttPort = 111; //perma.getServicePort("frost-server", 1883);

        try {
            stHttpUrl = new URL("http://" + address + ":" + httpPort + "/FROST-Server/v1.0/");
            stMqttUrl = "tcp://" + address + ":" + mqttPort;
        } catch (MalformedURLException mue) {
            fail(mue.getMessage());
        }
    }

    @After
    public void tearDown() throws Exception {
    }


    @NotNull
    private VirtualActuatorConfig getVirtualActuatorConfig() throws IOException {
        CapabilityProvider[] capabilityProviders = new CapabilityProvider[] {
                new JarCapabilityProvider(
                        new File("../va_capability_example/build/libs/va_capability_example.jar"),
                        new HashMap<>())
        };

        return new VirtualActuatorConfig(stHttpUrl, stMqttUrl, "v1.0/",
                CONFIG_ACTUATOR, CONFIG_THING,
                new ArrayList<>(), capabilityProviders, new HashMap<>(), new File("unittest-idMap.json"));
    }

    @NotNull
    private VirtualActuator getVirtualActuator() throws IOException {
        VirtualActuatorConfig config = getVirtualActuatorConfig();
        return new VirtualActuator(config);
    }


    @Test
    public void registerBaseEntities() throws IOException, ServiceFailureException {
        VirtualActuator virtualActuator = getVirtualActuator();
        virtualActuator.registerBaseEntities();

        SensorThingsService sts = new SensorThingsService(stHttpUrl);

        // expecting 2 entities since the VAS has to be counted in

        List<Actuator> actuators = sts.actuators().query().list().toList();
        assertEquals(2, actuators.size());
        //assertEquals(CONFIG_ACTUATOR, actuators.toList().get(1));

        List<Sensor> sensors = sts.sensors().query().list().toList();
        assertEquals(2, sensors.size());
        //assertEquals(getVirtualActuatorConfig().getConfigSensor(), sensors.toList().get(1));

        List<Thing> things = sts.things().query().list().toList();
        assertEquals(2, things.size());
        //assertEquals(CONFIG_THING, things.toList().get(1));
    }


    @Test
    public void loadCapabilities() throws IOException {
        VirtualActuator virtualActuator = getVirtualActuator();

        virtualActuator.loadCapabilities();
        assertEquals(1, virtualActuator.capabilities.size());
    }

    @Test
    public void registerCapabilities() throws IOException, ServiceFailureException {
        VirtualActuator virtualActuator = getVirtualActuator();
        virtualActuator.loadCapabilities();

        SensorThingsService sts = new SensorThingsService(stHttpUrl);

        List<Actuator> actuators = sts.actuators().query().list().toList();
        EntityList<TaskingCapability> capabilities = actuators.get(0).getCapabilities();
        assertEquals(1, capabilities.size());
    }
}