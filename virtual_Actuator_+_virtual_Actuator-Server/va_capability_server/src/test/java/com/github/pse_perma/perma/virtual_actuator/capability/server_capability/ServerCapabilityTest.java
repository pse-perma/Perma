package com.github.pse_perma.perma.virtual_actuator.capability.server_capability;

import com.github.pse_perma.perma.virtual_actuator.capability.CapabilityProvider;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorCapability;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorConfig;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorContext;
import de.fraunhofer.iosb.ilt.sta.model.Actuator;
import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@TestMethodOrder(OrderAnnotation.class)
class ServerCapabilityTest {
    private static final String ACTUATOR_DIR = "actuators";
    private static final String CapabilityJarOne = "https://github.com/FraunhoferIOSB/FROST-Server/blob/master/README.md";
    private static final String CapabilityJarTwo = "https://github.com/FraunhoferIOSB/FROST-Server/blob/master/CHANGELOG.md";
    private static final String CapabilityJarOneLocal = "README.md";
    private static final String CapabilityJarTwoLocal = "CHANGELOG.md";
    private static final String TEST_ACTUATOR_NAME = "handleTaskTestVirtualActuatorName";
    private static final String CONFIG_FILE = "config.json";

    private static VirtualActuatorContext virtualActuatorContext = null;
    private static ServerCapability serverCapability;
    private static TaskingCapability taskingCapability;
    private static Map<String, Object> expectedParameters;

