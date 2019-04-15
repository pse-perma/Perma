package com.github.pse_perma.perma.virtual_actuator.capability;

import java.util.List;
import java.util.Map;



public interface CapabilityProvider {

    public static class ParameterizedCapability {
        private VirtualActuatorCapability capability;
        private Map<String, Object> parameters;


        public ParameterizedCapability(VirtualActuatorCapability capability, Map<String, Object> parameters) {
            this.capability = capability;
            this.parameters = parameters;
        }


        public VirtualActuatorCapability getCapability() {
            return capability;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }
    }



    public List<ParameterizedCapability> getCapabilities();
}
