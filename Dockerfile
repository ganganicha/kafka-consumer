# define base docker image
FROM openjdk:11
LABEL maintainer="gangani-kulathilaka"
ADD target/kafka-consumer-challenge.jar kafka-consumer-docker.jar
ENTRYPOINT ["java","-jar", "kafka-consumer-docker.jar"]