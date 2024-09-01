FROM eclipse-temurin:21-jre-alpine
RUN apk update &&\
    apk add iproute2 nmap
WORKDIR /app
COPY *.jar .
ENTRYPOINT exec java -Xmx${HEAP:-512M} -jar *.jar
