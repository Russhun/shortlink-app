FROM openjdk:11
LABEL maintainer="artemy.saltsin@gmail.com"
VOLUME /tmp
EXPOSE 8080
ADD target/shortlinks-0.0.1-SNAPSHOT.jar shortlinks-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","shortlinks-0.0.1-SNAPSHOT.jar"]