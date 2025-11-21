FROM registry.pantavanij.com:8444/openjdk:11.0.9-jre
COPY target/ptvn-sourcing-req-service-1.0.jar  /opt/java/openjdk/japp.jar
#test
CMD ["java", "-jar", "/opt/java/openjdk/japp.jar"]
