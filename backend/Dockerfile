FROM maven:3.8.4-jdk-11-slim as backend-builder
WORKDIR /app
COPY . /app
RUN mvn -f /app/pom.xml clean package -P prod -D maven.test.skip=true -D autoconfig.charset=utf-8

FROM alpine:3
USER root
WORKDIR /app
EXPOSE 80
COPY --from=backend-builder /app/target/phone-number-manager-backend-0.0.1.jar .
ENV TZ=Asia/Shanghai
ENV SERVER_PORT=80
ENV CONTEXT_PATH=/
RUN echo "https://mirrors.aliyun.com/alpine/edge/main/" > /etc/apk/repositories \
    && echo "https://mirrors.aliyun.com/alpine/edge/community/" >> /etc/apk/repositories \
    && apk update && apk add --update --no-cache openjdk11-jre && rm -f /var/cache/apk/* && ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone && sh -c "touch /app/phone-number-manager-backend-0.0.1.jar"
CMD ["java", "-jar", "/app/phone-number-manager-backend-0.0.1.jar", "--spring.profiles.active=prod"]
