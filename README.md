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
    - postgresql 설치 SimpleRestAPI/202402031300
    - 사용자 조회 / 수정 / 삭제 기능 추가

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

6. 2024.02.08
  - SimpleRestAPI2/202402081000 branch 생성 
    - P6SPY 추가
      - 쿼리를 예쁘게 찍어보자.
      - 관련 Source
        - `P6SpySqlFormatter.java` 추가
        - `spy.properties` 추가
      - build.gradle
        - `implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.8.1'` 추가
  - SimpleRestAPI2/202402081000 을 main 으로 Merge
  - SimpleRestAPI2/202402081100 branch 생성
    - AOP 기능 추가
    - 각 트랜젝션 ID 당 유일한 Logging 처리
    - build.gradle
      - `implementation 'org.springframework.boot:spring-boot-starter-aop` 추가
    - 관련 Source
      - `TrackingAspect.java` 추가
      - `AopConfig.java` 추가
      - `MyThreadLocal.java` 추가
      - `WebConst.java` 추가