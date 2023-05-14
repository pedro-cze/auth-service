FROM openjdk:11-jre
COPY build/libs/*.jar app.jar
COPY dbinit.sh /usr/local/bin
EXPOSE 8083
ENTRYPOINT ["java", "-Dspring.profiles.active=prod" ,"-jar","/app.jar"]
