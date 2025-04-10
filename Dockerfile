# Use a Linix image with Tomcat 10
FROM tomcat:10.1-jdk21-openjdk-slim

# Copy in our ROOT.war to the right place in the container
COPY ROOT.war /usr/local/tomcat/webapps/
