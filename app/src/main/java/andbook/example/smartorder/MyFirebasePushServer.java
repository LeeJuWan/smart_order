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

    private String userDeviceToken = ""; //사용자 단말기 토큰

    //FCM 서버로 전송
    @SuppressLint("StaticFieldLeak")
    public void sendFCMRequest(final String message, Context context) {

        sendTokenRequest(context);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();

                    dataJson.put("body",message);
                    dataJson.put("title","스마트주문");
                    json.put("notification",dataJson);
                    json.put("to",userDeviceToken); //단일 기기로 보내기 위함

                    RequestBody body = RequestBody.create(JSON,json.toString());
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Authorization", "key=" +FCM_KEY)
                            .url(FCM_URL)
                            .post(body)
                            .build();
                    okhttp3.Response response = client.newCall(request).execute();
                } catch (JSONException e) {
                    System.err.println("sendFCMRequest JSONException error");
                } catch (IOException e) {
                    System.err.println("sendFCMRequest IOException error");
                }
                return null;
            }
        }.execute();
    }
    private void sendTokenRequest(final Context context) {
        String url = "http://" + GetIP.getIp() + "/an01/login.jspdasdasdasdas";

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        userDeviceToken=response; //고유키(매장)에 해당하는 사용자의 토큰을 저장
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("serialNumber",GetIP.getSerialNumber());

                Log.i("serialNumber",GetIP.getSerialNumber());
                return param;
            }
        };
        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(context);
        }
        sr.setShouldCache(false);
        AppHelper.requestQueue.add(sr);
    }
}
