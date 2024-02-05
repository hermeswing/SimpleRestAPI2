package octopus.base.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <pre>
 * 부모 클래스를 상속받는 자식 클래스에게 매핑 정보만을 제공
 * abstract 클래스로 생성해야 합니다.
 * </pre>
 */
@Getter
@ToString
@MappedSuperclass // BaseEntity를 상속한 Entity들은 아래의 필드들을 컬럼으로 인식한다.
@EntityListeners(AuditingEntityListener.class) // Audting(자동으로 값 Mapping) 기능 추가
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * 
     * @CreatedBy // implements AuditorAware<Long>를 구현한 Class를 생성해야 한다.
     */
    // private String crtId;
    @Column(name = "crt_id", nullable = false, updatable = false)
    protected String crtId;
    
    /**
     * 생성일자 : Entity가 생성되어 저장될 때 시간이 자동 저장된다.
     */
    @CreatedDate
    @Column(name = "crt_dt", nullable = false, updatable = false)
    protected LocalDateTime crtDt;
    
    /**
     * 수정자
     * 
     * @LastModifiedBy // implements AuditorAware<Long>를 구현한 Class를 생성해야 한다.
     */
    // private String mdfId;
    @Column(name = "mdf_id")
    protected String mdfId;
    
    /**
     * 수정일자 : Entity가 생성되어 저장될 때 시간이 자동 저장된다.
     */
    @LastModifiedDate
    @Column(name = "mdf_dt")
    protected LocalDateTime mdfDt;
    
}
