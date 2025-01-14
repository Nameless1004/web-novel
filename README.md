# 픽션홀릭 프로젝트 (Fiction Holic)
## 프로젝트 개요
* 프로젝트명: 픽션홀릭 (Fiction Holic)
* 목표: 웹 소설 독자와 작가들을 위한 플랫폼 개발. 사용자 친화적이고 확장 가능한 웹 애플리케이션으로, 독자들에게는 다양한 소설을 쉽게 탐색할 수 있는 환경을, 작가들에게는 창작물을 자유롭게 게시하고 관리할 수 있는 공간 제공.
* 인원: Backend 1명
* 사용 기술 스택
  * Frontend - React, Vite, Vercel, JSP
  * Backend - Github Actions(CI/CD), Docker, AWS [route53, ec2, rds], JAVA, Spring Boot 3.3.*, Spring JPA, Redis, Spring Security, OAuth2.0, Query DSL
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
- [x] S3 프로필, 소설 커버, 회차 커버 사진 업로드
- [ ] 소설 검색
- [ ] 구독 및 실시간 알림 서비스
- [ ] 추천 소설 서비스

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
<details>
<summary>조회수 동시성 문제</summary>

**[배경]**

하나의 에피소드에 여러 사용자가 동시에 조회수 증가 요청을 보낼 때 데이터베이스에서 동일한 조회수 필드에 동시성 문제가 발생하여 정확한 조회수 증가가 이뤄지지 않았습니다. 동시성을 제어하여 정확한 조회수 증가를 보장하기 위해 적절한 동시성 제어 기법을 도입할 필요가 있었습니다.



**[문제 해결방안]**
* 동시성 문제를 해결하기 위해 락을 활용한 다양한 방법을 검토했습니다. 주요 대안은 다음과 같습니다.
* **낙관적 락**
  * 트랜잭션이 충돌을 가정하지 않고 작업을 진행한 후, 데이터 갱신 시점에 충돌을 검사하여 문제가 있으면 재시도 요청을 보내는 방식입니다.
  * 이 방식은 충돌 가능성이 낮은 환경에서 유리하지만, 충돌이 빈번한 환경에서는 재시도가 많아져 성능이 저하될 수 있습니다.
* **비관적 락**
  * 데이터 갱신 중 다른 트랜잭션이 동일한 데이터에 접근하지 못하도록 차단하여 충돌 자체를 방지하는 방식입니다.
  * 이 방식은 충돌 가능성이 높은 환경에서 더 안정적이고 성능도 효율적입니다.
* **분산 락**
  * 여러 애플리케이션이나 분산 환경에서 공통의 데이터에 대한 락을 관리하는 방식입니다.
  * 그러나 현재 시스템은 단일 애플리케이션 환경으로, 분산 락은 필요하지 않다고 판단하여 제외했습니다.

**[낙관적 락과 비관적 락 비교]**
> 테스트는 k6로 진행됐으며, 50명 10초는 50명이 10초동안 계속 요청을 보냈다는 의미입니다. (각 유저는 요청 후 0.5초간 sleep)
* **응답 시간**

