package octopus.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * <pre>
 * @WebMvcTest는 Controller 관련 클래스만 스캔하기 때문에 JPA 관련 클래스를 읽을 수 없다.
 * 때문에 Test Class 작성 시 아래의 오류를 발생한다.
 * Caused by: java.lang.IllegalArgumentException: JPA metamodel must not be empty!
 * </pre>
 * 
 * @author jypark
 */
@Configuration
@EnableJpaAuditing // JPA Auditing 활성화, @EntityListeners 를 사용할 수 있도록 함.
public class JpaConfig {
}