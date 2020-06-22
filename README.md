# Microservice REST API designed to store revisions

Application stores configuration revisions.
Useful to store common configurations of mobile client applications, eg. colors, ads endpoints.

Administrator can store a key, and a value pair for a given client and version eg. android 8.0, android 4.4 etc. <br>
User can get all changed configurations for given client with specified version, eg. android 5.0. <br>
User can use ETag value in request to get configurations changed after given ETag.

# App requirements
1. JAVA_HOME variable should be provided to run program.
2. Lombok plugin is required to properly display code.
3. Required java - 1.8.

# Java version and libraries
Application is written using Java 8.
Libraries used in project:
- [Spring Boot 2](https://spring.io/projects/spring-boot)
- [Hiberante 5 + JPA](https://junit.org/junit5/)
- [Lombok](https://github.com/rzwitserloot/lombok)
- [JUnit 5](https://junit.org/junit5/)
- [Mockito 3 ](https://junit.org/junit5/)
- [Gradle](https://gradle.org/)

# Documentation
API documentation is available under /swagger-ui.html. <br>
Example when application is running at localhost, documentation is at:
http://localhost:8080/swagger-ui.html

