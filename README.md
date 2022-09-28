# 촘촘한 보안망, Spyder
![Logo](https://logosbynick.com/wp-content/uploads/2018/03/final-logo-example.png)



## 프로젝트 소개
- Spyder는 기존 국방모바일보안 앱을 보완한 보안 솔루션으로, 카메라 차단 기능뿐만 아니라, 휴대폰 안에 저장된 사진들을 검사하여 국방보안에 위험요소가 있는지 분석해줍니다. 처음 입대하거나 휴가에서 복귀하는 장병들은 Spyder을 활성화 시킴으로서 카메라 기능을 차단하고 휴대폰 안에 저장된 사진들을 빠르게 분석해 군 기밀 유출을 차단합니다.

- 또한, 기존 보안 앱과 달리 관리자 모니터링 웹페이지를 통해, 부대 내에 있는 휴대폰들의 Spyder가 켜져 있는지 실시간으로 보여줍니다. 카메라 기능이 켜져 있는 휴대폰을 바로 알려주고 통제가 가능하며, 저장된 사진 중 관리자가 설정한 위치정보 내에서 촬영된 사진들만 검사하여 모니터링이 가능합니다. 


## 기능 설명
 - APP
     - 카메라 차단 기능
     - 위치정보 설정 기능
     - 사진의 위치정보 분석 기능 (EXIF Analysis)
 - WEB
     - 앱이 설치된 단말기 감시 기능
     - 앱 강제 종료 감시 기능
     - 위치정보에 위반되는 사진 모니터링 기능

## 컴퓨터 구성 / 필수 조건 안내 (Prerequisites)
* ECMAScript 6 지원 브라우저 사용
* 권장: Google Chrome 버젼 77 이상
* 권장: Android 버전 -- 이상

## 기술 스택 (Technique Used) 
### WEB(Back-end)
 - Python
     - Framework: Django
 - AWS EC2
 
### WEB(Front-end)
 - HTML, CSS, JS

### APP
 - Java
     - IDE: Android Studio

## 설치 안내 (Installation Process)
```bash
$ git clone git주소
$ yarn or npm install
$ yarn start or npm run start
```
 
## 팀 정보 (Team Information)

|  팀원  |         소속          |     GitHub     |         Email         |
| :----: | :-------------------: | :------------: | :-------------------: |
| 조용인 |  육군 17사단   | yongincho |    choyongin21@gmail.com    |
| 김진 | 육군 수도군단 |  .   | . |

## 저작권 및 사용권 정보 (Copyleft / End User License)
 * [MIT](https://github.com/osam2020-WEB/Sample-ProjectName-TeamName/blob/master/license.md)

This project is licensed under the terms of the MIT license.
