FROM adoptopenjdk:8u292-b10-jdk-hotspot-focal

#MAINTAINER singleton.io@outlook.com

COPY i18n-service.jar app.jar
COPY l10n l10n

EXPOSE 8090
EXPOSE 8091

ENTRYPOINT ["java","-jar","/app.jar"]