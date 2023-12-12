FROM eclipse-temurin:21-jre-jammy

EXPOSE 8080

COPY target/p2nc-integrator-*.jar p2nc-integrator.jar

HEALTHCHECK --interval=15s --timeout=15s --retries=3 \
    CMD wget -q -O /dev/null http://localhost:8080/health || exit 1

ENTRYPOINT java -jar p2nc-integrator.jar

#CMD ["java", "-jar", "./SimpleJavaProject.jar"]