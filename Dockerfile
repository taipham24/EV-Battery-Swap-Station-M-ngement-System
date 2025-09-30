# ===== STAGE 1: Build với Maven + JDK =====
FROM maven:3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ===== STAGE 2: Runtime (chạy app) =====
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/ev_battery_swap-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

