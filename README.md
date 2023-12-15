# Application Overview

## Introduction

Before delving into the specifics of the application, I wish to clarify that the primary objective of developing this project was to showcase certain elements of my expertise. It was not intended to be a production-ready solution, even for internal use. Consequently, there are several limitations, including the absence of logging, basic security measures, and a rudimentary user interface. However, I am confident that this application will serve its purpose as a discussion piece for an interview, allowing for an in-depth conversation about its design and potential solutions.

Additionally, it is worth noting that the development process exceeded four hours, primarily due to the creation of the webpage and the REST API.

## Installation Instructions

To run the application, please follow these steps:

1. **Install Java Runtime Environment (JRE)** - Version 17 or higher is required.
2. **Configure Environment Variables**:
    - Add the Java `bin` directory to the `PATH` system environment variable.
    - Create a `JAVA_HOME` system environment variable.
3. **Check out the Code** - Obtain the code from the repository.
4. **Run the Application** - Execute the following command from the root directory: 

mvn spring-boot:run

## UI
After successful start application will be available by URL: http://localhost:8080/


## Additional Setup

Upon starting, the application pre-fills data to achieve the initial state required for the task. This pre-filling behavior is controlled by the application property `phone-store.prefill`, which is set to `true` by default (alternatively, it can be set to `false`).

## Database Configuration

The application utilizes an H2 Database.

- **Access Database Console**: Use the URL `http://localhost:8080/h2-console`.
- **Data Persistence Configuration**: To ensure data persistence (durability), specify the path to the storage location on your computer by setting the Spring datasource URL using `spring.datasource.url` property to app when run:
  mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:h2:file:C:/H2DB/DeviceBookingApp;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE"

Alternatively, configure this in the `application.properties` file.

## API Documentation

The application features OpenAPI documentation, accessible at `http://localhost:8080/swagger-ui/index.html`. This documentation can be used to review the REST API. Additionally, the Swagger interface allows for the addition of new phones or the removal of existing ones as needed.
