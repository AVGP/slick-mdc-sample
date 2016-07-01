# Slick MDC example

This is a sample project using [slickmdc](https://github.com/avgp/slickmdc).

The project starts a webserver using [Undertow](http://undertow.io) and serves requests at [http://localhost:8080](http://localhost:8080).
It will respond with "Hello Martin", reading the name from a SQLite database file using [Slick](http://slick.lightbend.com).

It logs information about itself using SLF4J and gives each request a "Request ID" that will stay the same for all child threads spawned
by Slick and others for the course of that request.

License: MIT
