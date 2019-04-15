FROM openjdk:8-jdk-alpine
VOLUME /tmp
WORKDIR /tmp
COPY /build/libs/backend-0.0.1.jar .
COPY /config/servers.json config/
COPY /upload-dir .
COPY /launch.sh .

CMD ./launch.sh
#ENTRYPOINT ["java", "-jar", "backend-0.0.1.jar"]
