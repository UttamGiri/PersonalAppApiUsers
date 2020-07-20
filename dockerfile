FROM openjdk:8-jdk-alpine 
VOLUME /tmp 
COPY target/PersonalAppApiUsers-0.0.1-SNAPSHOT.jar users-microservice.jar
#COPY encyptAPIKey3.jks encyptAPIKey3.jks
#COPY UnlimitedJCEPolicyJDK8/* /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/users-microservice.jar"] 