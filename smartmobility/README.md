# ITSK SmartMobility 성능검사 시스템
* ITSK에서 SmartMobility 과제의 성능검사를 위해 외주를 부탁한 프로젝트 페이지 3개로 구성되어있음
* 이 프로젝트의 경우 DB를 내장 SQLite를 썻기 때문에 다른 DB처럼 네트워크로 연결하지 않음

### 1. 개발환경
* Windows 10
* eclipse
* JDK 1.8
* Spring Boot 2.2.0 RELEASE
* Vue 2
* SQLite

### 2. 접속
```
http://192.168.34.133:10001
```

### 3. 빌드 및 배포(테스트서버)
* 첫번째로 frontend 소스를 빌드한다.
```
$ cd frontend
$ npm install
$ npm run build
```

* 두번째로 backend 소스를 빌드 및 패키징 한다.
```
$ mvnw clean package
```

* 마지막으로 빌드되고 패키징된 target/smps-x.x.x.jar 파일과 SQLite smartmobility.db 파일을 같은 폴더(디렉토리)에 놓는다.
* 이클립스에서는 smartmobility.db 파일을 프로젝트 폴더에 위치시키고 Spring Boot Application을 실행하면 구동된다.
```
$ java -jar smps-x.x.x.jar // 프로그램을 실행한다.
```

* 배포는 GitLab의 CI/CD 기능을 사용하여 자동적으로 빌드와 배포가 이루어지고 192.168.34.133 서버에 Docker 컨테이너로 배포된다. 따라서 배포가 완료되면 접속URL로 접속하여 확인이 가능하다.
* Devops 정보는 .gitlab-ci.yml 과 Dockerfile을 참조하면 됨.