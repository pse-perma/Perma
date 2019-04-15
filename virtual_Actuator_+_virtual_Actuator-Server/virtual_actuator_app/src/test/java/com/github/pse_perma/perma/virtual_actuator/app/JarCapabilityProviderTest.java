package com.github.pse_perma.perma.virtual_actuator.app;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class JarCapabilityProviderTest {

    @Test
    public void getCapabilities() throws IOException {
        JarCapabilityProvider jarCapabilityProvider = new JarCapabilityProvider(
                new File("../va_capability_example/build/libs/va_capability_example.jar"),
                new HashMap<>());

        assertEquals(1, jarCapabilityProvider.getCapabilities().size());
    }
}