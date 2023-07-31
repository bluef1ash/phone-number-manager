FROM node:lts-alpine as frontend-builder
USER root
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN echo "https://mirrors.aliyun.com/alpine/edge/main" > /etc/apk/repositories \
     && echo "https://mirrors.aliyun.com/alpine/edge/community" >> /etc/apk/repositories \
     && apk update && npm config set registry https://registry.npmmirror.com && npm install && npm run build

FROM nginx:stable-alpine
USER root
WORKDIR /usr/share/nginx/html
COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=frontend-builder /usr/src/app/dist /usr/share/nginx/html
ENV TZ=Asia/Shanghai
ENV REACT_APP_API_BASE_URL=http://127.0.0.1:8080
EXPOSE 80
RUN ln -sf /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone
CMD ["nginx", "-g", "daemon off;"]
