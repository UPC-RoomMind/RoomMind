package com.example.web.tools;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc的配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    /**
     * 配置切面
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //获取当前上下文用户信息拦截器
        registry.addInterceptor(new CurrentUserInterceptor())
                .addPathPatterns("/**");
    }
    /**
     * 配置前后端跨域报错的问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
    /**
     * 资源的配置处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 原有配置：映射 static 目录
        String filePath = System.getProperty("user.dir");
        String localtion = "file:" + filePath + "/src/main/resources/static/";
        registry.addResourceHandler("/**").addResourceLocations(localtion);
        
        // ✅ 新增：映射上传目录（支持开发和生产环境）
        String uploadPath = System.getProperty("app.upload.path", "./uploads");
        registry.addResourceHandler("/**").addResourceLocations("file:" + uploadPath + "/");
    }
}
