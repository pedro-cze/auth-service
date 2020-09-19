docker run --rm -P --publish 5432:5432 --name auth-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=auth-docker -d postgres:latest
