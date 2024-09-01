FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY *.jar .
ENTRYPOINT exec java -Xmx${HEAP:-512M} -jar *.jar
