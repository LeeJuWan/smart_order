package otherUtill;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import andbook.example.smartorder.OrderListActivity;
import andbook.example.smartorder.R;


// 알람 채널 생성 및 푸시알람 구현
public class AlarmChannels {
    private static Notification.Builder builder; // 푸시알람 빌더 생성
    private static NotificationManager notificationManager; // 푸시 알람 매니저 생성
    private static Notification.BigTextStyle bigTextStyle; // 푸시 알람 핀치줌을 위한 텍스트 스타일 생성
    private static Uri defaultSoundUri; // 알림용
    private static Intent intent;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ // 알람채널을 위한 ID 및 상세 메시지 생성
            Channel.GROUP_ID,
            Channel.GROUP_NAME,
            Channel.MESSAGE_ID,
            Channel.MESSAGE_NAME,
            Channel.MESSAGE_DESC})
    public @interface  Channel{
        // 채널 그룹
        String GROUP_ID = "smart_order",
                GROUP_NAME = "자동 주문 알림 메시지 ";
        // 메시지 채널
        String MESSAGE_ID ="message",
                MESSAGE_NAME = "알람 메시지",
                MESSAGE_DESC = "음식 주문시 알람이 갑니다.";
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static void createChannel(Context context) {

        // 채널 그룹 생성
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannelGroup group = new NotificationChannelGroup(
                Channel.GROUP_ID, Channel.GROUP_ID);
        notificationManager.createNotificationChannelGroup(group);

        // 채널 생성: 메시지
        NotificationChannel channelMessage = new NotificationChannel(
                Channel.MESSAGE_ID, Channel.MESSAGE_NAME,
                NotificationManager.IMPORTANCE_HIGH); // 알람 헤드업 0
        channelMessage.setDescription(Channel.MESSAGE_DESC); // 채널 설명
        channelMessage.setGroup(Channel.GROUP_ID); // 속해있는 채널 그룹 ID
        channelMessage.enableLights(true); // LED 점멸 기능
        channelMessage.setLightColor(Color.RED); // LED 색상(적색)
        channelMessage.enableVibration(true); // 진동 가능
        notificationManager.createNotificationChannel(channelMessage);
    }


    // 오레오 이상 버전
    @TargetApi(Build.VERSION_CODES.O)
    public static  void sendNotification(Context context, @Channel String channel ,String message, String serialNumber){
        intent = new Intent(context,OrderListActivity.class);
        intent.putExtra("serialNumber",serialNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // 기존의 같은 View를 보고있다가 알람을 누르면 Stack에서 기존 Activity 재사용 진행


        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,
                PendingIntent.FLAG_ONE_SHOT); // FLAG_ONE_SHOT 일회용 알람

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder = new Notification.Builder(context,channel)
                .setTicker(message) // 알람 발생 시 잠깐 text 나타냄
                .setSmallIcon(R.mipmap.ic_launcher) // 알람 이모티콘
                .setWhen(System.currentTimeMillis()) // 알람 발생 시간
                .setContentTitle("주문이 들어왔어요! 확인해보세요!") // 알람 제목
                .setContentText(message) // 알람 내용
                .setSound(defaultSoundUri) // 알람 기본 사운드
                .setContentIntent(pendingIntent) // 알람 누를 시 인텐트 진행
                .setPriority(Notification.PRIORITY_HIGH) // 알람 헤드업 1
                .setFullScreenIntent(pendingIntent,true) // 알람 헤드업 2
                .setDefaults(Notification.DEFAULT_ALL) // 알람 기본 진동 및 소리
                .setAutoCancel(true); //알람 누를 시 자동으로 삭제
        bigTextStyle= new Notification.BigTextStyle(builder);
        bigTextStyle.setBigContentTitle("주문이 들어왔어요! 확인해보세요!");
        bigTextStyle.bigText(message); // 알람 핀치줌 (미리 보기)
        notificationManager.notify((int) (System.currentTimeMillis() / 1000),builder.build());
    }

    // 오레오 미만 버전
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void sendNotification_notOreo(Context context , String message , String serialNumber){
        Intent intent = new Intent(context,OrderListActivity.class);
        intent.putExtra("serialNumber",serialNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // 기존의 같은 View를 보고있다가 알람을 누르면 Stack에서 기존 Activity 재사용 진행

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,
                PendingIntent.FLAG_ONE_SHOT); // FLAG_ONE_SHOT 일회용 알람

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder = new Notification.Builder(context)
                .setTicker(message) // 알람 발생 시 잠깐 text 나타냄
                .setSmallIcon(R.mipmap.ic_launcher) // 알람 이모티콘
                .setWhen(System.currentTimeMillis()) // 알람 발생 시간
                .setContentTitle("주문이 들어왔어요! 확인해보세요!") // 알람 제목
                .setContentText(message) // 알람 내용
                .setSound(defaultSoundUri) // 알람 기본 사운드
                .setContentIntent(pendingIntent) // 알람 누를 시 인텐트 진행
                .setPriority(Notification.PRIORITY_HIGH) // 알람 헤드업 1
                .setFullScreenIntent(pendingIntent,true) // 알람 헤드업 2
                .setDefaults(Notification.DEFAULT_ALL) // 알람 기본 진동 및 소리
                .setAutoCancel(true); //알람 누를 시 자동으로 삭제
        bigTextStyle= new Notification.BigTextStyle(builder);
        bigTextStyle.setBigContentTitle("주문이 들어왔어요! 확인해보세요!");
        bigTextStyle.bigText(message); // 알람 핀치줌 (미리 보기)
        notificationManager.notify((int) (System.currentTimeMillis() / 1000),builder.build());
    }
}