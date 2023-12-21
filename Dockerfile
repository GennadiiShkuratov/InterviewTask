FROM maven:3.8.4-openjdk-17 as build
COPY src /usr/src/DeviceBooking/src
COPY pom.xml /usr/src/DeviceBooking
WORKDIR /usr/src/DeviceBooking
RUN mvn clean package

FROM openjdk:17-slim
COPY --from=build /usr/src/DeviceBooking/target/*.jar /usr/app/DeviceBooking.jar
WORKDIR /usr/app
CMD ["java", "-jar", "DeviceBooking.jar"]
