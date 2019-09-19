package andbook.example.smartorder;



import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        //단말기의 새로운 토큰을 확인했을 때 호출 되는 콜백 메서드
        super.onNewToken(token);
         //기존의 FirebaseInstanceIdService에서 수행하던 토큰 생성, 갱신 등의 역할을 함
        GetIP.setToken(token);
    }

    //서버로 http 방식으로 푸쉬를 보내면 서버에서 메시징을 보낸다 이때 onMessage가 동작되어 사용자에게 노티가 간다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        //새로운 메시지를 받았을 때 호출되는 메소드
       String message = remoteMessage.getNotification().getBody();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            AlarmChannels.sendNotification(getApplicationContext(),AlarmChannels.Channel.MESSAGE_ID,message);
        else
            AlarmChannels.sendNotification_notOreo(getApplicationContext(),message);
    }
}
