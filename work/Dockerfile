# 轻量JDK17基础镜像
FROM eclipse-temurin:17-jre-alpine
WORKDIR /roommind
# 复制后端jar到镜像内
COPY roommind.jar /roommind/roommind.jar
# 复制前端dist静态资源到容器对应目录
COPY dist /roommind/static
# 指定启用prod环境配置
ENTRYPOINT ["java","-jar","roommind.jar","--spring.profiles.active=prod"]