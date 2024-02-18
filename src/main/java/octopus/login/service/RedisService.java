package octopus.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 출처 : https://velog.io/@u-nij/JWT-JWT-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-4-Redis-%EC%84%A4%EC%A0%95RedisRepositoryConfig-RedisService
 * </pre>
 * 
 * @author jypark
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Transactional
    public void setValues(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    // 만료시간 설정 -> 자동 삭제
    @Transactional
    public void setValuesWithTimeout(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }
    
    public String getValues(String key) {
        log.debug("key :: {}", key);
        return redisTemplate.opsForValue().get(key);
    }
    
    @Transactional
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}