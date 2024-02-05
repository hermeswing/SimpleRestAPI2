package octopus.sample.repository;

import octopus.sample.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// @Repository : JpaRepository를 사용하면 @Repository를 사용하지 않아도 됨.
public interface UsersRepository extends JpaRepository<Users, String> {
    /* 유효성 검사 - 중복 체크
     * 중복 : true, 중복이 아닌 경우 : false
     *
     * countBy~ 는 DB의 모든 row를 순회하며 갯수를 세는 쿼리를 실행하고,
     * existsBy~ 는 조건을 만족하는 데이터가 1건이라도 존재하면 즉시 스캔을 종료한다.
     */
    boolean existsByUserId( String userId );

    boolean existsByUserNm( String userNm );

    boolean existsByEmail( String email );

    Optional<Users> findByUserId( String userId );

    void deleteByUserId( String userId );
}