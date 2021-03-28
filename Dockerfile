FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 3086/tcp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
ADD /target/navi-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]