FROM maven:3 AS build
WORKDIR /workspace

COPY pom.xml .

RUN mvn -B dependency:go-offline

# Copy source code
COPY src ./src

RUN mvn -B clean package -DskipTests

FROM openjdk:21
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
