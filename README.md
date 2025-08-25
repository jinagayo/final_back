## :computer: 온라인 코딩 강의 플랫폼 (CodeSpark)
final project

---
### :mag: 프로젝트 소개
**CodeSpark**는 강사와 학습자를 연결하는 온라인 코딩 강의 플랫폼입니다.
강의 생성, 동영상 스트리밍, 과제 제출, 진도율 관리 등 학습에 필요한 모든 기능을 제공하며, 실제 개발 환경과 유사한 학습 경험을 제공합니다.




---
### 📅 개발 기간
> 2025.07.10 ~ 2025.08.11

---
### 👨‍👩‍👧‍👦 멤버 구성
| 이름 | 담당업무 |
|:----------|:----------:|
| 윤진아	| 비밀번호 찾기, S3 동영상 업로드 및 스트리밍, 파일관리 등|
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
- 🔑 **회원가입 및 로그인** : 수강생, 강사로 구분하여 회원가입 구현. 강사 회원가입시 관리자 승인 필수
- :mag: **비밀번호 변경 및 찾기** : 비밀번호를 변경 및 메일인증을 통해 찾기가 가능합니다. 

- 🎥 **동영상 업로드 & 스트리밍:** AWS S3 + Pre-Signed URL을 활용한 안전한 강의 영상 제공

- 👩‍🏫 **강사 전용 기능**: 강의 생성, 자료 업로드, 시험/과제 등록

- 🧑‍🎓 **학생 전용 기능**: 강의 시청, 과제 제출, 시험 응시, 진도율 확인, 수강신청
- :cop: **관리자 전용 기능**: 배너관리, 학생관리, 강사관리, 강의개설승인 등

- 📊 **학습 진도율 관리**:시청 기록 기반 자동 저장 & 전체 진행 상황 확인
- :clipboard: **게시판** : 공지게시판, QNA 게시판, 과목별 게시판으로 구분

- 🔐 **권한 분리** : Spring Security로 관리자/강사/학생 역할 구분

---
### :camera: 데모 스크린샷
> 아래는 실제 시스템 UI의 예시입니다.

  - 메인화면
  <img width="601" height="315" alt="Image" src="https://github.com/user-attachments/assets/285456c0-a1e4-4131-a358-e8c41765ee65" />
  
  - 강의 상세
  <img width="651" height="319" alt="Image" src="https://github.com/user-attachments/assets/6df5420e-59cd-4485-a1dd-0cf55e95ed7f" />
  
  - 강의 수강
  <img width="658" height="338" alt="강의수강" src="https://github.com/user-attachments/assets/96a503a7-5472-4803-81b4-f848983f1dcc" />

