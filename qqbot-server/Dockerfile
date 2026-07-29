# QQBot 后端 - Docker 镜像构建
# 基于 Eclipse Temurin JRE 17（轻量化 Java 17 运行环境）

FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /opt/qqbot

# 创建日志目录
RUN mkdir -p /opt/qqbot/logs

# 复制打包好的 JAR
COPY target/qqbot-server.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动参数
# -Xms256m -Xmx512m: 初始堆256M，最大堆512M（根据服务器配置调整）
# -XX:+UseG1GC: 使用 G1 垃圾回收器
# -Dspring.profiles.active: 默认使用生产环境配置
ENTRYPOINT ["java", \
    "-Xms256m", \
    "-Xmx512m", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
