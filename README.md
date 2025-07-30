# WannaB 📚

> NHN ACADEMY 최종 프로젝트 <br>
> 도서 쇼핑 사이트
### Wannab 도메인
[📎 WannaB](https://wannab.shop/)

## 📜 목차
- [🗓️ 프로젝트 개요](#프로젝트-개요)
  - [진행 기간](#진행-기간)
  - [팀 구성](#팀-구성-)
- [📢 서비스 소개](#서비스-소개)
- [🥳 서비스 설계](#서비스-설계)
  - [기술 스택](#기술-스택)
  - [ERD](#erd)
  - [Wireframe](#wireframe)
  - [Architecture](#architect)
  - [Docs](#docs)
- [🤗 기능 소개](#-기능-소개)
- [😆 느낀점](#-느낀점)

## 🗓️ 프로젝트 개요

### 진행 기간

- 2025.06.09 ~ 2025.07.25 (7주)
<br>

## 팀 구성 🔥
|             [고충원](https://github.com/won-ee)              |             [김동균](https://github.com/dkyun97)              |              [김영대](https://github.com/yeong-dae)              |             [김훈민](https://github.com/gnsals0904)              |
|:---------------------------------------------------------:|:----------------------------------------------------------:|:-------------------------------------------------------------:|:--------------------------------------------------------------:|
| <img width="130px" src="https://github.com/won-ee.png" /> | <img width="130px" src="https://github.com/dkyun97.png" /> | <img width="130px" src="https://github.com/yeong-dae.png" /> | <img width="130px" src="https://github.com/gnsals0904.png" /> |
|                  Front, CI/CD, 도서 서비스                   |                  결제, 쿠폰 서비스                             |                                                             |      팀장, CI/CD, 도서 서비스                                      |

|            [박지후](https://github.com/jihoo1214)            |             [양재원](https://github.com/yang-jaewon)              |             [임재은](https://github.com/LimJaeEun0930)              |             [정민수](https://github.com/Minsooooooo)              |
|:---------------------------------------------------------:|:----------------------------------------------------------:|:--------------------------------------------------------------:|:--------------------------------------------------------------:|
| <img width="130px" src="https://github.com/jihoo1214.png" /> | <img width="130px" src="https://github.com/yang-jaewon.png" /> | <img width="130px" src="https://github.com/LimJaeEun0930.png" /> | <img width="130px" src="https://github.com/Minsooooooo.png" /> |
|                            회원 서비스                         |                         회원 서비스                             |                         주문 서비스, 장바구니 서비스                        |                             주문 서비스                                |



<br>

## 서비스 소개
[📎 YouTube Link](https://www.youtube.com/watch?v=FNd26obeZ-I)

<br>

## 서비스 설계
### 기술 스택
### 주요 기술 스택

| **구분** | **Frontend** | **Backend** |
| --- | --- | --- |
| **Language** | HTML, CSS | Java **21** |
| **IDE** | IntelliJ | IntelliJ |
| **Framework** | - | Spring Boot, Spring Security, Spring Web, Spring JPA, Spring Cloud (Gateway, Config, OpenFeign, Eureka), Spring AOP, Spring Session Redis |
| **Library** | Tailwind CSS | jjwt, Lombok, OpenFeign, Spring Validation (Jakarta), Spring Mail, Commons DBCP2, QueryDSL, Spring REST Docs, RabbitMQ, Actuator |
| **Template** | Thymeleaf | - |

### 공통 기술 스택

| **구분** | **기술** |
| --- | --- |
| **DB** | MySQL, Redis, H2 |
| **Infra** | GitHub Action, Docker, Nginx, Elasticsearch, RabbitMQ, Eureka, Config Server, MinIO |
| **Tools** | GitHub, Notion, Dooray, Wiki |
| **Build** | Maven, Spring Boot Maven Plugin, **APT Plugin**, Asciidoctor, Spring Configuration Processor, **JaCoCo (Test Coverage)** |
| **Test** | Spring Boot Test, Spring Security Test, Spring REST Docs, Spring Rabbit Test, Spring Cloud Contract Stub Runner |

### CI/CD
<img width="2772" height="636" alt="Group 1" src="https://github.com/user-attachments/assets/af60b58f-aa5e-4756-ac24-c40b5c829e07" />


### ERD
[📎 Wannab ERD 구조도](https://www.erdcloud.com/d/pxzhyqs65xYJiXHvJ)
<img width="5290" height="2932" alt="Copy of Wannab" src="https://github.com/user-attachments/assets/fe0160a1-2421-4a03-bd3f-0ea991d3d6ef" />

### Architect
<img width="3173" height="1155" alt="WannaB-just-Architecture" src="https://github.com/user-attachments/assets/10002993-fac9-4a7f-8ae8-a93a01c2068a" />

### Wireframe

[📎 Figma Link](https://www.figma.com/community/file/1529710551736698846)

### Docs
[📎 WannaB Team wiki](https://github.com/nhnacademy-be10-WannaB/WannaB-wiki/wiki)

## 🤗 기능 소개
<!-- TODO: 기능 소개 작성 -->
### **🏗️  인프라**

- 담당자 : 고충원, 김훈민
    - CI/CD
    - NHN CLOUD 기반 배포
    - Docker를 활용한 컨테이너화
    - 블루 그린 및 롤링 방식의 무중단 배포 적용
    - Nginx를 활용한 로드 밸런싱 및 리버스 프록시 설정

### **🖥️  프론트(figma ,html, css)**

- 담당자 : 고충원
    - 피그마 및 html,css 구현

### **👤 회원**

- 담당자: 박지후, 양재원
    - jwt인증 : 박지후
    - oauth2 : 양재원

### 👑 **등급**

- 담당자: 양재원
    - 회원 등급 변경, 휴면 전환

### **📮 주소**

- 담당자: 박지후
  - 주소 생성, 수정, 삭제 기능 구현

### **💰포인트**

- 담당자: 박지후, 양재원
  - 포인트 적립/취소, 사용 기능 구현
  - 포인트 정책 생성, 조회, 수정 기능 구현

### **📝 리뷰**

- 담당자: 고충원
    - 도서별 리뷰 상세페이지 구현
    - 회원별 리뷰 상세페이지 구현
    - 리뷰 CRUD 구현

### **🎟️ 쿠폰**

- 담당자: 김동균
  - 쿠폰 CRUD 구현
  - RabbitMq 도입
  - 다양한 예외상황 처리
  
### **📖 도서**

- 담당자: 고충원, 김훈민
    - 도서 조회, 등록, 수정, 삭제 기능 구현
    - 도서 정렬(최신순, 제목순, 가격순) 기능 구현
    - 알라딘 API 기반 도서 등록 구현

### **🔍 검색**

- 담당자: 김훈민
  - Elasticsearch 기반 검색 기능 구현
    - nori, jaso 등을 통한 한국어 맞춤 검색
    - index properties 가중치로 통합 검색
    - 초성 저장 파이프라인 구축으로 초성 검색

### **☑️ 카테고리**

- 담당자: 김훈민, 김동균
  - 카테고리 계층정보 조회 Spring Cache로 성능 개선
    
### **❤️좋아요**

- 담당자: 고충원
    - 좋아요 CRUD 구현
    - 회원별 좋아요 내역 페이지 구현

### **📱 주문**

- 담당자: 임재은, 정민수
    - 회원 및 비회원 주문 기능 구현
    - 서버가 여러개인 경우 고려하여 레디스에 주문관련 정보 임시저장
    - 결제 완료 및 트랜잭션 커밋 이후 이벤트리스너를 통해 유저 포인트 차감/적립, 쿠폰처리, 도서재고 차감 비동기요청 RabbitMQ이용하여 구현
    - 회원 비회원 주문 조회 구현
    - 환불, 취소 기능 구현 및 그에 따른 포인트 롤백
    - 주문 상태변경 및 스케줄러를 활용한 TTL 구현
    - 주문-결제까지 완료시 SMTP이메일전송 이벤트 처리

### **🛒 장바구니**

- 담당자: 임재은
    - 회원 및 비회원의 장바구니 상품 추가, 수량변경, 삭제기능 구현
    - Redis 활용 및 회원의 경우 데이터베이스에 장바구니 영구저장되도록 구현

### **💳  결제**

- 담당자: 김동균
  - TossPayments API 사용
  - 성공,실패 처리 로직

### 🛍️ **포장지**

- 담당자: 정민수
    - CRUD 구현

### **🚚 배송**

- 담당자 :정민수
    - 배송비 정책 CRUD 구현

### 💾 **파일**

- 담당자: 고충원
    - Minio 를 이용하여 image 저장, 호출

## 😆 느낀점
<!-- TODO: 개인 느낌점 작성 -->
<table border="1" cellspacing="0" cellpadding="10">
  <!-- 고충원 -->
  <tr>
    <th rowspan="3">
      <a href="https://github.com/won-ee">고충원</a><br />
      <img width="130px" src="https://github.com/won-ee.png" /><br />
      Front, CI/CD, 도서 서비스
    </th>
    <td colspan="2" rowspan="3">프론트엔드 중심으로 개발을 시작했지만, 이번 프로젝트를 통해 백엔드와 인프라까지 직접 구현해보면서
서비스 전체 흐름을 처음부터 끝까지 다뤄보는 경험을 할 수 있었습니다.<br/>
백엔드에서 API 설계와 로직 구현을 경험하며, 이전에는 간접적으로만 이해했던 서버와의 상호작용을 기술적으로 깊이 있게 체감했고, 이를 통해 문제 해결 역량도 키울 수 있었습니다.<br/>
또한 GitHub Actions를 활용한 CI/CD 구성, Docker를 통한 자동화 배포 등 인프라 영역까지 다뤄보며, 단순한 기능 구현을 넘어 실제 운영 가능한 서비스를 만드는 과정을 경험했습니다.<br/>
전체 사이클을 직접 겪어보며, 개발자에게 있어 단일 역할에만 머무르지 않고 시스템 전반을 이해하려는 시야가 얼마나 중요한지 체감할 수 있었고, 이제는 협업 시에도 프론트와 백, 인프라의 관점을 함께 고려해 더 효과적으로 커뮤니케이션하고 문제를 해결할 수 있다는 자신감을 갖게 되었습니다.</td>
  </tr>
  <tr></tr>
  <tr></tr>

  <!-- 김동균 -->
  <tr>
    <th rowspan="3">
      <a href="https://github.com/dkyun97">김동균</a><br />
      <img width="130px" src="https://github.com/dkyun97.png" /><br />
      결제, 쿠폰 서비스
    </th>
    <td colspan="2" rowspan="3">프로젝트를 시작하기전엔 MSA 아키텍처를 전혀 모르는 상태에서 8주간 잘 해낼 수 있을까에 대해서 많은 두려움이 있었지만 결제&쿠폰 서비스를 맡으면서 MSA 전체적인 흐름 및 API에서의 책임 및 역할 그리고 다양한 예외상황을 겪어보는 아주 유의미한 시간을 보냈던거같습니다.</td>
  </tr>
  <tr></tr>
  <tr></tr>

  <!-- 김영대 -->
  <tr>
    <th rowspan="3">
      <a href="https://github.com/yeong-dae">김영대</a><br />
      <img width="130px" src="https://github.com/yeong-dae.png" /><br /> 
    </th>
    <td colspan="2" rowspan="3">2달동안 프로젝트를 진행하면서 스스로에 대한 자바에 관련 지식이 부족 하다는 사실을 많이 알게 되었고  프로젝트를 통해 앞으로 어떤식으로 공부를 해야되고 부족한 정보 지식들이 뭔지 알게 되었다. <br/>
      그리고 어떤식으로 코딩을 하고 프로젝트 진행 방식들을 알게 되는 계기가 되었다.</td>
  </tr>
  <tr></tr>
  <tr></tr>

  <!-- 김훈민 -->
  <tr>
    <th rowspan="3">
      <a href="https://github.com/gnsals0904">김훈민</a><br />
      <img width="130px" src="https://github.com/gnsals0904.png" /><br />
      팀장, CI/CD, 도서 서비스
    </th>
    <td colspan="2" rowspan="3">처음에 팀장으로 8인 팀을 이끌게 되어 걱정이 많았습니다😢<br/> 
      MSA 기반의 프로젝트를 처음 진행하는 만큼 CI/CD 구축과 아키텍쳐 구조를 설계하는데 어려움이 있었지만, 팀장으로서 책임감을 가지고 공부한 결과 잘 마무리 할 수 있었던 것 같습니다.<br/> 
      그 덕분에 정말 많은 것들을 배울 수 있었습니다. 팀원 분들이 모두 열심히 해준 덕분이라고 생각합니다..! 모두 고생하셨습니다 🎉
    </td>
  </tr>
  <tr></tr>
  <tr></tr>

  <!-- 박지후 -->
  <tr>
    <th rowspan="3">
      <a href="https://github.com/jihoo1214">박지후</a><br />
      <img width="130px" src="https://github.com/jihoo1214.png" /><br/>
      회원 서비스
    </th>
    <td colspan="2" rowspan="3">msa 환경으로 프로젝트를 진행하면서 실제로 운영하는 사이트들이 이런 방식으로 만들어져있다는 것을 이해할 수 있었고 배포, 보안, 외부 api 등 많은 분야중에서 일부분만 담당하였지만 나중에는 모든 분야를 이해해서 팀원들과 소통할 수 있는 개발자가 되고싶습니다. </td>
  </tr>
  <tr></tr>
  <tr></tr>

  <!-- 양재원 -->
  <tr>
    <th rowspan="3">
      <a href="https://github.com/yang-jaewon">양재원</a><br />
      <img width="130px" src="https://github.com/yang-jaewon.png" /><br />
      회원 서비스
    </th>
    <td colspan="2" rowspan="3">프로젝트를 진행하면서 기존 기능을 리팩토링하거나 새로운 기능을 추가하는 과정에서, 의도치 않게 기존 동작이 깨지는 경우가 자주 발생했고 이를 디버깅하는데 많은 시간을 소요했습니다.<br/> 
      이를 통해 코드의 안정성과 일관성을 유지하기 위해 테스트 코드가 필요함을 실감했습니다.<br/>
      개발 환경에서는 문제상황을 쉽게 확인할 수 있지만, 배포 환경에서는 로그를 통해서만 흐름을 파악 할 수 있어 디버깅에 어려움을 겪었습니다. <br/>
      이를 통해 흐름 및 요청을 파악할 수 있는 로그를 남기는 것이 중요함을 알 수 있었습니다.<br/>
      모놀리스환경에서는 IDE나 도구의 지원 덕분에 리팩토링이 수월했지만, MSA에서는 API를 타 서비스에서 활용할 경우 추적 및 변경이 어려우므로 요청/응답 객체와 API 인터페이스에 대한 사전 설계 및 컨벤션 정립의 중요함을 알 수 있었습니다.</td>
  </tr>
  <tr></tr>
  <tr></tr>

  <!-- 임재은 -->
  <tr>
    <th rowspan="3">
      <a href="https://github.com/LimJaeEun0930">임재은</a><br />
      <img width="130px" src="https://github.com/LimJaeEun0930.png" /><br />
      주문 서비스
    </th>
    <td colspan="2" rowspan="3">개인프로젝트 외에 다수의 팀원들과 함께 체계적으로 프로젝트를 해본 경험은 처음인데, 두달이라는 기간동안 많은 것을 배운것 같다. <br/>
      팀원들과 협업하는 과정에서 커뮤니케이션의 중요성을 알게 되었고, 몰입의 중요성을 알게 되었다. <br/>
      MSA환경에서 처음 개발을 해보았는데 모놀리식 아키텍쳐에선 생각할 수 없었던 것을 깨달게 되었다.<br/> 
      프로젝트과정에서 부족한 부분은 수료 이후에 보충할 수 있도록 해야겠다.</td>
  </tr>
  <tr></tr>
  <tr></tr>

  <!-- 정민수 -->
  <tr>
    <th rowspan="3">
      <a href="https://github.com/Minsooooooo">정민수</a><br />
      <img width="130px" src="https://github.com/Minsooooooo.png" /><br />
      주문 서비스
    </th>
    <td colspan="2" rowspan="3">긴 시간동안 팀 프로젝트를 해본경험이 처음인데 팀원들에게 많은것을 배워가는 프로젝트가 됐고, MSA 아키텍쳐 환경이나 Java, Spring 등 웹 개발에 대한 기본적인 지식을 얻어가는거 같아서 정말 유의미한 시간을 보내고 프로젝트를 끝마쳤습니다 </td>
  </tr>
  <tr></tr>
  <tr></tr>
</table>
