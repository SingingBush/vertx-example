Vert.x Example
==============

[![Maven](https://github.com/SingingBush/vertx-example/actions/workflows/maven.yml/badge.svg)](https://github.com/SingingBush/vertx-example/actions/workflows/maven.yml)

This example project was put together to evaluate the potential of using Vert.x instead of Spring Boot. Vert.x is a lot more light weight and will boot much faster than a typical Spring application. This will be better in a cloud environment such as AWS where being able to to auto-scale rapidly is important.

| URL | purpose |
| -- | -- |
| http://localhost:8888/metrics | prometheus metrics endpoint |
| http://localhost:8888/joke | makes an async call to icanhazdadjoke.com then returns text/plain |
| http://localhost:8888/jokes | makes an async call to icanhazdadjoke.com then returns json |
| http://localhost:8888/greeting | returns a random greeting string as text/plain |

# Run the application

```
mvn clean compile exec:java
```

# Build an executable jar (fat jar) and run it

```
mvn clean package
java -jar target/vertx-example-1.0.0-SNAPSHOT-fat.jar
```

# Build and run in a local docker container

```
docker build -t vertx-example .
docker run -dp 8888:8888 vertx-example
```

# Addition Resources

* https://vertx.io/docs/[Vert.x Documentation]
* https://stackoverflow.com/questions/tagged/vert.x?sort=newest&pageSize=15[Vert.x Stack Overflow]
* https://groups.google.com/forum/?fromgroups#!forum/vertx[Vert.x User Group]
* https://gitter.im/eclipse-vertx/vertx-users[Vert.x Gitter]


