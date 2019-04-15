#!/usr/bin/env bash

# Requirements for running this script:
#    docker, maven


# build backend
cd FROST-Client/FROST-Client-with_tasking_parameter_modelling/
mvn install
cd ../../backend/
./gradlew build --exclude-task test
sudo docker build --tag perma-backend .
cd ..

# build virtual actuator + virtual actuator server
cd FROST-Client/FROST-Client-without_tasking_parameter_modelling/
mvn install
cd ../../virtual_Actuator_+_virtual_Actuator-Server/
./gradlew installDist
./gradlew jar
sudo docker build --tag perma-vas .
cd ..

# build frontend
cd frontend/
sudo docker build --tag perma-frontend .
cd ..
