Simple REST API Version 2
=
# 목적
- Git 적 사고방식으로 프로젝트를 진행해보자.
- 무엇을 진행해야 하는지 고민하고, 프로그램을 짜야 겠구만...
- branch 기준
  - dependency 추가 ( lombok, securiry 등등 )
  - 기능 ( 사용자 관리, 로그인, 로그아웃 등등 )

## History

1. 2024.02.02
   - SimpleRestAPI/20240202 branch 생성
     - `HelloControler.java` 생성
     - Git 명령어  
         `git add .`  
         `git commit -m "Hello World RestController 생성"`  
         `git push --set-upstream origin SimpleRestAPI/20240202`
     - Hello World RestController 생성
       - '/' 이하는 태그(tag)임. 수정할 수 있음.
       - 문제 #1
         - 날짜를 태그로 생성하니, 오늘 추가적인 작업을 하려면 태그가 중복됨.
         - 일단 날짜 + 시간 + 분까지 변경.
         - Git 명령어( -m 옵션 : move의 약자임. )  
             `git branch -m SimpleRestAPI/202402021500`  
             or `git branch -m SimpleRestAPI/20240202 SimpleRestAPI/202402021500`  
             `git push origin -u SimpleRestAPI/202402021500`
         - Git 명령어( -u 옵션 : 최초 push 시 --set-upstream 또는 --set-upstream-to와 동일한 역할 )  
             `git push -u origin SimpleRestAPI/202402021500`
       - 문제 #2
         - branch 가 추가로 생겼다.
         - 기존 SimpleRestAPI/20240202 브랜치는 삭제해야 겠다.
         - Git 명령어  
             `git branch -D [브랜치명]` 로컬브랜치 강제삭제  
             `git push origin --delete [브랜치명]` 원격브랜치 삭제  
             `git push origin --delete SimpleRestAPI/20240202`
   - Git Merge
     - `main` brach 에 `SimpleRestAPI/20240202` branch를 붙이는 작업
     - 처음에는 Cherry-pick 을 생각했다가..
       - Cherry-pick 은 해당 branch 를 `main` branch 에 뜯어다가 붙이는 작업이었고,
       - 다행히 conflict 나서 취소할 수 있었다.
         - Git 명령어 ( 오류가 나지 않았다면 이동했겠지... ㅡㅡㅋ )  
             `git cherry-pick --abort`
     - `SimpleRestAPI/20240202` branch는 놔두고, main 에 branch를 생성하는 작업은 Merge 를 해야 한다.
       - Git 명령어  
         `git checkout main` -> 일단 `main` 으로 branch를 옮기고,    
         `git merge SimpleRestAPI/202402021500` -> 일단 `main` 과 합친다.

