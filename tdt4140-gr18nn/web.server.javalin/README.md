# Setup
Import the project in Eclipse/IntelliJ as a Maven project.

## Running tests
Either run tests directly from Eclipse/IntelliJ, or do `mvn test` in the root folder.
Each test will start its own instance of the app.

## Build/deploy
You can build a jar with dependencies by doing `mvn install` in the root folder.
The jar will be saved in the `target` folder, the exact file name should be 
`tdt4140-gr18nn.web.server.javalin-0.0.1-SNAPSHOT-jar-with-dependencies.jar`.

You can run this jar by doing 
`java -jar target/tdt4140-gr18nn.web.server.javalin-0.0.1-SNAPSHOT-jar-with-dependencies.jar` 
in the root folder.
