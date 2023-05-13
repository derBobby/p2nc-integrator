FROM openjdk:17-jdk-alpine

EXPOSE 8080

COPY target/PretixToNextcloudIntegrator-*.jar PretixToNextcloudIntegrator.jar

HEALTHCHECK --interval=30s --timeout=5s \
    CMD wget -q -O /dev/null http://localhost:8080/health || exit 1

ENTRYPOINT java -jar PretixToNextcloudIntegrator.jar

#CMD ["java", "-jar", "./SimpleJavaProject.jar"]