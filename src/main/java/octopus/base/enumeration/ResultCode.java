package octopus.base.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * ExceptionAdvice 에서 사용하기 위한 Error Code
 * </pre>
 * 
 * @author jypark
 */
@AllArgsConstructor
@Getter
public enum ResultCode {
    ERROR(-1, "일반오류"),
    WARNING(1, "주의"),
    SUCCESS(0, "성공");
    
    private final int code;
    private final String name;
}