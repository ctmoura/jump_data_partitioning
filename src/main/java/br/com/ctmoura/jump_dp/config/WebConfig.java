package br.com.ctmoura.jump_dp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.ctmoura.jump_dp.interceptor.RequestIdInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private RequestIdInterceptor requestIdInterceptor;

    public WebConfig(
            RequestIdInterceptor requestIdInterceptor) {
        super();
        this.requestIdInterceptor = requestIdInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor);
    }
}
