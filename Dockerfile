FROM openjdk:8-jdk-alpine
MAINTAINER majorovaira8@gmail.com
COPY target/store-0.0.1.jar store.jar
ENTRYPOINT ["java","-jar","/store.jar"]