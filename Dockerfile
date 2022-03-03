FROM maven:3.8.4-jdk-11-slim as backend-builder
WORKDIR /app
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package -P prod -D maven.test.skip=true -D autoconfig.charset=utf-8

FROM alpine:latest
USER root
RUN apk add --update --no-cache openjdk11-jre && rm -f /var/cache/apk/*
WORKDIR /app
EXPOSE 8080
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
ENV TZ=Asia/Shanghai
ENV DATABASE_HOST=127.0.0.1
ENV DATABASE_PORT=3306
ENV DATABASE_NAME=phone_number_manager
ENV DATABASE_USERNAME=root
ENV DATABASE_PASSWORD=root
COPY --from=backend-builder /app/target/phone-number-manager-0.0.1.jar .
RUN bash -c "touch /app/phone-number-manager-0.0.1.jar"
CMD ["java", "-jar", "/app/phone-number-manager-0.0.1.jar", "--spring.profiles.active=prod"]

FROM node:lts-alpine as frontend-builder
WORKDIR /usr/src/app/
COPY ./frontend/package.json ./
RUN npm install
COPY ./frontend/ ./
RUN npm run test:all && npm run fetch:blocks && npm run build

FROM nginx:stable-alpine
USER root
WORKDIR /usr/share/nginx/html/
COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=frontend-builder /usr/src/app/dist /usr/share/nginx/html/
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
ENV TZ=Asia/Shanghai
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
