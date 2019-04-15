package com.github.pse_perma.perma.virtual_actuator.capability.example;

import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorCapability;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorContext;
import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;



public class ExampleCapability implements VirtualActuatorCapability {

    private static final String TASKING_PARAMETER_NAME_STATUS = "status";
    private Map<String, Object> parameters;

    @Override
    public TaskingCapability getCapability(VirtualActuatorContext context, Map<String, Object> parameters) {
        this.parameters = parameters;
        TaskingCapability nopCapability = new TaskingCapability(
                "light on/off capability", "Turns the light on or off.");

        HashMap<String, Object> taskingParameters = new HashMap<>();
        taskingParameters.put("type", "Boolean");
        taskingParameters.put("name", TASKING_PARAMETER_NAME_STATUS);
        taskingParameters.put("label", "light on/off status");
        nopCapability.setTaskingParameters(taskingParameters);

        return nopCapability;
    }


    @Override
    public TaskExecutionResult handleTask(Task task) throws InvalidParameterException {
        Map<String, Object> taskingParameters = task.getTaskingParameters();

        if (!taskParametersValid(taskingParameters)) {
            System.err.println("Parameters of a received Task are invalid!");
            return TaskExecutionResult.FAILURE;
        } else {
            boolean status = Boolean.parseBoolean((String) taskingParameters.get(TASKING_PARAMETER_NAME_STATUS));
            if (status) {
                Object on_msg = parameters.get("on_msg");
                System.out.println(on_msg instanceof String ? on_msg : "ExampleCapability: enabled");
            } else {
                Object off_msg = parameters.get("off_msg");
                System.out.println(off_msg instanceof String ? off_msg : "ExampleCapability: disabled");
            }
            return TaskExecutionResult.SUCCESS;
        }
    }

    private boolean taskParametersValid(Map<String, Object> taskingParameters) {
        Object rpStatus = taskingParameters.get(TASKING_PARAMETER_NAME_STATUS);
        if (rpStatus.getClass() != String.class) {
            return false;
        }
        String status = ((String) rpStatus).toLowerCase();
        if (!"true".equals(status) && !"false".equals(status)) {
            return false;
        }
        return true;
    }
}
