FROM maven:3-openjdk-17 as backend-builder
COPY . /app
RUN mvn -f /app/pom.xml clean package -P prod -D maven.test.skip=true -D autoconfig.charset=utf-8

FROM openjdk:17-slim
WORKDIR /app
EXPOSE 80
COPY --from=backend-builder /app/target/phone-number-manager-backend-0.0.1.jar .
ENV TZ=Asia/Shanghai
ENV SERVER_PORT=80
ENV CONTEXT_PATH=/
RUN ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone \
    && sh -c "touch /app/phone-number-manager-backend-0.0.1.jar"
CMD ["java", "-jar", "/app/phone-number-manager-backend-0.0.1.jar", "--spring.profiles.active=prod"]
