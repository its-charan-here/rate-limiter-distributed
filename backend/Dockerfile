FROM eclipse-temurin:21-jdk as build
WORKDIR /app
COPY . .
# Convert Windows line endings to Unix
RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 