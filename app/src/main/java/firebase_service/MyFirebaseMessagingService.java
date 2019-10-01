package firebase_service;

import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import otherUtill.AlarmChannels;
import data_source.getStaticData;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // 주문 정보와 매장 식별 키
    private String message = "";
    private String serialNumber = "";

    @Override
    public void onNewToken(String token) {
        // 단말기의 새로운 토큰을 확인했을 때 호출 되는 콜백 메서드
        super.onNewToken(token);
        // 기존의 FirebaseInstanceIdService에서 수행하던 토큰 생성, 갱신 등의 역할을 함
        getStaticData.setToken(token);
    }

    // 서버로 http 방식으로 푸쉬를 보내면 서버에서 메시징을 보냄 이때 onMessage가 동작되어
    // 해당 매장 관리자 스마트폰 으로 주문 알람이 간다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        // 새로운 메시지를 받았을 때 호출되는 메소드
        Log.d("진입 onMessageReceived","inline");

        if(remoteMessage.getData().size()> 0){
            Log.d("진입 getData",remoteMessage.getData().get("body"));
            message = remoteMessage.getData().get("body");
            serialNumber = remoteMessage.getData().get("title");
            // 주문 정보와 매장 식별키를 담아 놓음
        }

        if(remoteMessage.getNotification() != null){
            Log.d("진입 getNotification",remoteMessage.getNotification().getBody());
            message = remoteMessage.getNotification().getBody();
            serialNumber = remoteMessage.getNotification().getTitle();
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ // 버전 체크 진행
            Log.d("진입 Message notification",message);
            AlarmChannels.sendNotification(getApplicationContext(),AlarmChannels.Channel.MESSAGE_ID,message,serialNumber);
        }
        else // 오레오 미만 버전 시 채널 미 필요
            AlarmChannels.sendNotification_notOreo(getApplicationContext(),message,serialNumber);
    }
}
