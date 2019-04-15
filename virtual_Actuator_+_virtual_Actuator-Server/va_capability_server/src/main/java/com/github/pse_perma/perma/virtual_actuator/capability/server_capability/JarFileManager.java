package com.github.pse_perma.perma.virtual_actuator.capability.server_capability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JarFileManager {
    // TODO: should better be load from json-config
    // relative to working directory of VAS
    private static final String VIRTUAL_ACTUATOR_EXECUTABLE
            = "./virtual_actuator_app/build/install/virtual_actuator_app/bin/virtual_actuator_app";
    private static final String CANONICAL_ACTUATOR_NAME = "de.fraunhofer.iosb.ilt.sta.model.Actuator";
    private static final String ACTUATOR_NAME = VirtualActuatorConfig.ACTUATOR_ENTITY_INTERNAL_NAME;
    private static final String ID_MAP_JSON_FILE_NAME = "idMap.json";
    private String actuatorDirectoryName = null;

    final Logger logger = LoggerFactory.getLogger(JarFileManager.class);

    private static JarFileManager jarFileManager;

    private Map<VirtualActuatorData, Process> actuatorProcessMap;

    private String javaExecutable;


    public JarFileManager() {
        this(new File(VIRTUAL_ACTUATOR_EXECUTABLE).getAbsolutePath());
    }

    public JarFileManager(String executable) {
        this.javaExecutable = executable;
        actuatorProcessMap = new HashMap<>();
    }

    public Process addAndExecuteVirtualActuator(VirtualActuatorData virtualActuatorData) throws IOException {
        Process process;
        ProcessBuilder processBuilder;

        processBuilder = new ProcessBuilder(javaExecutable, virtualActuatorData.getConfigFile().getAbsolutePath());
        processBuilder.directory(virtualActuatorData.getWorkingDirectory());

        // Remove inheritIO() when need getOutputStream, getInputStream or getErrorStream;
        // inheritIO(): passes IO from forked process to main process.
        // Avoids Buffer overflows in the subprocess which consequent in block or deadlock
        processBuilder.inheritIO();
        process = processBuilder.start();
        actuatorProcessMap.put(virtualActuatorData, process);
        return process;
    }

    /**
     * Create a map that maps from Virtual Actuator Id to the Virtual Actuator Directory
     * @return Virtual Actuator Directory and the corresponding Task-ID
      */
    public Map<Integer, File> getVAIdToDirectoryMap() {
        assert (actuatorDirectoryName != null);

        ObjectMapper mapper = new ObjectMapper();
        Map<Integer, File> returnMap = new LinkedHashMap<>();
        String actuatorEntryName = String.format("%s-%s", CANONICAL_ACTUATOR_NAME, ACTUATOR_NAME);
        File actuatorDirectory = new File(actuatorDirectoryName);
        File[] actuatorDirs = actuatorDirectory.listFiles();

        if (actuatorDirs != null) {
            for (File actuatorDir : actuatorDirs) {
                if (!actuatorDir.isDirectory()) continue;
                File idMapFile = new File(actuatorDir, ID_MAP_JSON_FILE_NAME);

                if (idMapFile.exists()) {
                    Map<String, Integer> idMap = null;
                    try {
                        idMap = mapper.readValue(idMapFile, Map.class);
                    } catch (IOException e) {
                        continue;
                    }
                    final Integer actuatorId = idMap.get(actuatorEntryName);
                    returnMap.put(actuatorId, actuatorDir);
                }
            }
        }
        return returnMap;
    }

    public void setActuatorDirectoryName(String actuatorDirectoryName) {
        this.actuatorDirectoryName = actuatorDirectoryName;
    }

    /**
     * Download a jar-file, or other file from http-Server
     * @param url Resource location
     * @param fileName The local filename to store
     * @return true if download correct or false if server-file not found or if local store not possible
     */
    public static boolean downloadJarFile(String url, String fileName) {
        boolean isDownloadSuccessfull;

        try {
            InputStream in = new URL(url).openStream();
            Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
            isDownloadSuccessfull = true;
        } catch (IOException e) {
            isDownloadSuccessfull = false;
        }

        return isDownloadSuccessfull;
    }
}
