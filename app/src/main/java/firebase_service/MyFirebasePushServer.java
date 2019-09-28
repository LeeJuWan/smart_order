package firebase_service;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MyFirebasePushServer {

    // 서버키 추후 서버에서 저장하여 사용할 예정
    private final String FCM_KEY = "AAAANhoH624:APA91bHX1AHnFuFhVlRORbR8XSaEmQe5nmVFXLkBW5WW83" +
            "lbjkBKyXAtLHcpCSDmNw0Oc3R5iI15EKyJm1xYI2zGAiEyTBQ5E1S90hnL0F9TGj1xMiGA6inrFlinA1DUzGBKCzi0nCCh";
    private final String FCM_URL = "https://fcm.googleapis.com/fcm/send"; // FCM 서버 주소
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8"); // 서버 전달 방식


    // FCM 서버로 전송
    @SuppressLint("StaticFieldLeak")
    public void sendFCMRequest(StringBuffer message, String userDeviceToken, String serialNumber) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    Log.d("진입 FCM Request1",String.valueOf(message));
                    Log.d("진입 FCM Request2",userDeviceToken);
                    Log.d("진입 FCM Request3",serialNumber);

                    OkHttpClient client = new OkHttpClient();
                    JSONObject notification = new JSONObject();
                    JSONObject json = new JSONObject();


                    notification.put("body",String.valueOf(message));
                    notification.put("title",serialNumber.trim());
                    json.put("to",userDeviceToken.trim()); // 단일 기기로 보내기 위함
                    json.put("data",notification);


                    RequestBody body = RequestBody.create(JSON,json.toString());
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Authorization", "key="+FCM_KEY)
                            .url(FCM_URL)
                            .post(body)
                            .build();
                    client.newCall(request).execute();
                    Log.d("진입 push Suceess",userDeviceToken);
                } catch (JSONException e) {
                    Log.d("sendFCMRequest","JSONException error");
                } catch (IOException e) {
                    Log.d("sendFCMRequest","IOException error");
                }
                return null;
            }
        }.execute();
    }
}
