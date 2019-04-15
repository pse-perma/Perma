package com.github.pse_perma.perma.virtual_actuator.capability;

import de.fraunhofer.iosb.ilt.sta.model.Actuator;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;


public class VirtualActuatorContext {

    private VirtualActuatorAPI virtualActuatorAPI;

    private VirtualActuatorConfig config;
    private Actuator actuator;
    private Sensor sensor;
    private Thing commonActuatorThing;


    public VirtualActuatorContext(VirtualActuatorAPI virtualActuatorAPI, VirtualActuatorConfig config,
                                  Actuator actuator, Sensor sensor, Thing commonActuatorThing) {
        this.virtualActuatorAPI = virtualActuatorAPI;

        this.config = config;
        this.actuator = actuator;
        this.sensor = sensor;
        this.commonActuatorThing = commonActuatorThing;
    }


    public VirtualActuatorAPI getVirtualActuatorAPI() {
        return virtualActuatorAPI;
    }

    public Actuator getActuator() {
        return actuator;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public VirtualActuatorConfig getConfig() {
        return config;
    }

    public Thing getCommonActuatorThing() {
        return commonActuatorThing;
    }
}
