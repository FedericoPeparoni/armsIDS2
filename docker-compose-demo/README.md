# ABMS Docker Compose Demo

This project contains the `docker-compose` configuration files needed to run the ABMS
full-stack application on the demo server.

## Prerequisites

You will need the following software installed:

    - Docker
    - Docker Compose

If you are developing on Mac or Windows, you can install both of these automatically
with the Docker Toolbox. Docker Toolbox requires that VirtualBox also be installed on
your system.

## Running the application

In your commandline, navigate to the root of the repository and run:

```
docker-compose up
```

This will bootstrap and initialize all components of the system.

When the application is ready, you can access it from the Docker Host's IP address on port 80.

The application can be terminated with `CTRL-C`.

### Daemon Mode

You can also start the application to run continously as a daemon process by running:

```
docker-compose up -d
```

To quit, simple run:

```
docker-compose stop
```
