package com.github.pse_perma.perma.virtual_actuator.capability.radio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorCapability;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorContext;
import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Map;


public class RadioCapability implements VirtualActuatorCapability {

    private static final String TASKING_PARAMETER_NAME_STATUS = "status";

    private static final ClassLoader CLASS_LOADER = RadioCapability.class.getClassLoader();
    private Clip clip;


    @Override
    public TaskingCapability getCapability(VirtualActuatorContext context, Map<String, Object> parameters) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            URL capabilityResource = CLASS_LOADER.getResource("TaskingCapability.json");
            if (capabilityResource == null) throw new IOException("TaskingCapability-resource could not be found.");

            URL url = CLASS_LOADER.getResource("radio.wav");
            clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip.open(ais);

            return mapper.readValue(capabilityResource, TaskingCapability.class);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("Audio-capability-initialisation failed!");
            e.printStackTrace();
            return null;
        }
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
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.stop();
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
