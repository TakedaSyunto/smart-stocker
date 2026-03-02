# Build ステージ
FROM eclipse-temurin:21-jdk-jammy AS build
COPY . .
RUN ./gradlew bootJar --no-daemon

# Run ステージ
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]