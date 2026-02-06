FROM maven:3.8-eclipse-temurin-8 AS builder
WORKDIR /build
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline -B
COPY src ./src
COPY assets ./assets
RUN mvn package -DskipTests -B

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=builder /build/target/jadran-server.jar ./app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
