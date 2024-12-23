# Use an official openjdk base image
#FROM openjdk:17-jdk-slim as runtime
FROM openjdk:17-jdk-slim as build

# Set the working directory
WORKDIR /app

# Copy the local jar file into the container
#COPY build/libs/MyFirstRestfulApi.jar app.jar
# Copy the application JAR into the container
ARG JAR_FILE=build/libs/UserBankApi.jar
COPY ${JAR_FILE} app.jar
# Expose the port your Spring Boot application will run on
EXPOSE 8080

# Command to run your app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
