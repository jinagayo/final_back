## :computer: 온라인 코딩 강의 플랫폼 (CodeSpark)
[<시연영상>](https://drive.google.com/file/d/1x0s7IEK62txPZf5CjHYP2imQYgtcCqZ6/view?usp=drive_link)    [<피피티>](https://docs.google.com/presentation/d/1nsRLjXnvtHFbDcE-QtZKq04RnSYLJ4Tz/edit?usp=drive_link&ouid=111927043237543963840&rtpof=true&sd=true)   


---
### :mag: 프로젝트 소개
**CodeSpark**는 강사와 학습자를 연결하는 온라인 코딩 강의 플랫폼입니다. <br>
강의 생성부터 동영상 스트리밍, 과제 제출, 학습 진도율 관리까지 학습에 필요한 모든 기능을 제공하며, 실제 개발 환경과 유사한 학습 경험을 제공합니다.<br>
이 시스템을 통해 강사는 손쉽게 온라인 코딩 강의를 개설하고, 학생들은 어디서든 강의를 수강하며 과제를 수행하고 질의응답을 할 수 있습니다.   <br>
이러한 기능을 하나의 플랫폼에서 제공함으로써 효율적인 비대면 코딩 교육 환경을 구축하는 것이 이 프로젝트의 주된 목적입니다.  <br>


---
### 📅 개발 기간
> 2025.07.10 ~ 2025.08.11

---
### 👨‍👩‍👧‍👦 멤버 구성
| 이름 | 담당업무 |
|:----------|:----------:|
| 윤진아	| 비밀번호 찾기, S3 동영상 업로드 및 스트리밍, 배포 등|
| 이채원	|비밀번호 변경, 결제시스템, 강의관리 등 |
| 장혜민	|로그인, 회원가입, 게시판 등 |

---
### ⚙️ 기술스택

#### 🎨 Frontend
- Bootstrap (UI 프레임워크)
- React

#### 🗄️ Backend
- Spring Boot
- Spring Security 
- JPA(Hibernate)
- MariaDB/MySQL


#### :large_blue_diamond: Infrastructure

- AWS (EC2, S3)
- Docker, GitHub Actions



#### 🧰 Development Tool
- Eclipse IDE
- vs CODE
- HeidiSQL
- Git / Github


****
### 📌 주요 기능
- 🔑 **회원가입 및 로그인:** 수강생과 강사 계정을 구분하여 회원가입을 지원하며, 강사로 회원가입할 경우 관리자 승인을 받아야 합니다
. Spring Security 기반의 인증을 적용하여 안전한 로그인/로그아웃 기능을 제공합니다.

- 🔍 **비밀번호 변경 및 찾기:** 사용자가 비밀번호를 변경하거나 분실 시 이메일 인증을 통해 재설정할 수 있는 기능을 제공합니다
. Gmail SMTP를 활용한 인증 코드 발송으로 보안성을 높였습니다.

- 🎥 **동영상 업로드 & 스트리밍:** 강의 동영상을 AWS S3에 업로드하고 Pre-Signed URL을 통해 스트리밍합니다
. 이를 통해 강의 영상을 안전하게 제공하고 대용량 파일도 효율적으로 전송할 수 있습니다.

- 👩‍🏫 **강사 전용 기능:** 강사는 새로운 강의를 개설하고(강의 신청), 강의 자료 업로드, 시험/과제 등록을 할 수 있습니다
. 업로드된 자료는 수강생들에게 바로 공유되며, 강의 개설 신청은 관리자의 승인을 받아 공개됩니다.

- 🧑‍🎓 **학생 전용 기능:**  학생은 강의 목록을 보고 수강 신청(결제) 후 강의를 수강할 수 있으며, 강의별 과제 제출, 시험 응시를 할 수 있습니다
. 또한 자신이 수강 중인 강의들의 진도율을 실시간으로 확인할 수 있습니다.

- 👮 **관리자 전용 기능:**  관리자는 플랫폼의 전체 운영을 담당하며, 메인 페이지 배너 관리, 회원 관리(학생/강사 승인), 강의 개설 승인 등 관리자 전용 대시보드 기능을 제공합니다
. 이를 통해 부적절한 콘텐츠나 사용자 계정을 관리하고 플랫폼의 품질을 유지합니다.

- 📊 **학습 진도 관리:**  수강생의 영상 시청 기록을 자동 저장하여 진도율을 산출하고, 강의별 전체 진행 상황을 한눈에 볼 수 있습니다
. 진도 정보는 강의 자료 조회 시 자동으로 생성됩니다.

- 📝**게시판 (커뮤니티):**  공지사항, Q&A, 강의별 토론 게시판 등 다양한 게시판을 제공합니다
. 수강생과 강사는 질문 답변을 주고받거나 정보를 공유할 수 있으며, 게시글에 파일 업로드도 지원됩니다.

- 🔐 **권한 분리 및 보안:** Spring Security를 통해 관리자/강사/학생별 권한을 엄격히 구분하고 접근 권한을 제어합니다
. 또한 중요 데이터는 암호화하여 저장하고, AWS S3 및 데이터베이스 접근 시 보안 설정을 통해 안전한 서비스 운영을 보장합니다.

---

### 📡 주요 API 엔드포인트 및 예시

백엔드는 RESTful API 형식으로 구현되어 있으며, 주요 기능별로 다음과 같은 엔드포인트를 제공합니다 <br>
(모든 경로 앞에 기본 URL http://localhost:8080 생략)

**인증 (Auth):**

POST /auth/login – 사용자 로그인 (아이디와 비밀번호를 JSON으로 전송).   성공 시 사용자 정보와 세션이 생성됨.

POST /auth/logout – 로그아웃 요청 (세션 무효화 및 완료 메시지 반환).

GET /auth/check – 현재 로그인 상태 및 세션에 저장된 사용자 정보 확인.

<br>

**회원 가입 및 계정 관리:**

POST /join/signup/student – 학생 회원가입 요청  (필수 정보: 아이디, 비밀번호, 이름, 이메일 등).

POST /join/signup/teacher – 강사 회원가입 요청 (관리자 승인이 필요함).

GET /join/check-userid/{userid} – 아이디 중복 확인

GET /join/check-email/{email} – 이메일 중복 확인 및 존재 여부 확인

POST /join/userDelete/{userid} – 회원 탈퇴 (로그인 세션 상태에서 비밀번호 확인 후 탈퇴 처리)

<br>

**강의 (Courses):**

GET /course/List – 전체 강의 목록 조회 (모든 공개 강의를 리스트로 반환)

GET /course/Detail?class_id={id} – 특정 강의의 상세 정보 조회 (강의 정보, 소개, 평점, 리뷰 목록 등 반환)

POST /course/teacher/formsubmit – 강사가 새 강의 개설을 신청 (강의 제목, 소개, 커리큘럼 등 폼 데이터 전송).

POST /course/teacher/upload-image – 강의 썸네일 등 이미지 파일 업로드 (멀티파트 파일을 S3에 업로드하고 경로 반환)

GET /course/teacher/List – 강사가 자신이 개설한 강의 신청 내역 및 상태 조회

(그 외에도 결제 및 수강신청 관련 /course/Payment 엔드포인트들과 관리자용 승인 처리 엔드포인트 등이 구현되어 있습니다.)

<br>

**동영상 및 자료 (Video):**

POST /video/upload – 강의 영상 업로드를 위한 Pre-Signed URL 발급.  클라이언트가 이 URL로 직접 S3에 영상을 업로드

POST /video/save – 업로드 완료된 영상에 대한 메타데이터 저장 (강의 자료 등록)

GET /video/material/{id} – 특정 강의 자료(영상 또는 노트)를 조회. 학생이 처음 해당 영상을 재생하면 진도율 추적을 위한 초기 레코드가 생성됨

GET /video/stream?key={s3ObjectKey} – 주어진 강의 영상 파일 키에 대한 스트리밍 URL(Pre-Signed URL)을 생성하여 반환. 이 URL을 통해 일정 시간 동안 해당 영상 스트리밍 가능

<br>

**게시판 (Board):**

GET /board/list?boardnum={코드} – 게시판 글 목록 조회 (공지사항, Q&A 등 boardnum에 따라 분류)
 페이지네이션 및 검색, 정렬을 지원

GET /board/detail/{id} – 게시글 상세 조회 및 내용 반환

POST /board/write/{boardnum} – 새 게시글 작성 (제목, 내용 그리고 파일 첨부(Optional)를 멀티파트로 전송).

PUT /board/edit/{id} – 기존 게시글 수정 (게시글 ID에 대응되는 글의 제목/내용 등을 수정).

POST /board/notices/{id}/view – 게시글 조회수 증가 처리

<br>
(댓글 작성/삭제 등 추가 엔드포인트도 /board/comment... 형식으로 구현되어 있습니다.)


---
### :camera: 데모 스크린샷
> 아래는 실제 시스템 UI의 예시입니다.

  - 메인화면
  <img width="601" height="315" alt="Image" src="https://github.com/user-attachments/assets/285456c0-a1e4-4131-a358-e8c41765ee65" />
  
  - 강의 상세
  <img width="651" height="319" alt="Image" src="https://github.com/user-attachments/assets/6df5420e-59cd-4485-a1dd-0cf55e95ed7f" />
  
  - 강의 수강
  <img width="658" height="338" alt="강의수강" src="https://github.com/user-attachments/assets/96a503a7-5472-4803-81b4-f848983f1dcc" />

