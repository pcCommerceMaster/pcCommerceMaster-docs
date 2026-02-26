Admin / 인증 도메인 API

-- 인증 --
1. 관리자 회원 가입
POST /api/admins/signup

- 관리자 계정 생성 요청
- 입력값 Validation 수행
- 비밀번호 BCrypt 해시 처리
- 기본 상태 : STAND_BY (승인 대기)

2. 로그인
POST /api/admins/login

- 입력값 검증
- DB 조회
- BCrypt 비밀번호 비교
- 상태 확인 (ACTIVE 여부)
- 세션 발급

3. 로그아웃
POST /api/admins/logout

- 현재 세션을 무효화합니다.

처리 흐름
- 세션 존재 여부 확인
- 세션 invalidate()
- 세션 삭제

-- 내 정보 --
1. 내 프로필 조회
GET /api/admins/me

- 로그인된 관리자의 정보를 조회합니다.

처리 흐름
- 세션 인증 검증
- 세션에서 로그인 관리자 ID 추출
- 해당 관리자 조회
- DTO 변환 후 반환

2. 내 정보 수정
PUT /api/admins/me

- 로그인된 관리자의 기본 정보를 수정합니다.

처리 흐름
- 세션 인증 검증
- 로그인 관리자 ID 추출
- 입력값 Validation
- 엔티티 필드 변경
- 트랜잭션 종료 시 자동 반영

3. 비밀번호 변경
PATCH /api/admins/me/password

- 로그인된 관리자의 비밀번호를 변경합니다.

처리 흐름
- 세션 인증 검증
- 현재 비밀번호 BCrypt 검증
- 새 비밀번호 Validation
- 새 비밀번호 BCrypt 해시 처리
- 엔티티 비밀번호 변경
- 트랜잭션 반영

-- 관리자 관리 --
1. 관리자 승인
PATCH /api/admins/{id}/approve

- 가입 대기 중인 관리자를 승인합니다.
- 상태를 ACTIVE로 변경합니다.

처리 흐름
- 세션 인증 검증
- MASTER 권한 확인
- 대상 관리자 조회
- 상태 ACTIVE 변경
- 저장

2. 관리자 거절
PATCH /api/admins/{id}/reject

- 가입 요청을 거절합니다.
- 상태를 REJECT로 변경합니다.

처리 흐름
- 세션 인증 검증
- MASTER 권한 확인
- 대상 관리자 조회
- 상태 REJECT 변경
- 거절 사유 저장


3. 관리자 목록 조회
GET /api/admins

Query parameter
- keyword 이름/이메일 검색
- role 관리자 역할
- status 관리자 상태
- page 페이지 번호
- size 페이지당 개수
- sortBy 정렬 기준
- direction asc/desc

- 인증 및 권한 검증
- Specification 기반 동적 검색
- Pageable 적용
- DTO 변환 후 반환

4. 관리자 상세 조회
GET /api/admins/{id}

- 특정 관리자의 상세 정보를 조회합니다.

처리 흐름
- 세션 인증 검증
- 관리자 권한 확인
- ID 기반 관리자 조회
- 존재하지 않을 경우 예외처리
- DTO 변환 후 반환

5. 관리자 정보 수정
PUT /api/admins/{id}

- 관리자의 기본 정보를 수정합니다.

처리 흐름
- 세션 인증 검증
- 관리자 권한 확인
- 입력값 Validation
- 수정 대상 관리자 조회
- 엔티티 필드 변경 
- 트랜잭션 종료 시 자동반영

6. 관리자 역할 변경
PATCH /api/admins/{id}/role

- 관리자의 권한(Role)을 변경합니다.

처리 흐름
- 세션 인증 검증
- MASTER 권한 여부 확인
- 대상 관리자 조회
- 역할 변경
- 변경 내용 저장

7. 관리자 상태 변경
PATCH /api/admins/{id}/role

- 관리자의 계정 상태를 변경합니다.

처리 흐름
- 세션 인증 검증
- 권한 확인
- 대상 관리자 조회
- 상태 값 변경
- 저장

8. 관리자 삭제
DELETE /appi/admins/{id}

- 관리자를 삭제합니다.
- 실제 데이터는 삭제하지 않고 deletedAt 값을 설정하는 소프트 삭제 방식입니다.

처리 흐름
- 세션 인증 검증
- MASTER 권한 확인
- 대상 관리자 조회
- deletedAt 현재 시간 설정
- 저장

보안 설계
- 비밀번호는 BCrypt 해시 알고리즘을 사용하여 저장
- 상태 기반 로그인 제어 (STAND_BY / REJECT 차단)
- 역할 기반 접근 제어
- 동적 검색은 JPA Specification 활용

설계 의도 
관리자 도메인은 승인 기반 계정 관리와 역할 중심 제어를 통해
보안성을 강화하였으며, 동적 검색 및 페이징 기능을 적용하여 
확장성을 고려해 설계되었습니다.