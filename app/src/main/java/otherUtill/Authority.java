package otherUtill;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import data_source.getStaticData;


// 중복 로그인 체크 로직
public class Authority {
    public static void isAuthority(Context context){
        Log.i("진입 isAuthority",".");
        StringBuffer stringBuffer = new StringBuffer("http://" + getStaticData.getIP() + "/an01/authority.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(stringBuffer),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("권한",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String,String>();
                Log.i("진입 Auth Token", getStaticData.getToken());
                map.put("token",getStaticData.getToken());


                return map;
            }
        };
        if (getStaticData.requestQueue == null) {
            getStaticData.requestQueue = Volley.newRequestQueue(context);
        }

        stringRequest.setShouldCache(false);
        getStaticData.requestQueue.add(stringRequest);
    }
}
