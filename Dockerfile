FROM java:11
EXPOSE 8080
VOLUME /tmp
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
ENV TZ=Asia/Shanghai
ADD ./target/*.jar /app.jar
RUN bash -c "touch /app.jar"
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
