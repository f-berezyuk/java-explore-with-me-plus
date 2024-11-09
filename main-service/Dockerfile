# Указываем базовый образ с JDK 21
FROM openjdk:21-jdk-slim

# Настраиваем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем собранный JAR файл в контейнер
COPY target/main-service.jar /app/main-service.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "/app/main-service.jar"]