FROM adoptopenjdk:8u292-b10-jdk-hotspot-focal

MAINTAINER singleton.io@outlook.com

ADD i18n-service.jar app.jar

EXPOSE 8090
EXPOSE 8091

ENTRYPOINT ["java","-jar","/app.jar"]
