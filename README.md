Simple REST API Version 2
=
# 목적
- Git 적 사고방식으로 프로젝트를 진행해보자.
- 무엇을 진행해야 하는지 고민하고, 프로그램을 짜야 겠구만...

## History
1. 2024.02.02
  - SimpleRestAPI/20240202 생성
    - Git 명령어  
        `git add .`  
        `git commit -m "Hello World RestController 생성"`  
        `git push --set-upstream origin SimpleRestAPI/20240202`
    - Hello World RestController 생성
      - '/' 이하는 태그(tag)임. 수정할 수 있음.
      - 문제
        - 날짜를 태그로 생성하니, 오늘 추가적인 작업을 하려면 태그가 중복됨.
        - 일단 날짜 + 시간 + 분까지 변경.
        - Git 명령어
            `git branch -m SimpleRestAPI/202402021500` or `git branch -m SimpleRestAPI/20240202 SimpleRestAPI/202402021500`
            `git push origin -u SimpleRestAPI/202402021500`
 