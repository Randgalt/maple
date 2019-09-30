# Standard Usage Example

In this example we define a simple Logging Utility ([Logging.java](src/main/java/com/myco/app/logging/Logging.java)) that initializes
structured logging with some good defaults (including the `ModelFormatter`) and provides a factory method to return a structured logger instance with
the defined logging schema.

See:

- [Logging.java](src/main/java/com/myco/app/logging/Logging.java) - project-specific factory for structured logging
- [LoggingSchema.java](src/main/java/com/myco/app/logging/LoggingSchema.java) - a single logging schema for the project
- [RequestHandler.java](src/main/java/com/myco/app/request/RequestHandler.java) - structured logging example
- [UpdateService.java](src/main/java/com/myco/app/request/UpdateService.java) - structured logging example with composed logging. Also 
has an example of using [@DoNotLog](src/main/java/com/myco/app/request/PayloadModel.java) to prevent sensitive data from being logged.
