package andbook.example.smartorder;




import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String message = "";
    private String serialNumber = "";

    @Override
    public void onNewToken(String token) {
        //단말기의 새로운 토큰을 확인했을 때 호출 되는 콜백 메서드
        super.onNewToken(token);
         //기존의 FirebaseInstanceIdService에서 수행하던 토큰 생성, 갱신 등의 역할을 함
        getStaticData.setToken(token);
    }

    //서버로 http 방식으로 푸쉬를 보내면 서버에서 메시징을 보낸다 이때 onMessage가 동작되어 사용자에게 노티가 간다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        Log.d("진입 onMessageReceived","inline"); //추적코드
        //새로운 메시지를 받았을 때 호출되는 메소드

        if(remoteMessage.getData().size()> 0){
            message = remoteMessage.getData().get("body");
            serialNumber = remoteMessage.getData().get("title");
            Log.d("진입 onMessage:message->",message);
        }

        if(remoteMessage.getNotification() != null){
            //FCM PUT 진행 시 notification으로 보내어 왔을 때
            Log.d("진입 getNotification",remoteMessage.getNotification().getBody());
            message = remoteMessage.getNotification().getBody();
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d("진입 Message:notification",message);
            AlarmChannels.sendNotification(getApplicationContext(),AlarmChannels.Channel.MESSAGE_ID,message,serialNumber);
        }
        else
            AlarmChannels.sendNotification_notOreo(getApplicationContext(),message,serialNumber);
    }
}
