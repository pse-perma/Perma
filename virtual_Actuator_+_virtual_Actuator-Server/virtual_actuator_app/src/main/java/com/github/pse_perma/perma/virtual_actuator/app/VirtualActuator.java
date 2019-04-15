package com.github.pse_perma.perma.virtual_actuator.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pse_perma.perma.virtual_actuator.capability.*;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.jackson.ObjectMapperFactory;
import de.fraunhofer.iosb.ilt.sta.model.*;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VirtualActuator {

    private static Logger logger = LoggerFactory.getLogger(VirtualActuator.class);

    private static final String TASK_STATUS_KEY = "task_status";
    private SensorThingsService stService;
    private MqttClient mqttClient;

    private VirtualActuatorConfig config;
    private Actuator actuator = null;
    private Sensor sensor = null;
    private Thing commonThing = null;
    private VirtualActuatorAPI vaAPI;
    private VirtualActuatorContext context;

    Map<VirtualActuatorCapability, TaskingCapability> capabilities = new HashMap<>();


    /**
     * Initializes a new Virtual Actor by parsing the provided configFile.
     * Neither loads the TaskingCapabilities from JARs nor registers with the SensorThings-Server.
     *
     * @param config Configuration for the {@link VirtualActuator}
     * @throws IOException If the provided configuration-file is invalid
     */
    public VirtualActuator(VirtualActuatorConfig config) throws IOException {
        this.config = config;
        this.stService = new SensorThingsService(this.config.getStHttpServerUrl());
        vaAPI = new VirtualActuatorAPI(this.config, this.stService);
    }


    public void registerBaseEntities() throws IOException {
        try {
            // main entities
            vaAPI.createOrUpdateEntity(config.getConfigActuator(), VirtualActuatorConfig.ACTUATOR_ENTITY_INTERNAL_NAME);
            vaAPI.createOrUpdateEntity(config.getConfigSensor(), VirtualActuatorConfig.SENSOR_ENTITY_INTERNAL_NAME);

            // again for actuator-sensor-link  TODO: do this only if necessary
            this.actuator = vaAPI.createOrUpdateEntity(
                    config.getConfigActuator(), VirtualActuatorConfig.ACTUATOR_ENTITY_INTERNAL_NAME);
            this.sensor = vaAPI.createOrUpdateEntity(
                    config.getConfigSensor(), VirtualActuatorConfig.SENSOR_ENTITY_INTERNAL_NAME);

            // locations for main_thing
            List<Location> locations = config.getLocations();
            for (int i = 0; i < locations.size(); i++) {
                Location location = locations.get(i);
                locations.set(i,
                        vaAPI.createOrUpdateEntity(location, "thing_location_" + location.getName()));
            }
            EntityList<Location> locationEntityList = new EntityList<>(EntityType.LOCATIONS);
            locationEntityList.addAll(locations);

            Thing thing = config.getConfigThing();
            thing.setLocations(locationEntityList);
            this.commonThing = vaAPI.createOrUpdateEntity(thing, VirtualActuatorConfig.THING_ENTITY_INTERNAL_NAME);
        }
        catch (ServiceFailureException e) {
            throw new IOException(e);
        }

        config.writeIdMap();
        context = new VirtualActuatorContext(this.vaAPI, this.config, this.actuator, this.sensor, this.commonThing);
    }


    public void loadCapabilities() {
        capabilities.clear();

        logger.debug(this.config.getCapabilityProviders().size() > 0
                ? "Loading the following Capabilities from JARs:"
                : "No Capabilities in JARs found.");
        for (CapabilityProvider capabilityProvider : this.config.getCapabilityProviders()) {
            for (CapabilityProvider.ParameterizedCapability parameterizedCapability
                    : capabilityProvider.getCapabilities()) {
                TaskingCapability taskingCapability = null;
                try {
                    taskingCapability = parameterizedCapability.getCapability()
                            .getCapability(context, parameterizedCapability.getParameters());
                } catch (Throwable ignored) { }  // robust handling of exceptions which might occur in Capability-JARs

                if (taskingCapability != null) {
                    logger.debug("  * " + taskingCapability.getName());
                    this.capabilities.put(parameterizedCapability.getCapability(), taskingCapability);
                } else {
                    logger.warn("Failed to load a capability.");
                }
            }
        }
    }

    public void registerCapabilities() throws IOException {
        try {
            // capabilities
            for (TaskingCapability capability : capabilities.values()) {
                capability.setActuator(this.actuator);
                // TODO: allow for custom things for capabilities
                capability.setThing(this.commonThing);

                TaskingCapability serverCapability = vaAPI.createOrUpdateEntity(capability, capability.getName());
                // TODO: find a better way to update the capability
                capability.setId(serverCapability.getId());
            }
        }
        catch (ServiceFailureException e) {
            throw new IOException(e);
        }

        config.writeIdMap();
    }


    private void handleTask(Task task) {
        if (task.getId() == null) {
            logger.error("handleTask() was called with a Task without an ID.");
            return;
        }

        if (task.getTaskingParameters() == null) {
            task.setTaskingParameters(new HashMap<>());
        }
        Object status = task.getTaskingParameters().get(TASK_STATUS_KEY);
        if (status != null) {
            if (status.getClass() != String.class) {
                logger.error("handleTask() was called with a Task with an invalid status.");
                return;
            } else {
                switch ((String) status) {
                    case "NEW":
                        break;  // handle the task

                    case "RUNNING":
                    case "SUCCESS":
                    case "FAILURE":
                        return;

                    default:
                        logger.error("handleTask() was called with a Task with an invalid status.");
                        return;
                }
            }
        }

        TaskingCapability taskingCapability = null;
        try {
            taskingCapability = task.getTaskingCapability();
        } catch (ServiceFailureException e) {
            logger.error("Error handling task: could not retrieve TaskingCapability.");
            return;
        }

        // TODO: use inverted map?
        for (Map.Entry<VirtualActuatorCapability, TaskingCapability> entry : capabilities.entrySet()) {
            if (taskingCapability.getId().equals(entry.getValue().getId())) {
                logger.info("Received task for TaskingCapability '{}'.", taskingCapability.getName());

                // TODO: get possible statuses from common enum
                task.getTaskingParameters().put(TASK_STATUS_KEY, "RUNNING");

                try {
                    vaAPI.createOrUpdateEntity(task, null);
                } catch (ServiceFailureException e) {
                    logger.error("Error while updating Task-state: " + e.getMessage());
                }

                VirtualActuatorCapability.TaskExecutionResult executionResult;
                try {
                    executionResult = entry.getKey().handleTask(task);
                } catch (Throwable e) {  // robust handling of exceptions which might occur in Capability-JARs
                    logger.warn("Capability threw the following exception while handling a task:");
                    e.printStackTrace();
                    executionResult = VirtualActuatorCapability.TaskExecutionResult.FAILURE;
                }
                task.getTaskingParameters().put(TASK_STATUS_KEY,
                        executionResult == null ?
                                VirtualActuatorCapability.TaskExecutionResult.FAILURE : executionResult.name());
                try {
                    vaAPI.createOrUpdateEntity(task, null);
                } catch (ServiceFailureException e) {
                    logger.error("Error while updating Task-state: " + e.getMessage());
                }
                return;
            }
        }

        // no matching capability found
        logger.warn("Could not handle task for unknown TaskingCapability '{}'.", taskingCapability.getName());
    }


    public void startMqttListening() throws MqttException {
        if (actuator == null)
            throw new IllegalStateException("No actuator registered before startMqttListening is called.");

        try {
            mqttClient = new MqttClient(this.config.getStMqttServerUrl(),
                    this.actuator.getName() + this.actuator.getId());
        } catch (MqttException e) {
            throw new MqttException(new InvalidParameterException("Invalid MQTT-Server-URL."));
        }

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttClient.connect(mqttConnectOptions);

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                logger.info("MQTT-connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                final ObjectMapper mapper = ObjectMapperFactory.get();
                Task task = mapper.readValue(message.getPayload(), Task.class);
                task.setService(VirtualActuator.this.stService);

                VirtualActuator.this.handleTask(task);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) { }
        });


        // subscribe tasks for all capabilities
        for (TaskingCapability capability : capabilities.values()) {
            String topicFilter = String.format("%s%s(%s)/%s",
                    this.config.getMqttRessourcePathPrefix(), EntityType.TASKING_CAPABILITIES.getName(),
                    capability.getId(), EntityType.TASKS.getName());

            logger.info("Subscribing for " + topicFilter);
            mqttClient.subscribe(topicFilter);
        }
    }



    public static void main(String[] args) {
        if (args.length != 1) {
            logger.error("Invalid parameters - exactly one parameter, the path to the config-file, must be provided.");
            return;
        }

        VirtualActuator virtualActuator;
        try {
            ConfigFactory configFactory = new ConfigFactory();
            configFactory.parseFile(new File(args[0]));
            configFactory.loadFromEnvironment();
            VirtualActuatorConfig config = configFactory.build();

            virtualActuator = new VirtualActuator(config);
        } catch (IOException | ConfigFactory.InvalidConfig e) {
            logger.error("The provided configuration-file is invalid: " + e.getMessage());
            return;
        }

        try {
            virtualActuator.registerBaseEntities();
        } catch (IOException e) {
            logger.error("Could not register at Server: " + e.getMessage());
            return;
        }

        virtualActuator.loadCapabilities();

        try {
            virtualActuator.registerCapabilities();
        } catch (IOException e) {
            logger.error("Could not register at Server: " + e.getMessage());
            return;
        }

        try {
            virtualActuator.startMqttListening();
            System.out.println();
        } catch (MqttException e) {
            logger.error("Could not start the MQTT-listener: " + e.getMessage());
            e.printStackTrace();
            return;
        }


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("Exiting...");
                virtualActuator.mqttClient.disconnect();
                virtualActuator.mqttClient.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }));

        // main thread exits here, mqttClient persists in its own thread waiting for messages and processing them
    }
}
