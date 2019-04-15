package com.github.pse_perma.perma.virtual_actuator.capability.server_capability;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class JarFileManagerTest {
    @Test
    void testDownloadJarFile() {
        File file = new File("file.test");
        try {
            file.createNewFile();
            JarFileManager.downloadJarFile("https://github.com/FraunhoferIOSB/FROST-Server/blob/master/README.md", file.toString());

            String actualText = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(file.exists() && file.length() > 0);
        file.delete();
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getProcessIdMap() throws IOException {
        File directory = new File("testActuators");
        directory.mkdir();
        File actDirectory = new File(directory, "testActuator1");
        actDirectory.mkdir();
        File idMapFile = new File(actDirectory, "idMap.json");
        idMapFile.createNewFile();

        PrintWriter writer = new PrintWriter(idMapFile, "UTF-8");
        writer.println("{\n" +
                "  \"de.fraunhofer.iosb.ilt.sta.model.Thing-main_thing\" : 19,\n" +
                "  \"de.fraunhofer.iosb.ilt.sta.model.Actuator-main_actuator\" : 94,\n" +
                "  \"de.fraunhofer.iosb.ilt.sta.model.Sensor-main_sensor\" : 41,\n" +
                "  \"de.fraunhofer.iosb.ilt.sta.model.TaskingCapability-createNewVA\" : 47\n" +
                "}");
        writer.close();

        JarFileManager jfm = new JarFileManager();
        jfm.setActuatorDirectoryName(directory.toString());
        Map<Integer, File> actuators = jfm.getVAIdToDirectoryMap();

        Assertions.assertEquals(1, actuators.size());
        Assertions.assertEquals(new File("testActuators/testActuator1"), actuators.get(94));

        idMapFile.delete();
        actDirectory.delete();
        directory.delete();
    }

    @Test
    void downloadJarFile() {
        File file = new File("file2.test");

        boolean isCorrectDownloaded = true;
        try {
            file.createNewFile();
            isCorrectDownloaded = JarFileManager.downloadJarFile("https://www.google.de/bing", file.toString());
            String actualText = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            isCorrectDownloaded = false;
        }
        Assertions.assertFalse(isCorrectDownloaded);
        file.delete();
    }


    /*
    @Test
    void addAndExecuteVirtualActuator() throws IOException {
        VirtualActuatorData vad = new VirtualActuatorData (
                "exampleHttpServerUrl",
                "exampleMqttURL",
                "v1.0/",
                "exampleActuatorName",
                "exampleActuatorDescription",
                "exampleThingName",
                "exampleThingDescription"
        );
        //  System.out.println((new File(".").getAbsolutePath()));
        //  System.out.println(vad.getWorkingDirectory());
        vad.setJarFileList(Arrays.asList("va_capability_example.jar"));
        JarFileManager jfm = new JarFileManager();
        File workingDirectory = vad.getWorkingDirectory();
        workingDirectory.getAbsolutePath() + "/" + locName);


        jfm.addAndExecuteVirtualActuator(vad);
        //Assertions.assertThrows(java.io.IOException.class, ()->jfm.addAndExecuteVirtualActuator(vad));
    }
    */
    /*
    @Test
    void destroyProcessWithActuatorId() throws IOException {
        File directory = new File("testActuators");
        directory.mkdir();
        File actDirectory = new File(directory, "testActuator1");
        actDirectory.mkdir();
        File idMapFile = new File(actDirectory, "idMap.json");
        idMapFile.createNewFile();

        PrintWriter writer = new PrintWriter(idMapFile, "UTF-8");
        writer.println("{\n" +
                "  \"de.fraunhofer.iosb.ilt.sta.model.Thing-main_thing\" : 19,\n" +
                "  \"de.fraunhofer.iosb.ilt.sta.model.Actuator-main_actuator\" : 94,\n" +
                "  \"de.fraunhofer.iosb.ilt.sta.model.Sensor-main_sensor\" : 41,\n" +
                "  \"de.fraunhofer.iosb.ilt.sta.model.TaskingCapability-createNewVA\" : 47\n" +
                "}");
        writer.close();

        JarFileManager jfm = new JarFileManager();
        jfm.setActuatorDirectoryName(directory.toString());
        Assertions.assertTrue(jfm.destroyProcessWithActuatorId(94));

        idMapFile.delete();
        actDirectory.delete();
        directory.delete();
    }
    */
}