FROM maven:3.9.9-eclipse-temurin-21-alpine

# Устанавливаем gettext для envsubst
RUN apk add --no-cache gettext

WORKDIR /app

EXPOSE 8080

# Команда создаёт settings.xml при каждом запуске
CMD ["sh", "-c", "envsubst < /app/settings.xml.template > /root/.m2/settings.xml && mvn spring-boot:run -Dspring-boot.run.profiles=postgres -DskipTests"]
