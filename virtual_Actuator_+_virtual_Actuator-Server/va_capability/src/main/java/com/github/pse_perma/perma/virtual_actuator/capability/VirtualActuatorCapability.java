package com.github.pse_perma.perma.virtual_actuator.capability;

import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;

import java.security.InvalidParameterException;
import java.util.Map;


public interface VirtualActuatorCapability {

    enum TaskExecutionResult {
        SUCCESS,
        FAILURE
    }

    public TaskingCapability getCapability(VirtualActuatorContext context, Map<String, Object> parameters);

    /**
     * Handles a Task for this Capability
     *
     * @throws InvalidParameterException if the Task does not match this Capability
     */
    public TaskExecutionResult handleTask(Task task) throws InvalidParameterException;
}
