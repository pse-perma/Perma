package com.github.pse_perma.perma.virtual_actuator.capability.aggregator;

import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorCapability;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorContext;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class SetDatastreamCapability implements VirtualActuatorCapability {

    private static Logger logger = LoggerFactory.getLogger(SetDatastreamCapability.class);

    private VirtualActuatorContext context;

    // used in AggregationCapability
    static Datastream sourceDatastream = null;
    static Datastream destinationDatastream  = null;


    @Override
    public TaskingCapability getCapability(VirtualActuatorContext context, Map<String, Object> parameters) {
        this.context = context;

        // create capability
        TaskingCapability capability = new TaskingCapability("SetAggregatorDatastreamCapability",
                "Set the Datastream of which Obeservations shall be aggreated.");

        HashMap<String, Object> taskingParameters = new LinkedHashMap<>();
        taskingParameters.put("type", "Count");
        taskingParameters.put("name", "SourceDatastreamId");
        taskingParameters.put("label", "Source Datastream ID");
        taskingParameters.put("description", "ID of the Datastream from which Observations are to be be aggregated.");

        /*HashMap<String, Object> constraint = new LinkedHashMap<>();
        constraint.put("type", "AllowedValues");
        constraint.put("interval", new int[] {1, 1000});
        taskingParameters.put("constraint", constraint);*/

        capability.setTaskingParameters(taskingParameters);

        return capability;
    }


    @Override
    public TaskExecutionResult handleTask(Task task) throws InvalidParameterException {
        Map<String, Object> taskingParameters = task.getTaskingParameters();
        taskingParameters.get("");

        // get Sensor to aggregate
        try {
            Object datastreamId = taskingParameters.get("SourceDatastreamId");
            if (!(datastreamId instanceof Number)) {
                logger.error("AggregationCapability: Parameter datastream-id is not a number!");
                return TaskExecutionResult.FAILURE;
            }
            Long sid = ((Number) datastreamId).longValue();

            sourceDatastream = context.getVirtualActuatorAPI().getStService().datastreams().find(sid);

            // create Datastream
            destinationDatastream = new Datastream(
                    "AggregatorCapStream", "Datastream of Aggregation", "aggregations",
                    sourceDatastream.getUnitOfMeasurement());
            destinationDatastream.setSensor(context.getSensor());
            destinationDatastream.setThing(context.getCommonActuatorThing());

            destinationDatastream.setObservedProperty(sourceDatastream.getObservedProperty());
            destinationDatastream.setObservationType(sourceDatastream.getObservationType());

            destinationDatastream = context.getVirtualActuatorAPI().createOrUpdateEntity(destinationDatastream, "aggregator-" + sourceDatastream.getId());

            return TaskExecutionResult.SUCCESS;
        } catch (ServiceFailureException e) {
            logger.error("AggregationCapability: Failed to create entities: " + e.getMessage());
            e.printStackTrace();
            return TaskExecutionResult.FAILURE;
        }
    }
}
