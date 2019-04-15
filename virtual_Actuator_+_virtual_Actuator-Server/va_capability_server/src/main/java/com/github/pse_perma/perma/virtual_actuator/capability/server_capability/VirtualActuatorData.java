package com.github.pse_perma.perma.virtual_actuator.capability.server_capability;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static java.lang.Long.toHexString;


public class VirtualActuatorData {
    private static final String CONFIG_FILENAME = "config.json";

    private String stHttpServerUrl;
    private String stMqttServerUrl;
    private String mqttRessourcePathPrefix;
    private String actuatorName;
    private String actuatorDescription;
    private String thingName;
    private String thingDescription;
    private List<String> jarFileList;

    private static long magicNumber = 0xfedcba9876543210L;
    private long id;

    public VirtualActuatorData(String stHttpServerUrl, String stMqttServerUrl, String mqttRessourcePathPrefix,
                               String actuatorName, String actuatorDescription,
                               String thingName, String thingDescription) {
        this.stHttpServerUrl = stHttpServerUrl;
        this.stMqttServerUrl = stMqttServerUrl;
        this.mqttRessourcePathPrefix = mqttRessourcePathPrefix;

        this.actuatorName = actuatorName;
        this.actuatorDescription = actuatorDescription;
        this.thingName = thingName;
        this.thingDescription = thingDescription;

        this.jarFileList = null;
        id = createHash();
    }

    public String getStHttpServerUrl() {
        return stHttpServerUrl;
    }

    public void setStHttpServerUrl(String stHttpServerUrl) {
        this.stHttpServerUrl = stHttpServerUrl;
    }

    public String getStMqttServerUrl() {
        return stMqttServerUrl;
    }

    public void setStMqttServerUrl(String stMqttServerUrl) {
        this.stMqttServerUrl = stMqttServerUrl;
    }

    public String getMqttRessourcePathPrefix() {
        return mqttRessourcePathPrefix;
    }

    public void setMqttRessourcePathPrefix(String mqttRessourcePathPrefix) {
        this.mqttRessourcePathPrefix = mqttRessourcePathPrefix;
    }

    public String getActuatorName() {
        return actuatorName;
    }

    public void setActuatorName(String actuatorName) {
        this.actuatorName = actuatorName;
    }

    public String getActuatorDescription() {
        return actuatorDescription;
    }

    public void setActuatorDescription(String actuatorDescription) {
        this.actuatorDescription = actuatorDescription;
    }

    public String getThingName() {
        return thingName;
    }

    public void setThingName(String thingName) {
        this.thingName = thingName;
    }

    public String getThingDescription() {
        return thingDescription;
    }

    public void setThingDescription(String thingDescription) {
        this.thingDescription = thingDescription;
    }

    public List<String> getJarFileList() {
        return jarFileList;
    }

    public void setJarFileList(List<String> jarFileList) {
        this.jarFileList = jarFileList;
    }


    /**
     * Returns the working directory for this Actuator.
     * This directory is supposed to contain user-provided jar-capabilities, the generated configuration-file
     * and runtime-artifacts of the VirtualActuatorData.
     *
     * The directory is not guaranteed to exist yet.
     *
     * the path is relative to the working-directory of the Virtual Actuator Server.
     *
     * @return a File referring to the working directory.
     */
    public File getWorkingDirectory() {
        return new File("./actuators/" + this.actuatorName + "#" + getActuatorHashString());
    }

    // only for initialization in constructor
    private long createHash() {
        long result = magicNumber;
        magicNumber++;
        Random rand = new Random();

        result = 31 * result + rand.nextLong();
        result = 31 * result + (stHttpServerUrl != null ? stHttpServerUrl.hashCode() : 0);
        result = 31 * result + (stMqttServerUrl != null ? stMqttServerUrl.hashCode() : 0);
        result = 31 * result + (mqttRessourcePathPrefix != null ? mqttRessourcePathPrefix.hashCode() : 0);
        result = 31 * result + (actuatorName != null ? actuatorName.hashCode() : 0);
        result = 31 * result + (actuatorDescription != null ? actuatorDescription.hashCode() : 0);
        result = 31 * result + (thingName != null ? thingName.hashCode() : 0);
        result = 31 * result + (thingDescription != null ? thingDescription.hashCode() : 0);

        return result;
    }

    /**
     * create unique hexadecimal HashString, even if two Virtual Actuators have equivalent values
     * @return String with hexadecimal Hash.
     */
    public String getActuatorHashString(){
        return toHexString(id);
    }

    public void createWorkingDirectory() throws IOException {
        File wd = this.getWorkingDirectory();
        if(!wd.exists()) {
            if (!wd.mkdirs()) {
                throw new IOException(String.format("Failed to create working directory for %s.", this.actuatorName));
            }
        }
    }

    public File getConfigFile() {
        return new File(this.getWorkingDirectory().getAbsolutePath() + "/" + this.CONFIG_FILENAME);
    }
}