2. 2024.02.03
   - SimpleRestAPI/202402031300 branch 생성
     - lombok 설치
     - postgresql 설치 
     - SimpleRestAPI/202402031300 branch 생성
     - 사용자 조회 / 수정 / 삭제 기능 추가
     - Postgresql 설정
         ````yaml
         spring:
           # Spring Database 처리
           datasource:
             class-name: org.postgresql.Driver                   # p6spy-spring-boot-starter 를 설정할 경우
             url: jdbc:postgresql://localhost:5432/springboot           # p6spy-spring-boot-starter 를 설정할 경우
             username: hermeswing
             password: pass
             #sql-script-encoding: utf-8            # springboot 1.5.x - Deprecated!
           jpa:
             open-in-view: false
             #database-platform: org.hibernate.dialect.PostgreSQLDialect
             #show-sql: true       # System.out 으로 출력. logging.level.org.hibernate.SQL=debug 로 대체합니다.
             hibernate:
               # create : entity를 drop cascade 하고 다시 생성
               # update : entity가 수정되면 수정된 내용만 반영
               # create-drop,validate, none
               # 하이버네이트가 자동으로 생성해주는 DDL은 신뢰성이 떨어지기 때문에
               # 절대로! 운영DB 환경에서 그대로 사용하면 안되고, 직접 DDL을 작성하는 것을 권장
               ddl-auto: none
         ```

3. 2024.02.05
  - SimpleRestAPI/202402031300 을 main 으로 Merge

4. 2024.02.06
  - 소스정리
    - `application.yml`, `application-local.yml`, `logback-local.xml` 
  - SimpleRestAPI2/20240206 생성
    - Swagger v3.x 를 적용. `localhost:8080/swagger-ui/index.html`
    - 관련 Source
      - `SwaggerConfig.java` : Swagger Config 설정
    - build.gradle
      - `implementation 'io.springfox:springfox-boot-starter:3.0.0'` 추가

5. 2024.02.07
   - SimpleRestAPI2/20240206 을 main 으로 Merge
   - SimpleRestAPI2/202402071500 branch 생성
     - JSON 타입으로 리턴 결과를 표준화했음.
       - 관련 Source
         - `ResultCode.java` 추가
         - `CommonResult.java` 추가
         - `ListResult.java` 추가
         - `SingleResult.java` 추가
         - `ResponseManager.java` 추가
   - SimpleRestAPI2/202402071500 을 main 으로 Merge
     - Message properties 처리
       - application.yml 에 `spring.messages` 설정을 추가
       - 관련 Source
           - `MessageConfig.java` 추가
           - `exception.properties` 추가
           - `messages.properties` 추가
       - Message 설정
         ```yaml
         spring:
           messages:
             basename: messages/messages, messages/exception   # 각각 ResourceBundle 규칙을 따르는 쉼표로 구분된 기본 이름 목록
             always-use-message-format: false  # 인수가 없는 메시지도 구문 분석하여 항상 MessageFormat 규칙을 적용할지 여부
             encoding: UTF-8
             fallback-to-system-locale: true   # locale에 해당하는 file이 없을 경우 system locale을 사용 ( default : true )
             use-code-as-default-message: true # 해당 코드를 찾을 수 없을 경우 Code 를 출력한다. ( default : false )
             cache-duration: 3                 # default는 forever
             #cache-seconds: -1                # 로드된 자원 번들 파일 캐시 만료(초). -1로 설정하면 번들은 영원히 캐시됩니다.
         ```
6. 2024.02.08
   - SimpleRestAPI2/202402081000 branch 생성 
     - P6SPY 추가
       - 쿼리를 예쁘게 찍어보자.
       - 관련 Source
         - `P6SpySqlFormatter.java` 추가
         - `spy.properties` 추가
         ```properties
         #p6spy 설정
         appender = com.p6spy.engine.spy.appender.Slf4JLogger
         driverlist = org.postgresql.Driver
         logMessageFormat = com.p6spy.engine.spy.appender.MultiLineFormat
         ```
       - build.gradle
         - `implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.8.1'` 추가
   - SimpleRestAPI2/202402081000 을 main 으로 Merge
   - SimpleRestAPI2/202402081100 branch 생성
     - AOP 기능 추가
     - 각 트랜젝션 ID 당 유일한 Logging 처리
     - build.gradle
       - `implementation 'org.springframework.boot:spring-boot-starter-aop` 추가
     - 관련 Source
       - `TrackingAspect.java`, `AopConfig.java`, `MyThreadLocal.java`, `WebConst.java` 추가
   - SimpleRestAPI2/202402081100 을 main 으로 Merge
     - 일부 기능 수정

