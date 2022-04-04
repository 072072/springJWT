package sang.ik.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//swagger 사용하려면 @EnableWebMvc 추가해줘야함
@EnableWebMvc
@Configuration
public class WebConfig {

/*    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("jwt-auth-token");
    }*/
}
