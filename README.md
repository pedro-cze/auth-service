# auth-service
Authentication service using spring boot written in Kotlin

### Single Endpoint
The service API is made of single public endpoint, that can be accessed by POST `{host}/auth/login`. The service then verifies provided credentials (username, password) and
returns either generated API token in case of success or `401 - Unauthorized` otherwise.

### Database
The service uses Postgres as its primary datasource.
