# Use BellSoft's Liberica JDK on Alpine Linux as it's a
FROM bellsoft/liberica-openjre-alpine:17

# If we need fonts then uncomment these lines
#RUN apk update
#RUN apk add fontconfig ttf-dejavu --no-cache

COPY target/vertx-example-1.0.0-SNAPSHOT-fat.jar ./vertx-example.jar

CMD ["java", "-jar", "./vertx-example.jar"]
