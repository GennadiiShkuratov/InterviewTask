# Application Overview

## Introduction

Before delving into the specifics of the application, I would like to clarify that the primary objective of implementing this task was to showcase certain aspects of expertise. It was not intended to be a production-ready solution, even for internal use. As there are many gaps what not solved in this task such as:
1) there are no security;
2) no logging;
3) data race condition not solved;
4) raw Rest API need to be review;
5) Also model does not follow KISS and YAGNI principles, considering simplicity of task requirements. It was designed this way just to show my OOP and design understanding;

Also, It is worth noting that the development process exceeded four hours, primarily due to the creation of the webpage and the REST API and integrate adding persistence level.

## Extra documentation
Classes diagram included in project. For better readability, I removed from picture all supportive classes and left just core. File: /ClassDiagram.png 

## Installation Instructions

To run the application, please follow these steps:

1. **Install Docker**
2. **Run the Application** - Execute the following command from the root directory: 

docker run -d -p 8080:8080 device-booking

## UI
After successful start application will be available by URL: http://localhost:8080/


## Additional Setup

Upon starting, the application pre-fills data to achieve the initial state required for the task. This pre-filling behavior is controlled by the application property `phone-store.prefill`, which is set to `true` by default (alternatively, it can be set to `false`).

## Database Configuration

The application utilizes an H2 Database.

- **Access Database Console**: Use the URL `http://localhost:8080/h2-console`.
- **Data Persistence Configuration**: To ensure data persistence (durability), specify the path to the storage location on your computer by setting the Spring datasource URL using `spring.datasource.url` property to app when run:
  -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:h2:file:C:/H2DB/DeviceBookingApp;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE"

Alternatively, configure this in the `application.properties` file.

## API Documentation

The application features OpenAPI documentation, accessible at `http://localhost:8080/swagger-ui/index.html`. This documentation can be used to review the REST API. Additionally, the Swagger interface allows for the addition of new phones or the removal of existing ones as needed.
