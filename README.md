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
- [Spring security](https://spring.io/projects/spring-security)
- [Hiberante 5 + JPA](https://hibernate.org/orm/documentation/5.0)
- [Lombok](https://github.com/rzwitserloot/lombok)
- [JUnit 5](https://junit.org/junit5)
- [Mockito 3 ](https://site.mockito.org)
- [Gradle](https://gradle.org)
- [H2 Database](https://www.h2database.com)
- [Swagger 2](https://swagger.io/docs/specification/2-0/basic-structure)
# Documentation
API documentation is available under /swagger-ui.html. <br>
Example when application is running at localhost, documentation is at:
http://localhost:8080/swagger-ui.html



## Examples

```http
HTTP/1.1 POST /config
{ "client": "android", "version": "4.4", "key": "font_color", "value": "#c2b280" }

HTTP1/1.1 201 Created
```

```http
HTTP/1.1 GET /config/android/4.4

HTTP/1.1 200 OK
ETag: 1593412511234
{ "font_color": "#c2b28" }
```

```http
HTTP/1.1 GET /config/android/4.4
If-None-Match: 1593412511234

HTTP/1.1 304 Not Modified
```

```http
HTTP/1.1 GET /config/android/8.0

HTTP/1.1 304 Not Modified
```

```http
HTTP/1.1 GET /config/android/5.0

HTTP/1.1 304 Not Modified
```

```http
HTTP/1.1 GET /config/android/5.1

HTTP/1.1 304 Not Modified
```

```http
HTTP/1.1 POST /config
{ "client": "android", "version": "4.4", "key": "background_color", "value": "#ff0000" }

HTTP/1.1 201 Created
```

```http
HTTP/1.1 GET /config/android/4.4

HTTP/1.1 200 OK
Etag: 1593422511234 
{ "font_color": "#c2b280", "background_color": "#ff0000" }
```

```http
HTTP/1.1 GET /config/android/4.4
If-None-Match: 1593412511234

HTTP/1.1 200 OK
ETag: 1593422511234
{ "background_color": "#000" }
```

```http
HTTP/1.1 GET /config/android/4.4
If-None-Match: 1593422511234

HTTP/1.1 304 Not Modified
```

```http
HTTP/1.1 POST /config
{ "client": "android", "version": "4.4", "key": "background_color", "value": "#ff0001" }

HTTP/1.1 201 Created
```

```http
HTTP/1.1 POST /config
{ "client": "android", "version": "4.4", "key": "font_color", "value": "#d2b280" }

HTTP/1.1 201 Created
```
```http
HTTP/1.1 GET /config/android/4.4

HTTP/1.1 200 OK
Etag: 1593423400230  -- latest config creation timestamp (font_color, #d2b280)
{ "font_color": "#d2b280", "background_color": "#ff0001" }
```

```http
HTTP/1.1 POST /config
{ "client": "android", "version": "4.4", "key": "icon_color", "value": "#ff0031" }

HTTP/1.1 201 Created
```

```http
HTTP/1.1 POST /config
{ "client": "android", "version": "4.4", "key": "apikey", "value": "#14uuee4uf55mm" }

HTTP/1.1 201 Created
```

```http
HTTP/1.1 GET /config/android/4.4

HTTP/1.1 200 OK
ETag: 1593423511234  -- latest config creation timestamp (apikey, #14uuee4uf55mm)
{ "font_color": "#d2b280", "background_color": "#ff0001", "icon_color" : "#ff0031", "apikey": "#14uuee4uf55mm" }
```

```http
HTTP/1.1 GET /config/android/4.4
If-None-Match: 1593423400230

HTTP/1.1 200 OK
ETag: 1593423511234  -- latest config creation timestamp (apikey, #14uuee4uf55mm)
{ "icon_color" : "#ff0031", "apikey": "#14uuee4uf55mm" }
```