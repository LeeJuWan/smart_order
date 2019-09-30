# 비대면 음성주문 서비스(Version 1.0)  

## 개요  
 ### 비대면 음성 Off-line 주문 서비스 App  
 1. 소개  
  - 기존에 존재하는 주문 App과 Kiosk의 단점들을 보완하여 사용자 입장에서 접근성과 편리성을 더 높인 Off-Line App  
  - 고객은 자신이 위치한 매장주소를 찾고 음성인식을 통해 주문  
  - 관리자는 주문리스트에 내용 확인 후, POS기기에 입력한 후 서비스 제공  
   
 2. 주문방식의 종류  
  - Kiosk주문방식  
      - Kiosk를 이용하여 물품 선택  
      - 결제  
  - App을 활용한 Off-Line주문  
      - 각 가게의 App을 다운로드  	
      -  매장방문을 선택  
      -  결제  
  - **비대면 음성주문 서비스**  
      - 통합형 App 설치  
      - 각 가게의 (아날로그) 메뉴판을 확인 후 (디지털)음성인식 시스템을 활용한 물품 주문  
    
3. 장점과 단점  
 - 장점  
     - 각 매장별 개별적인 App설치가 아닌 통합형 App설치  
     - 매장 점주는 Kiosk를 구매할 필요 없음    
     - 고객은 Kiosk앞에서 대기할 필요 없음  
     - 기존의 아날로그(메뉴판)와 디지털(음성주문)의 결합으로 인한 편리성  
     - 주문 응대를 위한 신체적 피로감 감소  
     - 음성을 통한 고객 직접 주문이기에 주문 실수 방지  
     - 메뉴 리뉴얼 업데이트 불 필요 (매장 메뉴판 업데이트 진행 시 문제 없음)
  - 단점  
     - 각 매장 메뉴판 필요 -> 보통의 매장들은 메뉴판을 기본적으로 소유하므로 문제없음  
		
4. **비대면 음성주문 서비스 주문 흐름도**  
 - 고객은 매장의 위치 검색  
 - 음성인식 시스템으로 주문  
 - 매장은 주문 내용 확인 및 서비스 제공  

5. 기존의 서비스와의 차별성  
 
| 제품 | 접근성 | 비용 | 편리성 |  
|:--:|:--:|:--:|:--:|  
|Kiosk | o | x | o |  
|App | o | o | x |  
|비대면 음성주문 서비스 | o | o | o |  


## 환경  
### 개발환경  
- Android  
	 - Volley ([Vooley Document](https://developer.android.com/training/volley))  
	 - Google STT API ([STT Document](https://cloud.google.com/speech-to-text/docs/?hl=ko))  
	 - Google Cloud Messaging([API Document](https://firebase.google.com/docs/cloud-messaging))  
-  Tomcat Server ([Tomcat download](http://tomcat.apache.org/))  
- Eclipse ([Eclipse download](https://www.eclipse.org/downloads/))  
-  Maria DB ([Maria Database](https://mariadb.org/download/))  

### 실행환경  
 - Android Smart Phone  
	
## Activity의 기능  
 - MainActivity  
	  - 매장 관리자모드와 고객 주문모드 중 선택  
	
 - AddressListActivity  
	  - MainActivity -> Client Button Click  
	  - 자신이 위치한 매장의 주소를 찾을 수 있는 Activity  
	  	  
 - OrderActivity  
	 - AddressListActivity ->Each List Click  
	 - 음성을 통해 주문할 수 있는 Acitivity  
	  
 - LoginActivity  
	- MainAcitivity -> Admin Button Click  
	- ID와 PASSWORD를 통해 관리자 로그인  
	 - JOIN버튼을 통해 매장 회원가입 Activity로 이동  
	 
 - JoinActivity  
	 - LoginActivity -> Join Button Click  
	 - 각 매장이 회원가입 할 수 있는 Activity  
	 
 - OrderListActivity  
	 - LoginActivity or JoinActivity -> Login or Join Button Click  
	 - 고객으로부터 접수된 주문정보가 실시간 리스트로 나타나는 Activity  

- 작성일 : 2019-09-29  
- 작성자 : 안동규
