FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml
RUN chmod +x ./mvnw

RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app
RUN useradd --system --uid 1001 ubesto && mkdir -p /app/data && chown -R ubesto:ubesto /app

COPY --from=build /workspace/target/ubesto-0.0.1-SNAPSHOT.jar /app/app.jar

ENV APP_UI_ENABLED=false
ENV SERVER_PORT=8080
ENV SPRING_DATASOURCE_URL="jdbc:h2:file:/app/data/db;AUTO_SERVER=FALSE"
ENV SPRING_DATASOURCE_USERNAME=db-user
ENV SPRING_DATASOURCE_PASSWORD=db-password
ENV JAVA_OPTS="-Djava.awt.headless=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=127.0.0.1 -XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8080 9010
USER ubesto

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