![image](https://github.com/user-attachments/assets/92d97cfd-4a0c-43ca-908b-79972bcd81b6)

* **처리량**

![image](https://github.com/user-attachments/assets/0e10cc9c-2958-401a-a9d0-6f96f89352f9)

> **선택과 이유**
>
> 낙관적 락과 비관적 락을 비교한 결과, 충돌 가능성이 높은 조회수 데이터의 특성상 낙관적 락은 재시도 요청으로 인해 성능 저하가 심각했습니다. 반면, 비관적 락은 충돌을 사전에 방지하여 처리 속도가 빠르고 처리량도 높은 것으로 확인되었습니다. 이러한 이유로 **비관적 락을 도입하기로 결정**했습니다.

**[결과]**

비관적 락을 도입한 후, 동시에 다수의 사용자가 조회수를 갱신하는 경우에도 데이터 충돌이 발생하지 않고 정확한 집계가 이루어졌습니다. 성능 테스트 결과, 처리 속도와 안정성이 모두 개선되었으며, 조회수 관련 동시성 문제를 성공적으로 해결할 수 있었습니다.
이를 통해 데이터 정합성을 유지하면서도 시스템 성능을 보장할 수 있는 최적의 대안을 적용할 수 있었습니다.
</details>

<details>
<summary>실시간 현재 시간대 인기 소설 리스트 조회 슬로우 쿼리 문제</summary>

**[배경]**

실시간 인기 소설 리스트를 조회하는 API가 평균 4초 이상의 응답 시간을 기록하며 성능 병목 현상이 발견되었습니다. 이는 메인 페이지에 노출되는 주요 기능으로, 사용자 경험에 큰 영향을 미치는 문제였습니다.

문제는 아래와 같은 쿼리 구조에서 발생했습니다.
```sql
SELECT novel.id as novelId , COUNT(evl.id) AS episodeViewCount
FROM episode_view_log evl
LEFT JOIN episode ON evl.episode_id = episode.id
LEFT JOIN novel ON episode.novel_id = novel.id
LEFT JOIN user ON novel.author_id = user.id
WHERE evl.hour = 11
GROUP BY novel.id
ORDER BY episodeViewCount DESC
LIMIT 0, 9;
```

문제점
1. 조인 연산 증가로 인한 성능 저하
* `episode_view_log` -> `episode` -> `novel` 로 이어지는 다단계 조인이 쿼리 실행 시간을 증가시켰습니다.
2. 복잡한 실행 계획
* `GROUP BY`와 `ORDER BY` 작업이 많은 데이터를 처리하는 중간 결과에서 수행되어, 불필요한 데이터 연산량이 증가했습니다.

**[문제 해결방안]**

문제를 해결하기 위해 세 가지 방법을 고려해 보았습니다.

1. 스키마 수정 및 쿼리 최적화
2. 배치 작업으로 통계 처리
3. Redis에서 실시간으로 순위 통계 후 주기적으로 DB에 저장

그 중 스키마 수정 및 쿼리 최적화 방법을 선택한 이유는 다음과 같습니다

* 배치 작업: 배치 서버를 별도로 구축하고 운영해야 하므로, 추가적인 비용이 발생하게 됩니다.
* Redis 처리: Redis를 사용하여 실시간 통계를 처리하는 방법도 고려했으나, 현재 고가용성을 위한 Redis 설정이 되어 있지 않았습니다. 이로 인해 장애 발생 시 데이터 손실 가능성이나 시스템 안정성 문제가 우려될 수 있습니다.

따라서, 시스템의 안정성과 비용 효율성을 고려했을 때, 스키마 수정 및 쿼리 최적화 (1번) 방식이 가장 적합하다고 판단하여 이 방법을 선택하였습니다.

1. 스키마 변경
  * `episode_view_log` 테이블에 `novel_id` 컬럼을 추가하여 `novel`과 직접적인 연관관계를 맺도록 스키마를 변경했습니다.
  * 이를 통해 `episode` 테이블과의 조인을 제거하여 쿼리 복잡도를 낮추었습니다.

2. 개선된 쿼리
```sql
SELECT novel.id, COUNT(novel.id) AS counts
FROM episode_view_log
LEFT JOIN novel ON episode_view_log.novel_id = novel.id
WHERE episode_view_log.hour = 20 AND DATE(episode_view_log.timestamp) = CURDATE()
GROUP BY novel.id
ORDER BY counts DESC
LIMIT 0, 9;
```
<details>
 <summary>쿼리 수정 전 ANALYZE</summary>

 ```
-> Limit: 9 row(s)  (actual time=3520..3520 rows=9 loops=1)
-> Sort: episodeViewCount DESC, limit input to 9 row(s) per chunk  (actual time=3520..3520 rows=9 loops=1)
-> Table scan on <temporary>  (actual time=3519..3519 rows=9934 loops=1)
-> Aggregate using temporary table  (actual time=3519..3519 rows=9934 loops=1)
-> Nested loop inner join  (cost=10142 rows=252) (actual time=3.63..3482 rows=54520 loops=1)
-> Nested loop inner join  (cost=8858 rows=252) (actual time=3.62..3424 rows=54520 loops=1)
-> Inner hash join (no condition)  (cost=4841 rows=252) (actual time=3.6..114 rows=54520 loops=1)
-> Filter: (evl.`hour` = 11)  (cost=2420 rows=2518) (actual time=3.42..102 rows=54520 loops=1)
-> Index range scan on evl using FKiv8oy89xnca2wf0lt43gtoq7 over (NULL < episode_id), with index condition: (evl.episode_id is not null)  (cost=2420 rows=25180) (actual time=3.42..95.4 rows=54520 loops=1)
-> Hash
-> Covering index scan on user using PRIMARY  (cost=0.35 rows=1) (actual time=0.0149..0.0174 rows=1 loops=1)
-> Filter: (episode.novel_id is not null)  (cost=0.793 rows=1) (actual time=0.0606..0.0606 rows=1 loops=54520)
-> Single-row index lookup on episode using PRIMARY (id = evl.episode_id)  (cost=0.793 rows=1) (actual time=0.0604..0.0604 rows=1 loops=54520)
-> Filter: (novel.author_id = `user`.id)  (cost=0.25 rows=1) (actual time=828e-6..895e-6 rows=1 loops=54520)
-> Single-row index lookup on novel using PRIMARY (id = episode.novel_id)  (cost=0.25 rows=1) (actual time=624e-6..647e-6 rows=1 loops=54520)
```
</details>

<details>
 <summary>쿼리 수정 후 ANALYZE</summary>
 
 ```
-> Limit: 9 row(s)  (actual time=53.3..53.3 rows=9 loops=1)
-> Sort: episodeViewCount DESC, limit input to 9 row(s) per chunk  (actual time=53.3..53.3 rows=9 loops=1)
-> Table scan on <temporary>  (actual time=52.2..52.8 rows=9732 loops=1)
-> Aggregate using temporary table  (actual time=52.2..52.2 rows=9732 loops=1)
-> Nested loop inner join  (cost=6849 rows=5032) (actual time=2.99..44 rows=37252 loops=1)
-> Filter: ((evl.`hour` = 13) and (evl.novel_id is not null))  (cost=5088 rows=5032) (actual time=2.94..11.6 rows=37252 loops=1)
-> Table scan on evl  (cost=5088 rows=50320) (actual time=0.454..8.77 rows=50331 loops=1)
-> Single-row covering index lookup on novel using PRIMARY (id = evl.novel_id)  (cost=0.25 rows=1) (actual time=761e-6..778e-6 rows=1 loops=37252)
```
</details>

**[결과]**
* 성능 개선
  * 기존 쿼리에 비해 약 96.16%의 속도 개선을 달성했습니다. ( 4817ms -> 185ms : intelliJ Query Console 기준)
  * 복잡한 조인 연산을 줄이고, 데이터 접근 효율을 높임으로써 실행 시간이 크게 단축되었습니다.

* 추가 최적화
  * 캐싱 도입
    * POST Man으로 api를 테스트 했을 때 응답을 받기 까지 평균적으로 89ms가 나왔습니다. 인기 소설 조회의 경우 메인페이지에 있어 잦은 읽기 요청이 있어 캐싱을 해두기로하였습니다.
    * 캐싱 DB로 Redis를 사용하였습니다. Redis를 사용한 이유는 Docker로 추가적인 인프라 구축 없이 간편하게 실행할 수 있습니다. 또한, Redis는 기본적으로 무료로 제공되며, 지금과 같은 작은 규모의 시스템에서는 비용 부담 없이 충분히 사용할 수 있는 장점이 있어 사용하였습니다.
    * 조회수 기반으로 순위가 실시간으로 변화하므로, 캐시의 최신화가 중요했습니다. 이를 위해 TTL을 1분으로 설정하여 캐시가 1분 주기로 자동 갱신되도록 설정했습니다. 이로 인해 실시간으로 변화하는 조회수 순위를 반영하면서도, 데이터베이스에 과도한 부하를 주지 않고 빠른 응답 속도를 유지할 수 있습니다.
    * 캐싱을 도입하여 89ms -> 9ms로 약 89.89% 속도 개선을 하였습니다.
    * 캐싱 전후 비교
    
  ![image](https://github.com/user-attachments/assets/ec6c07ee-6d0f-4440-a8ba-0aad6e5f32b3)

</details>
