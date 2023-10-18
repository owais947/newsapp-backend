#base docker image
#FROM registry.cmd.navi-tech.in/common/maven:3-jdk-11
FROM 571315076762.dkr.ecr.ap-south-1.amazonaws.com/common/spring-boot-maven:1.0
#FROM maven:3.6.3-jdk-11

#will make a directory in the container named app
WORKDIR /app

#Copy the project's source code and pm.xml to the container
COPY pom.xml .
COPY src ./src

#Build the project using maven
RUN mvn clean package

#Exposing port 8080
EXPOSE 8080
EXPOSE 8081

#ADD target/assignment2-0.0.1-SNAPSHOT.jar assignment2-docker.jar

#CMD can be overridden when docker container runs
#ENTRYPOINT ["java","-jar","assignment2-docker.jar"]
CMD ["java","-jar","assignment2-0.0.1-SNAPSHOT.jar"]