    @BeforeAll
    private static void globalSetUp() throws MalformedURLException {
        Map<String, Object> parameterActuatorName = new LinkedHashMap<>();
        parameterActuatorName.put("type", "Text");
        parameterActuatorName.put("name", "vaName");
        parameterActuatorName.put("label", "Aktor-Name");
        parameterActuatorName.put("description", "Name des neuen virtuellen Aktors");

        Map<String, Object> parameterActuatorDescription = new LinkedHashMap<>();
        parameterActuatorDescription.put("type", "Text");
        parameterActuatorDescription.put("name", "vaDescription");
        parameterActuatorDescription.put("label", "Aktor-Beschreibung");
        parameterActuatorDescription.put("description", "Beschreibung des neuen virtuellen Aktors");

        Map<String, Object> parameterThingName = new LinkedHashMap<>();
        parameterThingName.put("type", "Text");
        parameterThingName.put("name", "thingName");
        parameterThingName.put("label", "Thing-Name");
        parameterThingName.put("description", "Name des zugehoerigen Things");

        Map<String, Object> parameterThingDescription = new LinkedHashMap<>();
        parameterThingDescription.put("type", "Text");
        parameterThingDescription.put("name", "thingDescription");
        parameterThingDescription.put("label", "Thing-Beschreibung");
        parameterThingDescription.put("description", "Beschreibung des zugehoerigen Things");

        Map<String, Object> parameterCapabilityList = new LinkedHashMap<>();
        parameterCapabilityList.put("type", "Text");
        parameterCapabilityList.put("name", "capabilityList");
        parameterCapabilityList.put("label", "Gewaehlte Capabilities");
        parameterCapabilityList.put("description", "Eine durch ; getrennte Liste, welche die Jar-Dateien mit den gewuenschten Capabilities enthaelt");

        List<Map<String, Object>> parameterCollection = new ArrayList<>();
        parameterCollection.add(parameterActuatorName);
        parameterCollection.add(parameterActuatorDescription);
        parameterCollection.add(parameterThingName);
        parameterCollection.add(parameterThingDescription);
        parameterCollection.add(parameterCapabilityList);

        expectedParameters = new LinkedHashMap<>();
        expectedParameters.put("type", "DataRecord");
        expectedParameters.put("field", parameterCollection);

        Actuator configActuator = new Actuator();

        VirtualActuatorConfig config = new VirtualActuatorConfig(
                new URL("https://example.org/v1.0"),
                "tcp://example.org:30000",
                "v1.0/", configActuator,
                null, new ArrayList<>(),
                new CapabilityProvider[0],
                null,
                null
        );

        virtualActuatorContext = new VirtualActuatorContext(null, config, null, null, null);
        serverCapability = new ServerCapability();
        taskingCapability = serverCapability.getCapability(virtualActuatorContext, new HashMap<>());
    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @AfterAll
    static void tearAllDown() {
        // delete downloaded files and Directory
        File f1 = new File(ACTUATOR_DIR + File.separator + TEST_ACTUATOR_NAME + File.separator + CapabilityJarOneLocal);
        File f2 = new File(ACTUATOR_DIR + File.separator + TEST_ACTUATOR_NAME + File.separator + CapabilityJarTwoLocal);
        File f3 = new File(ACTUATOR_DIR + File.separator + TEST_ACTUATOR_NAME + File.separator + CONFIG_FILE);
        File d1 = new File(ACTUATOR_DIR + File.separator + TEST_ACTUATOR_NAME);

        f1.delete();
        f2.delete();
        f3.delete();
        d1.delete();
    }

    @Test
    @Order(1)
    void getCapability() {
        String actualName = taskingCapability.getName();
        String actualDescr = taskingCapability.getDescription();
        Map<String, Object> actualTaskingParameter = taskingCapability.getTaskingParameters();

        Assertions.assertEquals("createNewVA", actualName);
        Assertions.assertEquals("Virtual Actuator Server, starts new Virtual Actuators", actualDescr);
        Assertions.assertEquals(expectedParameters, actualTaskingParameter);
    }

    @Test
    @Order(2)
    void handleTaskWithEmptyTask() {
        Task task = new Task();

        VirtualActuatorCapability.TaskExecutionResult actualResult = serverCapability.handleTask(task);
        Assertions.assertEquals(VirtualActuatorCapability.TaskExecutionResult.FAILURE, actualResult);
    }

    @Test
    @Order(3)
    void handleTask() {
        Task task = new Task();
        Map<String, Object> taskingParameter = new LinkedHashMap<>();
        taskingParameter.put("vaName", TEST_ACTUATOR_NAME);
        taskingParameter.put("vaDescription", "handleTaskTestVirtualActuatorDescription");
        taskingParameter.put("thingName", "handleTaskTestVirtualActuatorThingName");
        taskingParameter.put("thingDescription", "handleTaskTestVirtualActuatorThingDescription");
        taskingParameter.put("capabilityList", CapabilityJarOne + ";" + CapabilityJarTwo);
        task.setTaskingParameters(taskingParameter);

        VirtualActuatorCapability.TaskExecutionResult actualResult = serverCapability.handleTask(task);

        // ToDo: it fails to execute .md files, try to download and execute jar-file and expect Result.SUCCESS
        Assertions.assertEquals(VirtualActuatorCapability.TaskExecutionResult.FAILURE, actualResult);
    }

    @Test
    @Order(4)
    void handleTask2() {
        // runs only correctly if working directory is set to ./implementierung-virtueller-aktor
        Map<String, Object> taskingParameter = new LinkedHashMap<>();
        taskingParameter.put("vaName", TEST_ACTUATOR_NAME);
        taskingParameter.put("vaDescription", "handleTaskTestVirtualActuatorDescription");
        taskingParameter.put("thingName", "handleTaskTestVirtualActuatorThingName");
        taskingParameter.put("thingDescription", "handleTaskTestVirtualActuatorThingDescription");
        String f = "file://" + (new File(".")).getAbsolutePath().toString() + "/va_capability_server/src/test/resources/va_capability_example.jar";
        // "file:///home/hydro/pse/impl/va/va_capability_server/src/test/resources/va_capability_example.jar");
        taskingParameter.put("capabilityList", f);
        TaskingCapability taskingCapability = new TaskingCapability("createNewVA", "description");

        Task task = new Task(taskingCapability, taskingParameter);

        VirtualActuatorCapability.TaskExecutionResult actualResult = serverCapability.handleTask(task);
        // ToDo: it fails to execute .md files, try to download and execute jar-file and expect Result.SUCCESS
        Assertions.assertEquals(VirtualActuatorCapability.TaskExecutionResult.SUCCESS, actualResult);
    }
}