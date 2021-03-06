# 社区居民联系电话管理系统

此系统使用Spring Boot + Mybatis + Spring Security + Node.js + Webpack + Vue.js架构编写。数据库采用MySQL，提供**强大的**、**安全的**和**完整的**
管理社区居民的信息的功能。

## 安全性

此系统在系统层面提供了众多的安全特性，产品安全无忧。这些特性包括：

- Session会话验证
- 系统用户“单点登录”
- XSS安全防护
- 表单自动验证
- 输入数据过滤
- 表单令牌验证

## 技术实现

- [Maven](https://maven.apache.org)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [MySQL](https://www.mysql.com)
- [MyBatis](https://github.com/mybatis)
- [POI](https://poi.apache.org)
- [Webpack](https://github.com/webpack/webpack)
- [JQuery](https://jquery.com)
- [Bootstrap](https://getbootstrap.com)
- [Vue.js](https://github.com/vuejs/vue)
- [Vue Router](https://github.com/vuejs/vue-router)
- [Vue Cookie](https://github.com/alfhen/vue-cookie)
- [Vue CountTo](https://github.com/PanJiaChen/vue-countTo)
- [Element UI](https://element.eleme.io)
- [EChart](https://echarts.baidu.com)
- 开源模板[CoreUI](https://github.com/coreui)
- ...

感谢以上技术的开发者！

## 系统截图

![image](https://github.com/bluef1ash/phone-number-manager/raw/master/design/screenshot/login.jpg)

<div style="text-align: center">登录页面</div>

![image](https://github.com/bluef1ash/phone-number-manager/raw/master/design/screenshot/index.jpg)

<div style="text-align: center">系统首页</div>

![image](https://github.com/bluef1ash/phone-number-manager/raw/master/design/screenshot/list.jpg)

<div style="text-align: center">列表页面</div>

![image](https://github.com/bluef1ash/phone-number-manager/raw/master/design/screenshot/create.jpg)

<div style="text-align: center">添加、编辑数据页面</div>

## Author: Bluef1ash（廿二月的天）

E-mail: liangtian_2005@163.com

## 增加Docker版本

[查看Docker版本](https://hub.docker.com/repository/docker/bluef1ash/phone-number-manager)，成功安装Docker后，运行`docker push
bluef1ash/phone-number-manager:latest`，推荐使用`docker-compose`，配置问题如下

````yml
version: '3'
services:
    database:
        image: library/mysql:latest
        container_name: mysql
        environment:
            - MYSQL_ROOT_PASSWORD=root
        volumes:
            - .:/docker-entrypoint-initdb.d
        command: [
                '--character-set-server=utf8mb4',
                '--collation-server=utf8mb4_unicode_ci'
        ]
    phone-number-manager:
        image: bluef1ash/phone-number-manager:latest
        container_name: phone-number-manager
        build:
            context: .
            dockerfile: Dockerfile
        environment:
            spring.datasource.host: database
        volumes:
            - ./data.sql:/data.sql
        ports:
            - 8080:8080
        depends_on:
            - database
````

## 商业友好的开源协议

此系统遵循MIT开源许可证发布。MIT许可证（The MIT License）是许多软件授权条款中，被广泛使用的其中一种。与其他常见的软件授权条款（如GPL、LGPL、BSD）相比，MIT是相对宽松的软件授权条款。

## 首次运行需要执行`yarn install`与`yarn run build`命令，切记！（使用docker-compose则无需执行）

```shell
yarn install
yarn run build
```

## 系统默认用户

> 系统默认用户名：**admin**

> 系统默认密码：**admin888**
