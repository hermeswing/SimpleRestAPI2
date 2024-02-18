package octopus.base.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * <pre>
 * 출처 : https://velog.io/@u-nij/JWT-JWT-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-4-Redis-%EC%84%A4%EC%A0%95RedisRepositoryConfig-RedisService
 * </pre>
 * 
 * @author jypark
 */
@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedisRepositoryConfig {
    
    @Value("${spring.redis.host}")
    private String host;
    
    @Value("${spring.redis.port}")
    private int port;
    
    /**
     * Lettuce로 Redis와 연결한다.
     * 
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // single node에 redis를 연결하기 위한 설정 정보를 가지고 있는 기본 클래스이다.
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(
                host, port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
    
    /**
     * Spring Boot 2.0부터 RedisTemplate와 StringTemplate 두 가지 Bean을 자동으로 생성하여 제공하고 있다. RedisTemplate에는 serializer를 설정해주는데 설정하지 않으면,
     * 스프링에서 조회할 때는 값이 정상으로 보이지만 redis-cli로 데이터 확인이 어렵다.
     * 
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        
        /**
         * redis에 객체를 저장할 때 redis-cli로 확인해 보면 byte코드로 저장되어 serializer를 통해 직렬화해 주어야 한다. 다양한 직렬화 방법 중 StringRedisSerializer를 사용했다.
         * String 값을 그대로 저장하는 Serializer이다. 따라서 객체를 Json형태로 변환하여 Redis에 저장하기 위해서는 직접 Encoding, Decoding을 해주어야 한다는 단점이 존재한다. 이때
         * StringRedisSerializer를 사용하고 직접 Json Parser를 적용하는 방식으로 RedisTemplate을 사용하여 단점을 보완한다.
         */
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        
        // 일반적인 key:value의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        
        // Hash를 사용할 경우 시리얼라이저
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        
        // 모든 경우
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        
        return redisTemplate;
    }
    
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
        
        return stringRedisTemplate;
    }
}