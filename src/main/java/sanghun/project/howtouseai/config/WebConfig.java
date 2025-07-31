package sanghun.project.howtouseai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 카테고리 아이콘 전용 핸들러
        registry.addResourceHandler("/images/categories/**")
                .addResourceLocations("classpath:/static/images/categories/");
    }
} 