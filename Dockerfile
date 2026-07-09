FROM openjdk:17-jdk-slim
# 把容器内工作目录从 /app 改成 /roommind
WORKDIR /roommind
# 启动命令对应容器内 roommind.jar
ENTRYPOINT ["java","-jar","roommind.jar","--spring.profiles.active=test"]