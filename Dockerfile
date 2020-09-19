FROM openjdk:11-jre
COPY build/libs/*.jar app.jar
COPY ./docker/dbinit.sh /usr/local/bin
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app.jar"]
