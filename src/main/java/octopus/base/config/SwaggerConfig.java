package octopus.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * <pre>
 *     Swagger 3.x 는 @link "http:// localhost:8080/swagger-ui/index.html"
 * </pre>
 */
@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket( DocumentationType.SWAGGER_2 )
                .select()
                .apis( RequestHandlerSelectors.basePackage( "octopus" ) )
                .paths( PathSelectors.any() )
                .build()
                .apiInfo( apiInfo() );
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Octopus REST API",
                "사용자 관리 of API.",
                "Octopus V0.1",
                "",
                new Contact( "문어발 프로젝트", "www.octopus.com", "myemail@octopus.com" ),
                "무조건 오픈", "API license URL", Collections.emptyList() );
    }


}
