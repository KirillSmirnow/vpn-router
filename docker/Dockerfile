FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY *.jar .
ENTRYPOINT exec java -Xmx512M -Dspring.profiles.active=$PROFILE -jar *.jar
