# ── Stage 1: Maven 构建 ────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-8-alpine AS builder

WORKDIR /app

# 先只复制所有 pom.xml，利用 Docker 层缓存（依赖不变就不重新下载）
COPY pom.xml .
COPY csdn-meeting-domain/pom.xml        csdn-meeting-domain/
COPY csdn-meeting-application/pom.xml   csdn-meeting-application/
COPY csdn-meeting-infrastructure/pom.xml csdn-meeting-infrastructure/
COPY csdn-meeting-interfaces/pom.xml    csdn-meeting-interfaces/
COPY csdn-meeting-start/pom.xml         csdn-meeting-start/

RUN mvn dependency:go-offline -B

# 再复制源码并构建
COPY . .
RUN mvn package -DskipTests

# ── Stage 2: 运行时（只保留 JRE + JAR，镜像更小）─────────────────────
FROM eclipse-temurin:8-jre-alpine

WORKDIR /app

COPY --from=builder /app/csdn-meeting-start/target/csdn-meeting-client-start.jar app.jar

# 数据目录（图片等持久化文件），由 docker compose 挂载宿主机 ./data
VOLUME ["/app/data"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
