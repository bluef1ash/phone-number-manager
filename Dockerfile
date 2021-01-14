FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD ./target/phone-number-manager-0.0.1-SNAPSHOT.jar app.jar
ADD ./src/resources/data.sql data.sql
ADD ./README.md README.md
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar", "-Dlogging.path=/var/log"]
