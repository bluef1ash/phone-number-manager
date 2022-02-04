FROM java:11
EXPOSE 8080
WORKDIR /usr/src/app/
USER root
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
ENV TZ=Asia/Shanghai
ADD ./backend/target/*.jar /app.jar
RUN bash -c "touch /app.jar"
CMD ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]

FROM circleci/node:latest-browsers as builder
WORKDIR /usr/src/app/
USER root
COPY ./frontend/package.json ./
RUN npm install
COPY ./frontend/ ./
RUN npm run test:all && npm run fetch:blocks && npm run build

FROM nginx
WORKDIR /usr/share/nginx/html/
COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /usr/src/app/dist  /usr/share/nginx/html/
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
ENV TZ=Asia/Shanghai
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
