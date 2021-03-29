FROM openjdk:8-jdk-alpine
VOLUME /tmp
RUN apk update && apk upgrade && apk add ca-certificates && update-ca-certificates && apk add --update tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone && rm -rf /var/cache/apk/*
ENV TZ=Asia/Shanghai
ADD ./target/phone-number-manager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar", "-Dlogging.path=/var/log"]
