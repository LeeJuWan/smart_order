package andbook.example.smartorder;

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

public class AlarmChannels {
    private static Notification.Builder builder; //푸시알람 빌더 생성
    private static NotificationManager notificationManager; //푸시 알람 매니저 생성
    private static Notification.BigTextStyle bigTextStyle; //푸시 알람 핀치줌을 위한 텍스트 스타일 생성
    private static Uri defaultSoundUri; //알림용
    private static Intent intent;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ //알람채널을 위한 ID 및 상세 메시지 생성
            Channel.GROUP_ID,
            Channel.GROUP_NAME,
            Channel.MESSAGE_ID,
            Channel.MESSAGE_NAME,
            Channel.MESSAGE_DESC})
    public @interface  Channel{
        //채널 그룹
        String GROUP_ID = "smart_order",
                GROUP_NAME = "자동 주문 알림 메시지 ";
        //메시지 채널
        String MESSAGE_ID ="message",
                MESSAGE_NAME = "알람 메시지",
                MESSAGE_DESC = "음식 주문시 알람이 갑니다.";
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static void createChannel(Context context) {

        //채널 그룹 생성
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannelGroup group = new NotificationChannelGroup(
                Channel.GROUP_ID, Channel.GROUP_ID);
        notificationManager.createNotificationChannelGroup(group);

        //채널 생성: 메시지
        NotificationChannel channelMessage = new NotificationChannel(
                Channel.MESSAGE_ID, Channel.MESSAGE_NAME,
                android.app.NotificationManager.IMPORTANCE_DEFAULT);
        channelMessage.setDescription(Channel.MESSAGE_DESC); //채널 설명
        channelMessage.setGroup(Channel.GROUP_ID); //속해있는 채널 그룹 ID
        channelMessage.enableLights(true); // LED 점멸 기능
        channelMessage.setLightColor(Color.RED); // LED 색상(적색)
        channelMessage.enableVibration(true); //진동 가능
        notificationManager.createNotificationChannel(channelMessage);
    }


    //오레오 이상 버전
    @TargetApi(Build.VERSION_CODES.O)
    public static  void sendNotification(Context context, @Channel String channel ,String message, String serialNumber){
        intent = new Intent(context,OrderListActivity.class);
        intent.putExtra("serialNumber",serialNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,
                PendingIntent.FLAG_ONE_SHOT); //FLAG_ONE_SHOT 일회용 알람

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder = new Notification.Builder(context,channel)
                .setTicker(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("스마트 주문")
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true);
        bigTextStyle= new Notification.BigTextStyle(builder);
        bigTextStyle.setBigContentTitle("스마트 주문");
        bigTextStyle.bigText(message);
        notificationManager.notify((int) (System.currentTimeMillis() / 1000),builder.build());
    }

    //오레오 미만 버전
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void sendNotification_notOreo(Context context , String message , String serialNumber){
        Intent intent = new Intent(context,OrderListActivity.class);
        intent.putExtra("serialNumber",serialNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,
                PendingIntent.FLAG_ONE_SHOT); //FLAG_ONE_SHOT 일회용 알람

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder = new Notification.Builder(context)
                .setTicker(message)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("스마트 주문")
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        bigTextStyle= new Notification.BigTextStyle(builder);
        bigTextStyle.setBigContentTitle("스마트 주문");
        bigTextStyle.bigText(message);
        notificationManager.notify((int) (System.currentTimeMillis() / 1000),builder.build());
    }
}