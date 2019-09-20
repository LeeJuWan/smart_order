package andbook.example.smartorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MyFirebasePushServer {

    private final String FCM_KEY = "AAAANhoH624:APA91bHX1AHnFuFhVlRORbR8XSaEmQe5nmVFXLkBW5WW83" +
            "lbjkBKyXAtLHcpCSDmNw0Oc3R5iI15EKyJm1xYI2zGAiEyTBQ5E1S90hnL0F9TGj1xMiGA6inrFlinA1DUzGBKCzi0nCCh"; //서버키
    private final String FCM_URL = "https://fcm.googleapis.com/fcm/send"; //FCM 서버
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8"); //서버 전달 방식


    //FCM 서버로 전송
    @SuppressLint("StaticFieldLeak")
    public void sendFCMRequest(String message, String userDeviceToken) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    Log.d("진입 SendFCMRequest1",message);
                    Log.d("진입 SendFCMRequest2",userDeviceToken);

                    OkHttpClient client = new OkHttpClient();
                    JSONObject notification = new JSONObject();
                    JSONObject json = new JSONObject();


                    notification.put("body",message);
                    notification.put("title","스마트주문");
                    json.put("to",userDeviceToken); //단일 기기로 보내기 위함
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
                    System.err.println("sendFCMRequest JSONException error");
                } catch (IOException e) {
                    System.err.println("sendFCMRequest IOException error");
                }
                return null;
            }
        }.execute();
    }
}
