package octopus.base.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * </pre>
 */
@Data
@MappedSuperclass // BaseEntity를 상속한 Entity들은 아래의 필드들을 컬럼으로 인식한다.
@EntityListeners(AuditingEntityListener.class) // Audting(자동으로 값 Mapping) 기능 추가
public class
BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String        crtId; // 생성자
    protected LocalDateTime crtDt; // 생성일자
    protected String        mdfId; // 수정자
    protected LocalDateTime mdfDt; // 수정일
    
    protected String createDate; // 생성일자
    protected String modifyDate; // 수정일
    
    public void parseDate(LocalDateTime crtDt, LocalDateTime mdfDt) {
        this.createDate = crtDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.modifyDate = mdfDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
