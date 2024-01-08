# Gateway-Service

## 🚀 프로젝트 소개

&nbsp;&nbsp; 이 Repository는 부트캠프 Final Project의 일부로 개발된 Microservices Architecture(MSA) 기반의 Gateway-Service에 관한 소스 코드를 포함하고 있습니다.
프로젝트는 [Job-A](https://github.com/miracle-job-a)에 대한 목적과 요구사항을 충족하기 위해 `2023-11-02 ~ 2023-12-20` 동안 진행되었습니다.
Gateway-Service는 모든 웹 클라이언트의 요청을 처리하는 기능을 제공합니다.

## 🏗 프로젝트 구조

프로젝트는 MSA 아키텍처에 기반하여 여러 개의 독립적인 서비스로 나뉘어져 있습니다. 제가 참여한 서비스는 다음과 같습니다.

- [User-Service](https://github.com/chocolaggibbiddori/miracle-job-a-user-service): 사용자 관리와 관련된 서비스
- [Gateway-Service](https://github.com/chocolaggibbiddori/miracle-job-a-gateway-service): 웹 사용자의 모든 요청을 처리하는 서비스
- [Util-Service](https://github.com/chocolaggibbiddori/miracle-job-a-util-service): 여러 서비스에서 사용되는 부가적인 기능을 담당하는 서비스

## 👥 역할 소개

### 나의 역할: Gateway-Service

- **주요 업무:** JWT 인증 필터 개발
- **필터의 역할:**
  - 로그인이 필요한 모든 요청에 대해서 JWT 인증을 수행
  - 쿠키에 토큰이 없거나, 유효하지 않은 토큰일 경우 401 에러 페이지 응답
- **문제점:**
  - 기존에는 사용자의 정보를 세션에 담아두고 사용하고 있었음
  - 로그인 유지를 JWT 인증으로 변경하려고 하니 세션에 정보를 담던 것이 문제가 됨
  - 토큰에 사용자 정보를 담아두고 토큰에서 사용자 정보를 꺼내 사용하는 방식으로 변경하려면 시간이 아주 많이 소요될 것으로 판단됨
- **해결:**
  - 로그인 유지는 JWT 인증으로 수행하되, 사용자의 정보는 기존에 사용했던 것처럼 세션에서 꺼내오는 것으로 유지하도록 함
  - JWT 인증 필터에서 토큰이 유효한 것으로 판단되면 세션에 해당 사용자의 정보를 담는 것으로 구현함

## 📌 참고 및 기타 정보

- 프로젝트에 대한 더 자세한 내용은 [프로젝트 전체 README](https://github.com/miracle-job-a)를 참고하세요.
- 기술 스택이나 특정 라이브러리에 대한 자세한 정보는 각 서비스의 README를 확인하세요.

---

프로젝트에 참여하면서 많은 것을 배우고 기여할 수 있어서 행복했습니다. 문제가 있거나 질문이 있다면 언제든지 연락해주세요!