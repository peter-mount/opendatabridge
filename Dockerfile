# Dockerfile for the interim Darwin V16 feed
FROM maven:3-jdk-8 AS build

WORKDIR /work

# Install our settings.xml file so it uses our repository
ADD settings.xml /root/.m2/

# Add just the pom. If this gets changed then the cache is invalidated & everything
# after this point gets rebuilt.
ADD pom.xml pom.xml

# Run maven to install all dependencies but don't actually do anything.
# This means that if there's a compile issue, as long as the pom hasn't changed
# then we download them only once & docker will cache this layer.
#
# --batch-mode reduces the log spam in CI - i.e. Jenkins logs
# -Dmaven.main.skip disables the compile stage
# -Dassembly.skipAssembly=true disables the assembly stage
# -Dmaven.install.skip=true disables the install stage
# -Dxjc.skip=true disables xjc
RUN mvn \
      --batch-mode \
      -Dmaven.main.skip \
      -Dassembly.skipAssembly=true \
      -Dmaven.install.skip=true \
      -Dxjc.skip=true \
      clean install dependency:resolve

# Now add the sources & do the compile.
# Doing this here means that any changes to the sources don't invalidate the
# already downloaded dependencies.
ADD src/ src
RUN mvn --batch-mode clean install

# Now install the jars
RUN mkdir -p /dist/opt/nre &&\
    cd /dist/opt/nre &&\
    unzip /work/target/activemq-rabbitmq-bridge-*-zip-with-jars.zip

# The logging properties
ADD logging.properties /dist/opt/nre/logging.properties

# Install the launcher script into the correct place & make it executable
ADD docker-entry-point.sh /dist/docker-entry-point.sh
RUN chmod +x /dist/docker-entry-point.sh

# Build the final image with Java8 & the contents of /dest copied into the root
FROM openjdk:8-jre-alpine
MAINTAINER peter@retep.org
WORKDIR /opt/nre

COPY --from=build /dist/ /
