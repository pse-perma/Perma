package com.github.pse_perma.perma.virtual_actuator.app;

import com.github.pse_perma.perma.virtual_actuator.capability.CapabilityProvider;
import com.github.pse_perma.perma.virtual_actuator.capability.VirtualActuatorCapability;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;


public class JarCapabilityProvider implements CapabilityProvider {

    private ArrayList<VirtualActuatorCapability> capabilities;
    private Map<String, Object> parameters;


    public JarCapabilityProvider(File jarFile, Map<String, Object> parameters) throws IOException {
        this.parameters = parameters;

        URL[] urls = new URL[1];
        urls[0] = jarFile.toURI().toURL();
        URLClassLoader ucl = new URLClassLoader(urls);

        ServiceLoader<VirtualActuatorCapability> serviceLoader
                = ServiceLoader.load(VirtualActuatorCapability.class, ucl);

        this.capabilities = new ArrayList<>();
        for (VirtualActuatorCapability capability : serviceLoader) {
            this.capabilities.add(capability);
        }
    }


    @Override
    public List<ParameterizedCapability> getCapabilities() {
        ArrayList<ParameterizedCapability> capabilities = new ArrayList<>();
        for (VirtualActuatorCapability capability : this.capabilities) {
            capabilities.add(new ParameterizedCapability(capability, parameters));
        }
        return capabilities;
    }
}
