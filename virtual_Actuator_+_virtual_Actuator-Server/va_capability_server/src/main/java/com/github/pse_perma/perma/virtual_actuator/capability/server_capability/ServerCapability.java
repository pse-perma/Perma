package com.github.pse_perma.perma.virtual_actuator.capability.server_capability;

import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorCapability;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorContext;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;


public class ServerCapability implements VirtualActuatorCapability {
    final Logger logger = LoggerFactory.getLogger(ServerCapability.class);
    final static String HANDLE_TASK = "handleTask";
    private VirtualActuatorContext context;
    private JarFileManager jarFileManager = new JarFileManager();

    @Override
    public TaskingCapability getCapability(VirtualActuatorContext context, Map<String, Object> parameters) {
        this.context = context;

        // TODO: it might make more sense to load the capability-definition from a json-resource

        TaskingCapability taskingCapability;
        taskingCapability = new TaskingCapability(
                "createNewVA",
                "Virtual Actuator Server, starts new Virtual Actuators");

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

        Map<String, Object> capParameters = new LinkedHashMap<>();
        capParameters.put("type", "DataRecord");
        capParameters.put("field", parameterCollection);

        taskingCapability.setTaskingParameters(capParameters);
        logger.debug("ServerCapability.getCapability() invoked");

        return taskingCapability;
    }

    @Override
    public TaskExecutionResult handleTask(Task task) throws InvalidParameterException {
        final TaskingCapability taskingCapability;

        try {
            taskingCapability = task.getTaskingCapability();
        } catch (ServiceFailureException e) {
            logger.error("ServerCapability::handleTask() : can't get taskingCapability");
            return TaskExecutionResult.FAILURE;
        }
        if (taskingCapability == null) {
            logger.error("ServerCapability::handleTask() : can't get taskingCapability");
            return TaskExecutionResult.FAILURE;
        }

        String taskName = taskingCapability.getName();
        if (taskName.equals("createNewVA")) {
            logger.debug("ServerCapability::handleTask() : createNewVA task recognized");
            return handleTaskCreateNewVA(task);
        }
        else {
            logger.error("ServerCapability::handleTask() : didn't recognize TaskingCapabilty \"{}\"", taskName);
            return TaskExecutionResult.FAILURE;
        }
    }

    private TaskExecutionResult handleTaskCreateNewVA(Task task) throws InvalidParameterException {
        assert (context != null); // getCapability must be invoked first and set context
        assert (task != null);

        ConfigCompose configComposer;

        Map<String, Object> taskingParameters = task.getTaskingParameters();
        if (taskingParameters == null) {
            logger.error("TaskingParameters were null");
            return TaskExecutionResult.FAILURE;
        }

        String stHttpServerUrl = context.getConfig().getStHttpServerUrl().toString();
        String stMqttServerUrl = context.getConfig().getStMqttServerUrl();
        String mqttRessourcePathPrefix = context.getConfig().getMqttRessourcePathPrefix();

        String actuatorName = (String) taskingParameters.get("vaName");
        String actuatorDescription = (String) taskingParameters.get("vaDescription");
        String thingName = (String) taskingParameters.get("thingName");
        String thingDescription = (String) taskingParameters.get("thingDescription");
        String jarFileString = (String) taskingParameters.get("capabilityList");

        // TODO: do error-handling for invalid tasks in Virtual Actuator / FROST-Client?
        if (actuatorName == null || actuatorName.isEmpty()) {
            logger.error("Failed to read vaName");
            return TaskExecutionResult.FAILURE;
        }
        if (actuatorDescription == null || actuatorDescription.isEmpty()) {
            logger.error("Failed to read vaDescription");
            return TaskExecutionResult.FAILURE;
        }
        if (thingName == null || thingName.isEmpty()) {
            logger.error("Failed to read thingName");
            return TaskExecutionResult.FAILURE;
        }
        if (thingDescription == null || thingDescription.isEmpty()) {
            logger.error("Failed to read thingDescription");
            return TaskExecutionResult.FAILURE;
        }
        if (jarFileString == null) {
            logger.debug("Failed to read capabilityList");
            return TaskExecutionResult.FAILURE;
        }

        VirtualActuatorData virtualActuatorData = new VirtualActuatorData (
                stHttpServerUrl,
                stMqttServerUrl,
                mqttRessourcePathPrefix,
                actuatorName,
                actuatorDescription,
                thingName,
                thingDescription
        );
        File workingDirectory = virtualActuatorData.getWorkingDirectory();

        List<String> serverJarFileList;
        List<String> localJarFileList = new LinkedList<>();
        String localJarFileName;

        if (jarFileString.isEmpty()) {
            serverJarFileList = new LinkedList<>();
        } else {
            serverJarFileList = Arrays.asList(jarFileString.split(";"));
            for (String foreignJarFile : serverJarFileList) {
                int lastIndexOf = foreignJarFile.lastIndexOf("/");
                localJarFileName = foreignJarFile.substring(lastIndexOf + 1);
                localJarFileList.add(localJarFileName);
            }
        }

        virtualActuatorData.setJarFileList(localJarFileList);

        try {
            virtualActuatorData.createWorkingDirectory();
        } catch (IOException e) {
            logger.error("ServerCapability.handleTask(): error creating the working directory: " + e.getMessage());
            return TaskExecutionResult.FAILURE;
        }

        Iterator<String> itServer = serverJarFileList.iterator();
        Iterator<String> itLocal = localJarFileList.iterator();

        while (itServer.hasNext() && itLocal.hasNext()) {
            String serName = itServer.next();
            String locName = itLocal.next();

            if (!JarFileManager.downloadJarFile(serName,
                    workingDirectory.getAbsolutePath() + "/" + locName)) {
                logger.error("ServerCapability.handleTask(): failed to download file: " + serName);
                return TaskExecutionResult.FAILURE;
            }
        }

        configComposer = new ConfigCompose();
        configComposer.setValues(
                stHttpServerUrl,
                stMqttServerUrl,
                mqttRessourcePathPrefix,
                actuatorName,
                actuatorDescription,
                localJarFileList,
                thingName,
                thingDescription
        );

        try {
            configComposer.composeFile(virtualActuatorData.getConfigFile());
            jarFileManager.addAndExecuteVirtualActuator(virtualActuatorData);

            logger.debug("ServerCapability.handleTask(): handled Task successfully");
            return TaskExecutionResult.SUCCESS;
        } catch (IOException e) {
            logger.error("ServerCapability.handleTask(): Virual Actuator jar execution failed.");
            return TaskExecutionResult.FAILURE;
        }
    }
}