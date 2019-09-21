package andbook.example.smartorder;

import android.util.Log;

import com.android.volley.RequestQueue;


public class getStaticData {
    public static RequestQueue requestQueue;

    private static String token = "";

    public static String getIP() { return "zwsdkd.cafe24.com"; }

    public static void setToken(String new_Token){
        token = new_Token;
    }
    public static String getToken(){
        Log.d("token getstatic data",token);
        return token;
    }
}
