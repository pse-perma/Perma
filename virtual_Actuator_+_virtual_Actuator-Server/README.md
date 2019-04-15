PERMA Virtual Actuator
======================

This is an Actuator-implementation for OGC SensorThings. It is able to register with the SensorThings-Server
and provide TaskingCapabilities from JAR-extensions. Virtual Actuator is part of PERMA.


## Building

PERMA Virtual Actuator uses the FROST-Client branch FROST-Client-without_tasking_parameter_modelling. It is expected to reside in
the local maven repository of the build-system.
To build FROST-Client using maven, run  `$ mvn clean install`  in its main directory. 

PERMA Virtual Actuator is build using gradle. To build a distribution-set, simply run  `$ gradle installDist`
in the main directory (the same directory in which this README resides).

## Running

The generated archive should be outputted in virtual_actuator_app/build/install/. To run Virtual Actuator,
one of the scripts in bin/ can be used to start PERMA Virtual Actuator.

The application resp. the script expects exactly one command-line parameter, a path to the configuration file.

## Configuration

PERMA Virtual Actuator is configured by a configuration file in JSON format. Alongside basic properties
like the SensorThings-Server, the available TaskingCapability-JARs are referenced from here.
exampleConfig.json in this directory serves as an example.

Additionally, some basic options can be set using environment variables. The following are available:
  VA_HTTP_SERVER, VA_MQTT_SERVER, VA_MQTT_PREFIX


PERMA Virtual Actuator Server
=============================

The Virtual Actuator Server is a Virtual Actuator with a special Capability. An example configuration is
serverConfig-docker.json in this directory.
