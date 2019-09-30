# 차세데 오더 서비스(Version 1.0)  

## 개요  
 ### 차세데 Off-line 주문 서비스 App  
 1. 소개  
	 - 기존에 존재하는 주문 App과 Kiosk의 단점들을 보완하여 사용자 입장에서 접근성과 편리성을 더 높인 Off-Line App  
	 1. 고객은 자신이 위치한 매장주소를 찾고 음성인식을 통해 주문  
	 2. 관리자는 주문리스트에 내용 확인 후, POS기기에 입력한 후 서비스 제공  
   
   1. 주문방식의 종류  
	- Kiosk주문방식  
		  1. Kiosk를 이용하여 물품 선택  
		  2. 결제  
	- App을 활용한 Off-Line주문  
		1. 각 가게의 App을 다운로드  	
		2.  매장방문을 선택  
		3.  결제  
	- **차세데 오더 서비스 주문**  
		1. 통합형 App 설치  
		2. 각 가게의 (아날로그) 메뉴판을 확인 후 (디지털)음성인식 시스템을 활용한 물품 주문  
    
2. 장점과 단점  
	- 장점  
		1. 각 매장별 개별적인 App설치가 아닌 통합형 App설치  
		2. 매장점주 입장에서 Kiosk의 구매할 필요 없음    
		3. 고객은 Kiosk앞에서 대기할 필요 없음  
		4. 번거로운 절차 생략  
	- 단점  
		1. 각 매장 메뉴판 필요  
			* 보통의 매장들은 메뉴판을 기본적으로 소유하므로 문제없음  
3. **차세데 오더 서비스 주문 흐름도**  
	1. 고객은 매장의 위치 검색  
	2. 음성인식 시스템으로 주문  
	3. 매장은 주문 내용 확인 및 서비스 제공  

4. 표  
 
| 제품 | 접근성 | 비용 | 편리성 |  
|:--:|:--:|:--:|:--:|  
|Kiosk | o | x | o |  
|App | o | o | x |  
|차세데 오더 서비스 | o | o | o |  

## 환경  
### 개발환경  
1. Android  
	 - Volley ([Vooley Document](https://developer.android.com/training/volley))  
	 - Google STT API ([STT Document](https://cloud.google.com/speech-to-text/docs/?hl=ko))  
	 - Google Cloud Messaging([API Document](https://firebase.google.com/docs/cloud-messaging))  
2.  Tomcat Server ([Tomcat download](http://tomcat.apache.org/))  
3. Eclipse ([Eclipse download](https://www.eclipse.org/downloads/))  
4.  Maria DB ([Maria Database](https://mariadb.org/download/))  

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