7. 2024.02.18
   - SimpleRestAPI2/20240218 branch 생성
     - JWT 추가 
       - `implementation 'org.springframework.boot:spring-boot-starter-security'`  
       - `implementation group: 'io.jsonwebtoken', name: 'jjwt-api', version: '0.11.5'`  
       - `runtimeOnly group: 'io.jsonwebtoken', name: 'jjwt-impl', version: '0.11.5'`  
       - `runtimeOnly group: 'io.jsonwebtoken', name: 'jjwt-jackson', version: '0.11.5'`
       - 관련 Source
         - `SecurityConfig.java`, `JwtSecurityConfig.java`, `AuthDTO.java`, `PrincipalDetails.java`, `PrincipalDetailsService.java` 추가
     - JWT 설정
       ```yaml
       custom:
         jwt:
           header: Authorization
           secretKey: MySecretKeyABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890ABCDE
           refresh-token-validity-in-seconds: 1209600000  # 14일
           #token-validity-in-seconds: 43200000            # 12시간 ( 3600000; // 1시간 )
           token-validity-in-seconds: 86400000            # 24시간
           cookie-expiration: 7776000000                  # 90일
       ```
     - Redis 추가
       - `implementation 'org.springframework.boot:spring-boot-starter-data-redis'`
     - Redis 설정
       ```yaml
       redis:
         host: localhost
         lettuce:
           pool:
             enabled: true
             max-active: 8       # pool에 할당될 수 있는 커넥션 최대수 (음수로 하면 무제한)
             max-idle: 8         # 풀에서 관리하는 idle 커넥션의 쵀소수 대상 (양수일 때만 유효)
             max-wait: -1        # pool이 바닥났을 때 예외 발생 전, 커넥션 할당 차단 최대 시간(단위 밀리세컨드, 음수는 무제한 차단)
             min-idle: 0         # 풀에서 관리하는 idle 커넥션의 쵀소수 대상 (양수일 때만 유효)
         port: 6379              # 레디스 서버 포트
         # 레디스 서버 이름
         #spring.redis.sentinel.master
         # 호스트: 포트 쌍 목록 (콤마로 구분)
         #spring.redis.sentinel.nodes=
         # 커넥션 타임아웃 (단위 밀리세컨드)
         timeout: 0
       ```
       - 관련 Source
         - `RedisRepositoryConfig.java`, `RedisService.java` 추가

8. 2024.02.21
   - Spring Security 때문에 화면이 Swagger 화면 오픈 불가
     - `SecurityConfig.java` 파일에 PERMIT_URL_ARRAY 설정 추가
       ```java
         private static final String[] PERMIT_URL_ARRAY = {
         /* swagger v2 */
         "/v2/api-docs",
         "/swagger-resources",
         "/swagger-resources/**",
         "/configuration/ui",
         "/configuration/security",
         "/swagger-ui.html",
         "/webjars/**",
         /* swagger v3 */
         "/v3/api-docs/**",
         "/swagger-ui/**"
         };
       ```
     - `JwtFilter.java` 파일에 swagger resouce 제외 설정 추가
     
9. 2024.02.22
   - 테스트 `http://localhost:8080/swagger-ui/index.html`
     - 입력 테스트
       ```json
          {
             "userId":"test"
           , "userNm":"홍길동"
           , "password":"A123456!"
           , "email":"test@naver.com"
           , "userRole":"USER"
           , "crtId":"test"
           , "mdfId":"test"
          }
       ```
       - Security 적용으로 Password 암호화 적용.
       - 기초데이터(`data-postgresql.sql`) 파일의 Password 도 암호화 해서 수정. 
       - 비밀번호 변경 : 1234 -> A123456!
     - 로그인 테스트
       ```json
          {
             "password": "A123456!"
           , "userId": "test"
          }
       ```
       - Response headers의 authorization 값을 확인해서 테스트 시 header 값을 적용해야 함.
         - `authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDg2NTIzNzAsInN1YiI6ImFjY2Vzcy10b2tlbiIsImh0dHBzOi8vbG9jYWxob3N0OjcwMDAiOnRydWUsInVzZXJJZCI6InRlc3QiLCJyb2xlIjoiUk9MRV9VU0VSIn0.LZpOoHc9Eyu_yrW4pThE90YC3L38UuIQyYBdOSt5cPssmzJgNbLqGtaAtYn00Ug2a2uAG-A32-8q8Y7bbeIZYw`
       