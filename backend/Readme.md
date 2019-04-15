# PERMA Backend
This application is built using the Springboot framework (v2.1.3). Gradle was used to manage dependencies and build the app.

## How to build
Just run ```./gradlew jar``` (or ```./gradlew.bat jar```, if you are on Windows).
Make sure you have the FROST-Client-with_tasking_parameter_modelling version of the Frost-Client installed.

## How to run
Start the backend just like any java application by running ```java -jar backend-0.0.1.jar```. The file is located in the default Gradle build directory (```./build/libs/backend-0.0.1.jar```).

There is a script file (```./launch.sh```) which can be used on Unix systems to start up the backend and set up an initial SensorThings server to choose on the website.

This application has no graphical output; only logging on a console is available.

### How to configure SensorThings servers before startup
If not existing yet, create a file named ```servers.json``` in ```./config/```. The format is a JSON Array, similar to the following example:

```json
[ 
  { 
    "name":"Google",
    "url":"http://www.google.com"
  }, { 
    "name":"Bing",
    "url":"http://www.bing.com"
  } 
]
```

When making changes to the server list on the website, the file will be updated accordingly. Thus, you will always have an up-to-date list of servers, even when restarting the backend application.

## Docker
The backend can also be used within a docker container. The necessary ```Dockerfile``` is available. 

A ```docker-compose.yml``` file is in the root folder for testing the application with Test Containers (integration tests). It includes the other modules of the Perma system.
