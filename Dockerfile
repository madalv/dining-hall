
# EITHER UNCOMMENT THE LINE N SUFFER
# OR BUILD FATJAR YOURSELF BEFORE BUILDING
FROM openjdk:18-slim
WORKDIR /src
COPY . /src

#RUN gradlew buildFatJar


EXPOSE 8081:8081
RUN mkdir /app
RUN cp /src/build/libs/*.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]