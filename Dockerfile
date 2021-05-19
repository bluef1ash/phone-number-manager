FROM maven:3.8.4-jdk-11-slim as backend-builder
WORKDIR /app
COPY ./backend/src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package -P prod -D maven.test.skip=true -D autoconfig.charset=utf-8

FROM alpine:3
USER root
RUN apk add --update --no-cache openjdk11-jre && rm -f /var/cache/apk/*
WORKDIR /app
EXPOSE 80
COPY --from=backend-builder /app/target/phone-number-manager-0.0.1.jar .
ENV TZ=Asia/Shanghai
ENV SERVER_PORT=80
RUN ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone && bash -c "touch /app/phone-number-manager-0.0.1.jar"
CMD ["java", "-jar", "/app/phone-number-manager-0.0.1.jar", "--spring.profiles.active=prod"]

FROM node:lts-alpine as frontend-builder
WORKDIR /usr/src/app
COPY ./frontend/package.json /usr/src/app
RUN npm install
COPY ./frontend/ /usr/src/app
RUN npm run test:all && npm run build

FROM nginx:stable-alpine
USER root
WORKDIR /usr/share/nginx/html
COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=frontend-builder /usr/src/app/dist /usr/share/nginx/html
ENV TZ=Asia/Shanghai
ENV BASE_URL=http://127.0.0.1:8080
EXPOSE 80
RUN ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone
CMD ["nginx", "-g", "daemon off;"]
