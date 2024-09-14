FROM eclipse-temurin:21-jdk AS build

ARG GRADLE_VERSION=8.6

RUN apt-get update && apt-get install -y unzip wget

RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip \
    && rm gradle-${GRADLE_VERSION}-bin.zip \
    && mv gradle-${GRADLE_VERSION} /opt/gradle

ENV GRADLE_HOME=/opt/gradle
ENV PATH=$PATH:$GRADLE_HOME/bin

WORKDIR /app

COPY . .

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
