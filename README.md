# rocket-manager

This service is responsible for receiving and processing rocket state updates. It uses Kotlin with Ktor and is designed to handle JSON messages related to rocket state changes. The service processes these updates and keeps track of each rocket and their current state. The state of the rockets tracked by the system can be accessed by REST endpoints.

## Requirements
Before you can build and run the service, make sure you have the following tools installed:
- Java 17 (or newer)
- Gradle (or use the Gradle wrapper ./gradlew)
- Kotlin 1.8+

## Getting started
### 1. Clone the repository

### 2. Build the project and run the service using Gradle
In a terminal in the same folder where gradlew and gradlew.bat files are (./rocket-manager), run the following commands.

On Mac:
```bash
./gradlew build
./gradlew run
```
On Windows:
```bash
.\gradlew build
.\gradlew run
```
If the program successfully runs, you should see the following:
```
2025-04-07 02:55:22.656 [main] INFO  Application - Autoreload is disabled because the development mode is off.
2025-04-07 02:55:22.862 [main] INFO  Application - Application started in 0.229 seconds.
2025-04-07 02:55:22.976 [DefaultDispatcher-worker-1] INFO  Application - Responding at http://localhost:8088
```

### 3. Access endpoints
The service should now be running. You can post messages with updates to rockets on the following endpoint using Postman or a `curl`:

```
http://localhost:8088/messages
```
If the message is posted successfully, a response confirming successful processing will be returned.

Example data:
```
{
"metadata": {
"channel": "193270a9-c9cf-404a-8f83-838e71d9ae67",
"messageNumber": 1,    
"messageTime": "2022-02-02T19:39:05.86337+01:00",                                          
"messageType": "RocketLaunched"                             
},
"message": {                                                    
"type": "Falcon-9",
"launchSpeed": 500,
"mission": "ARTEMIS"
}
}
```

You can retrieve a list of all the rockets currently tracked by the system:
```
http://localhost:8088/rockets
```

Or get a specific rocket based on it's channel:
```
http://localhost:8088/rocket?id=193270a9-c9cf-404a-8f83-838e71d9ae67
```
## Testing
Building the project will always trigger the automated tests to run. The tests can also be manually run via
```bash
./gradlew test
```
For more details of the test results, add `--info` to the end of the command.