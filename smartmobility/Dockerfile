FROM java:8
EXPOSE 3000
ADD target/*.jar /app.jar
VOLUME ["/app"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]