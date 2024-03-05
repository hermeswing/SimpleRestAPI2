package octopus.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        //return new Docket( DocumentationType.SWAGGER_2 )   // 2.x 버전
        return new Docket(DocumentationType.OAS_30)          // 3.x 버전
                .securityContexts(Arrays.asList(securityContext())) // 추가
                .securitySchemes(Arrays.asList(apiKey()))           // 추가
                .select()
                .apis(RequestHandlerSelectors.basePackage("octopus"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    // 추가
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    // 추가
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }

    // 추가
    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Octopus REST API",
                "사용자 관리 of API.",
                "Octopus V0.1",
                "",
                new Contact("문어발 프로젝트", "www.octopus.com", "myemail@octopus.com"),
                "무조건 오픈", "API license URL", Collections.emptyList());
    }


}
