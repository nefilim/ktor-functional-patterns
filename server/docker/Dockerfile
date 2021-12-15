FROM openjdk:11-jre

ADD build/libs/server*all.jar ktor-patterns.jar
ADD src/main/resources/application-container.conf /
ADD ./docker/docker-entrypoint.sh /docker-entrypoint.sh

RUN apt-get update && apt-get install -y procps

EXPOSE 8080/tcp

ENTRYPOINT ./docker-entrypoint.sh /ktor-patterns.jar
