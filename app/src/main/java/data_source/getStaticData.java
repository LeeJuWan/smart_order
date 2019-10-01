package data_source;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;


public class getStaticData {
    // 자주 사용하는 변수  메모리에 고정 시켜놓기
    private static String token = "";

    public static RequestQueue requestQueue;
    public static StringRequest link_stringRequest;
    //알람이 오면 리스트를 즉각 업데이트 할 수 있도록 전역변수 설정

    public static String getIP() {
        return "zwsdkd.cafe24.com";
    }
    public static void setToken(String new_Token){
        token = new_Token;
    }
    public static String getToken(){ return token; }
}
