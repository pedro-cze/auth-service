# auth-service 
[![Build Status](https://travis-ci.com/pedro-cze/auth-service.svg?token=EvkxjVyDZp6WYVbjE5Ke&branch=master)](https://travis-ci.com/pedro-cze/auth-service)

![LINE](http://line.coverage.link)
![BRANCH](http://branch.coverage.link)
![COMPLEXITY](http://complexity.link)

Authentication service using spring boot written in Kotlin

### Build
<code>./gradlew build</code>

### Auth & Registration Endpoints
Every authentication request is served by single endpoint that can be accessed through POST `{host}/auth/login`. The service then verifies provided credentials (username, password) and returns either generated API token in case of success or `401 - Unauthorized` otherwise.<br>
Registration is only allowed to user with admin rights.

### Database
The service uses Postgres as its primary datasource.
