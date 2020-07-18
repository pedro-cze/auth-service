# auth-service
Authentication service using spring boot written in Kotlin

### Build
<code>./gradlew build</code>

### Auth & Registration Endpoints
Every authentication request is served by single endpoint that can be accessed through POST `{host}/auth/login`. The service then verifies provided credentials (username, password) and returns either generated API token in case of success or `401 - Unauthorized` otherwise.<br>
Registration is only allowed to user with admin rights.

### Database
The service uses Postgres as its primary datasource.