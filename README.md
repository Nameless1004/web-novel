# 픽션홀릭 프로젝트 (Fiction Holic)
## 프로젝트 개요
* 프로젝트명: 픽션홀릭 (Fiction Holic)
* 목표: 웹 소설 독자와 작가들을 위한 플랫폼 개발. 사용자 친화적이고 확장 가능한 웹 애플리케이션으로, 독자들에게는 다양한 소설을 쉽게 탐색할 수 있는 환경을, 작가들에게는 창작물을 자유롭게 게시하고 관리할 수 있는 공간 제공.
* 인원 1명
* 사용 기술 스택
  * Frontend - React, Vite, Vercel, JSP
  * Backend - Github Actions(CI/CD), Docker, AWS [route53, ec2, rds], JAVA, Spring Boot 3.3.*, Spring JPA, Redis, Spring Security, OAuth2.0
## 개발
- [x] 소셜 로그인
- [x] 일반 로그인 / 회원가입
- [x] JWT 토큰(액세스, 리프레쉬)활용한 인증/인가
- [x] 소설 CRUD
- [x] 회차 CRUD
- [x] 댓글 CRUD
- [x] 현재 시간대 실시간 인기 소설 목록 조회 및 캐싱
- [x] 프론트 배포
- [x] 백엔드 배포
- [ ] 구독 및 실시간 알림 서비스
- [ ] 추천 소설 서비스
- [ ] S3 프로필, 소설 커버, 회차 커버 사진 업로드

## 서비스 URL
[픽션홀릭](https://www.fictionholic.xyz)

## CI/CD PIPELINE
![image](https://github.com/user-attachments/assets/b3a80889-003b-402b-b3f7-6fd84bad2d5b)

## 구조
![image](https://github.com/user-attachments/assets/a7711603-4d75-43e0-aa19-7faaf87c5afd)

## API 명세서
[POST MAN Docs](https://documenter.getpostman.com/view/37553747/2sAYJAfJQw)
## ERD
![erd](https://github.com/user-attachments/assets/8dc2a860-7dab-4088-b27a-7f229b042e0b)

## 트러블 슈팅
### 조회수 동시성 문제
* 조회수 증가 시 동시성 문제가 발생
* 해결방안 세가지 DB락, 분산락
  * 현재는 분산 환경, 다중 어플리케이션이 아니여서 분산락은 제외
  * 낙관적 락과 비관적락을 비교
    * 비교결과 낙관적락이 충돌이 많을 경우 재시도 요청때문에 속도가 느린것을 확인 
    * 동시에 접근할 확률이 높은 경우 낙관적 락에 비해 비관적락이 속도도 빠르고 처리량도 높음
    * 따라서 비관적락으로 처리

### 실시간 현재 시간대 인기 소설 리스트 조회 슬로우 쿼리 문제
* 배경
  * 실시간으로 현재 시간대의 인기 소설 리스트 목록조회를 하는 부분에서 쿼리가 4초대가 나오는 것을 발견
* 문제 세부사항
```sql
SELECT
    novel.id AS novel_id,
    COUNT(episode_view_log.id) AS episodeViewCount
FROM
    episode
        INNER JOIN
    episode_view_log ON episode.id = episode_view_log.episode_id
        LEFT JOIN
    novel ON episode.novel_id = novel.id
        JOIN  
    user on user.id = novel.author_id
WHERE
    episode_view_log.hour = 19
GROUP BY
    novel.id
ORDER BY
    episodeViewCount DESC
LIMIT
    0, 9
```
해당 쿼리에서 EXPLAIN결과 episode_view_log테이블의 쿼리 실행 계획에서 ALL 스캔이 발생
* 원인 분석
    * episode_view_log테이블에는 novel_id가 저장되지 않고, episode_id를 통해 novel테이블과 간접적으로 연결
    * 조회 시 episode와 novel을 추가적으로 조인해야 하므로 실행 계획이 복잡해짐
* 해결방안
  * 위 문제를 해결하기 위해 episode_view_log 테이블의 스키마를 변경하여 novel과 직접적으로 연결
  * 조인 연산을 줄이고 실행 계획을 개선
* 결과
최종쿼리
```sql
SELECT
    novel.id,
    COUNT(novel.id) AS counts
FROM episode_view_log
         LEFT JOIN novel on episode_view_log.novel_id = novel.id
WHERE
    episode_view_log.hour = 20  AND DATE(episode_view_log.timestamp) = CURDATE()
GROUP BY
    novel.id
ORDER BY
  counts DESC
LIMIT
    0, 9
```
개선 후 약 95%의 속도 개선
* 추가 개선사항
* 캐싱
  * POST Man으로 api를 테스트 했을 때 응답을 받기 까지 평균적으로 40ms가 나왔습니다. 인기 소설 조회의 경우 메인페이지에 있어 잦은 읽기 요청이 있어 캐싱을 해두기로하였습니다.
  * 캐싱 db는 docker로 다루기 쉽고 공짜인 redis를 사용하기로 하였습니다.
  * 실시간으로 조회수에 기반하여 순위가 변경되기 때문에 캐시의 최신화는 5분 정도의 주기로 TTL로 캐시를 만료하고 다시 db에서 불러와서 저장하도록 하였습니다.
