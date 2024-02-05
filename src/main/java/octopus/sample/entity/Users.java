package octopus.sample.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import octopus.base.enumeration.UserRole;
import octopus.base.model.BaseEntity;
import octopus.sample.dto.UserDTO;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Proxy;
import org.springframework.data.domain.Persistable;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

@Getter // getter를 자동으로 생성합니다.
// @Setter // 객체가 무분별하게 변경될 가능성 있음
// @ToString(exclude = { "crtId", "crtDt", "mdfId", "mdfDt" }) // 연관관계 매핑된 엔티티 필드는 제거. 연관 관계 필드는 toString()에서 사용하지 않는 것이
// // 좋습니다.
@ToString( callSuper = true )
@NoArgsConstructor( access = AccessLevel.PROTECTED ) // 인자없는 생성자를 자동으로 생성합니다. 기본 생성자의 접근 제어자가 불명확함. (access =
// AccessLevel.PROTECTED) 추가
@DynamicInsert // insert 시 null 인필드 제외
@DynamicUpdate // update 시 null 인필드 제외
// @AllArgsConstructor // 객체 내부의 인스턴스멤버들을 모두 가지고 있는 생성자를 생성 (매우 위험)
@JsonIgnoreProperties( { "hibernateLazyInitializer", "handler" } ) // Post Entity에서 User와의 관계를 Json으로 변환시 오류 방지를 위한 코드
@Proxy( lazy = false )
@Entity // jpa entity임을 선언. 실제 DB의 테이블과 매칭될 Class
@Table( name = "USERS", uniqueConstraints = { @UniqueConstraint( columnNames = "userId" ) } )
public class Users extends BaseEntity implements Persistable<Long> {
    private static final long serialVersionUID = 1L;

    /**
     * <pre>
     * save 시 Select 날리는 것을 방지
     * 특정 필드를 컬럼에 매핑하지 않음(매핑 무시),
     * 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용
     * </pre>
     */
    @Transient
    private boolean isNew = true;
    @Id // PK 필드임
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id; // Comment ID
    @NotBlank
    @Column( nullable = false )
    private String userId; // 사용자 ID
    @NotBlank
    @Column( nullable = false )
    private String userNm; // 사용자명
    //@Id // PK 필드임
    @NotBlank
    @Column( nullable = false )
    private String email; // 이메일주소
    @Column( nullable = false )
    private String password; // 비밀번호
    @Column( nullable = false )
    @Enumerated( EnumType.STRING )
    private UserRole userRole;

    @Builder
    public Users( Long id, String userId, String userNm, String email, String password, UserRole userRole,
                  String crtId, String mdfId ) {
        Assert.hasText( userId, "userId must not be empty" );
        Assert.hasText( email, "email must not be empty" );
        Assert.hasText( crtId, "crtId must not be empty" );
        Assert.hasText( mdfId, "mdfId must not be empty" );

        this.id = id;
        this.userId = userId;
        this.userNm = userNm;
        this.email = email;
        this.password = password;
        this.userRole = UserRole.USER;

        super.crtId = crtId;
        super.mdfId = mdfId;
    }

    public static Users createEntry( UserDTO.UserDto userDTO ) {
        Users user = new Users();
        user.id = userDTO.getId();
        user.userId = userDTO.getUserId();
        user.userNm = userDTO.getUserNm();
        user.email = userDTO.getEmail();
        user.password = userDTO.getPassword();
        user.userRole = UserRole.USER;

        user.crtId = userDTO.getCrtId();
        user.mdfId = userDTO.getMdfId();

        return user;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    /**
     * 사용자 정보 Update
     */
    public void updateUsers( UserDTO.UserDto dto ) {
        this.userId = dto.getUserId();
        this.userNm = dto.getUserNm();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.userRole = dto.getUserRole();
        super.mdfId = dto.getMdfId();
    }

}
