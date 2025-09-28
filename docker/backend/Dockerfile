FROM openjdk:18-jdk-alpine

ARG JAR_FILE=easton-portal-0.0.1.jar
ENV TARGET_PATH=/opt/easton-portal/docker/backend/${JAR_FILE}
COPY ${JAR_FILE} ${TARGET_PATH}

ENTRYPOINT java -jar $TARGET_PATH