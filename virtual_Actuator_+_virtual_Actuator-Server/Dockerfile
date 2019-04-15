FROM openjdk:8-jdk-alpine
VOLUME /app
WORKDIR /app/
COPY virtual_actuator_app/build/install/virtual_actuator_app  virtual_actuator_app/build/install/virtual_actuator_app
COPY va_capability_server/build/libs/va_capability_server.jar va_capability_server/build/libs/
COPY serverConfig-docker.json .
RUN mkdir -p /app/data
ENTRYPOINT ["/app/virtual_actuator_app/build/install/virtual_actuator_app/bin/virtual_actuator_app", "serverConfig-docker.json"]
