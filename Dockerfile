FROM openjdk:20-jdk-alpine

EXPOSE 8080

COPY target/PretixToNextcloudIntegrator-*.jar PretixToNextcloudIntegrator.jar

ENTRYPOINT java -jar PretixToNextcloudIntegrator.jar

#CMD ["java", "-jar", "./SimpleJavaProject.jar"]