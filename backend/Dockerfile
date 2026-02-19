# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
COPY --from=build /home/app/target/*.jar app.jar
EXPOSE 41414
ENTRYPOINT ["java","-jar","/app.jar"]
