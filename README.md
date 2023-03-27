# 社区居民联系方式管理系统

此系统使用 Spring Boot + Mybatis Plus + Spring Security + React + Ant Design Pro 架构编写。数据库采用 MySQL，提供**强大的**、**安全的**和**完整的**管理社区居民的信息的功能。

![](https://img.shields.io/badge/license-MIT-green) ![](https://camo.githubusercontent.com/200800486bf56a3f00be17fd8b81711349ee51cebf9c6e7ff2f67aac3ceb4e62/68747470733a2f2f62616467656e2e6e65742f62616467652f69636f6e2f416e7425323044657369676e3f69636f6e3d68747470733a2f2f67772e616c697061796f626a656374732e636f6d2f7a6f732f616e7466696e63646e2f507034575067564442332f4b4470677667754d704766716148506a6963524b2e737667266c6162656c) ![](https://camo.githubusercontent.com/a4e9a25db34208ff56c51c597f3148f926bf0b16b6eaf135944f25e907eed878/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f6275696c64253230776974682d756d692d3032386665342e7376673f7374796c653d666c61742d737175617265) ![](https://img.shields.io/badge/Redis-5.0+-yellow.svg)

## 安全性

此系统在系统层面提供了众多的安全特性，产品安全无忧。这些特性包括：

- Session会话验证
- ~~系统用户“单点登录”~~
- XSS安全防护
- 表单自动验证
- 输入数据过滤
- 表单令牌验证
- 前后端分离

## 技术实现

- [Maven](https://maven.apache.org)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [MySQL](https://www.mysql.com)
- [MyBatis Plus](https://baomidou.com)
- [POI](https://poi.apache.org)
- [React](https://github.com/facebook/react)
- [Ant Design Pro](https://pro.ant.design)
- [pro components](https://github.com/ant-design/pro-components)
- ...

感谢以上技术的开发者！

## 系统后台源码链接

- [Github](https://github.com/bluef1ash/phone-number-manager/tree/main/backend)
- [Gitee](https://gitee.com/bluef1ash/phone-number-manager/tree/main/backend)

## 系统前台源码链接

- [Github](https://github.com/bluef1ash/phone-number-manager/tree/main/frontend)
- [Gitee](https://gitee.com/bluef1ash/phone-number-manager/tree/main/frontend)

## 系统截图

### 登录页面

![image](https://github.com/bluef1ash/phone-number-manager/raw/main/design/screenshot/login.png)

### 系统首页

![image](https://github.com/bluef1ash/phone-number-manager/raw/main/design/screenshot/index.png)

### 列表页面

![image](https://github.com/bluef1ash/phone-number-manager/raw/main/design/screenshot/list.png)

### 添加、编辑数据页面

![image](https://github.com/bluef1ash/phone-number-manager/raw/main/design/screenshot/create.png)

## Author: Bluef1ash （廿二月的天）

E-mail: liangtian_2005@163.com

## 增加Docker版本

[系统前台镜像](https://hub.docker.com/repository/docker/bluef1ash/phone-number-manager-frontend)，[系统后台镜像](https://hub.docker.com/repository/docker/bluef1ash/phone-number-manager-backend)，推荐使用 `docker-compose`，`docker-compose.yml` 配置文件如下

````yml
version: '3.5'

services:
  database:
    image: library/mysql:latest
    container_name: "mysql"
    environment:
      - MYSQL_ROOT_PASSWORD=root
    command: [
      '--character-set-server=utf8mb4',
      '--collation-server=utf8mb4_unicode_ci'
    ]

  redis:
    image: redis:latest
    container_name: redis
    command: redis-server /etc/redis/redis.conf
    privileged: true

  phone-number-manager-backend:
    image: bluef1ash/phone-number-manager-backend:latest
    container_name: "phone-number-manager-backend"
    environment:
      - SERVER_PORT=80
      - CONTEXT_PATH=/
      - DATABASE_HOST=127.0.0.1
      - DATABASE_PORT=3306
      - DATABASE_NAME=phone_number_manager
      - DATABASE_USERNAME=root
      - DATABASE_PASSWORD=root
      - REDIS_HOST=127.0.0.1
      - REDIS_PORT=6379
    depends_on:
      - database
      - redis

  phone-number-manager-frontend:
    image: bluef1ash/phone-number-manager-frontend:latest
    container_name: "phone-number-manager-frontend"
    environment:
      - REACT_APP_API_BASE_URL=http://phone-number-manager-backend
    ports:
      - "80:80"
    volumes:
      - dist:/usr/share/nginx/html:ro
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - phone-number-manager-backend

volumes:
  dist:

````

## 商业友好的开源协议

此系统遵循MIT开源许可证发布。MIT许可证（The MIT License）是许多软件授权条款中，被广泛使用的其中一种。与其他常见的软件授权条款（如GPL、LGPL、BSD）相比，MIT是相对宽松的软件授权条款。

## 首次运行需要执行 `npm install` 与 `npm run build` 命令，切记！（使用 `docker-compose` 则无需执行）

```shell
npm install
npm run build
```

## 系统默认用户

> 系统默认用户名： **admin**

> 系统默认密码： **admin888**
