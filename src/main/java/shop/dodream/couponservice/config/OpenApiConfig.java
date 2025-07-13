package shop.dodream.couponservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookServiceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("쿠폰 서비스 API")
                        .version("v1.0")
                        .description("쿠폰 서비스 API"));
    }
}
