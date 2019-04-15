package com.github.pse_perma.perma.virtual_actuator.capability.aggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorCapability;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorContext;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Map;


public class AggregationCapability implements VirtualActuatorCapability {

    private static final ClassLoader CLASS_LOADER = AggregationCapability.class.getClassLoader();
    private static Logger logger = LoggerFactory.getLogger(AggregationCapability.class);

    private VirtualActuatorContext context;


    @Override
    public TaskingCapability getCapability(VirtualActuatorContext context, Map<String, Object> parameters) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL capabilityResource = CLASS_LOADER.getResource("TaskingCapability.json");
            if (capabilityResource == null) throw new IOException("TaskingCapability-resource could not be found.");

            return mapper.readValue(capabilityResource, TaskingCapability.class);
        } catch (IOException e) {
            System.out.println("Aggregator-capability-initialisation failed!");
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public TaskExecutionResult handleTask(Task task) throws InvalidParameterException {
        if (SetDatastreamCapability.destinationDatastream == null) {
            logger.warn("handleTask is called but the Datastream hasn't been set yet.");
            return TaskExecutionResult.FAILURE;
        }

        Map<String, Object> taskingParameters = task.getTaskingParameters();
        Object aggregationType = taskingParameters.get("aggregationType");
        if (!(aggregationType instanceof String)) {
            logger.warn("aggregationType of an DatastreamAggregationCapability is invalid.");
            return TaskExecutionResult.FAILURE;
        }

        int aggrType;

        switch ((String) aggregationType) {
            case "Average": aggrType = 1; break;
            case "Sum":     aggrType = 2; break;

            default:
                logger.warn("aggregationType of an DatastreamAggregationCapability has an invalid value.");
                return TaskExecutionResult.FAILURE;
        }

        try {
            EntityList<Observation> observations =
                    //sourceDatastream.observations().query().filter("Actuator/id eq '73'").list();
                    SetDatastreamCapability.sourceDatastream.observations().query().list();

            double sum = 0;
            int c = 0;
            for (Observation observation : observations) {
                Object result = observation.getResult();
                if (result instanceof Number) {
                    sum += ((Number) result).doubleValue();
                    c++;
                } else {
                    logger.error("Aggregator: Invalid value - unexpected type {}. Value: '{}'.",
                            result.getClass(), result);
                    return TaskExecutionResult.FAILURE;
                }
            }

            double avg = sum / c;
            SetDatastreamCapability.destinationDatastream.observations().create(
                    new Observation(aggrType == 1 ? avg : sum, SetDatastreamCapability.destinationDatastream));

            return TaskExecutionResult.SUCCESS;

        } catch (ServiceFailureException e) {
            logger.error("Error while handling an aggregation-Task: Server-communication failed: " + e.getMessage());
            e.printStackTrace();
            return TaskExecutionResult.FAILURE;
        }
    }
}
