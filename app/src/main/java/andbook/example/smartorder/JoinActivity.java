package andbook.example.smartorder;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;


public class JoinActivity extends AppCompatActivity {

    private final String pw_regex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}"; //비밀번호 정규식
    private final String ph_regex = "^\\d{2,3}-\\d{3,4}-\\d{4}$"; //전화번호 정규식

    private String id = "";
    private String market_name = "";
    private String market_addr = "";
    private String market_phone = "";
    private String encryption_pw = "";

    @Override
    protected void onCreate(Bundle savedInstanceStat) {
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_join);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        final EditText join_id = (EditText) findViewById(R.id.join_id);
        final EditText join_pw = (EditText) findViewById(R.id.join_pw);
        final EditText join_workplace_name = (EditText) findViewById(R.id.join_workplace_name);
        final EditText join_workplace_adress = (EditText) findViewById(R.id.join_workplace_adress);
        final EditText join_workplace_phoneNumber = (EditText) findViewById(R.id.join_workplace_phoneNumber);

        Button cancel_btn = (Button) findViewById(R.id.join_cancel);
        Button ok_btn = (Button) findViewById(R.id.join_ok);


        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override //회원 가입 완료
            public void onClick(View v) {
//                if (join_id.getText().toString() == null || "".equals(join_id.getText().toString())) {//사용할 아이디 공백일 시
//                    Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (join_pw.getText().toString() == null || "".equals(join_pw.getText().toString())) {//사용할 비밀번호 공백일 시
//                     Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
//                   return;
//                }
//                if (join_workplace_name.getText().toString() == null || "".equals(join_workplace_name.getText().toString())) {//사용할 상호명 공백일 시
//                     Toast.makeText(getApplicationContext(),"매장 상호명을 입력해주세요.",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (join_workplace_adress.getText().toString() == null || "".equals(join_workplace_adress.getText().toString())) //사용할 주소 공백일 시
//                {
//                     Toast.makeText(getApplicationContext(),"매장 주소를 입력해주세요",Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
//
//                if (join_workplace_phoneNumber.getText().toString() == null || "".equals(join_workplace_phoneNumber.getText().toString())) //사용할 전화번호 공백일 시
//                {
//                    Toast.makeText(getApplicationContext(),"휴대전화 또는 매장 전화번호를 입력해주세요",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                    if (ph_regex.matches(join_workplace_phoneNumber.getText().toString())) { //전화번호를 올바른 형식으로 입력했을 시
//                        if (pw_regex.matches(join_pw.getText().toString())) { //비밀번호를 올바른 형식으로 입력했을 시
                id = join_id.getText().toString();
                market_name = join_workplace_name.getText().toString();
                market_addr = join_workplace_adress.getText().toString();
                market_phone = join_workplace_phoneNumber.getText().toString();
                encryption_pw = BCrypt.hashpw(join_pw.getText().toString(), BCrypt.gensalt(10)); //사용할 아이디 암호화 완료

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //회원 가입 시 알람채널 생성
                    AlarmChannels.createChannel(getApplicationContext());
                }
                sendRequest();


//                        } else
//                             Toast.makeText(getApplicationContext(),"비밀번호를 특수문자+영문자+숫자 조합 8자 이상으로 입력해주세요.",Toast.LENGTH_SHORT).show();
                //비밀번호를 올바른 형식으로 입력하지 않았을 시
//                    } else
//                        Toast.makeText(getApplicationContext(),"전화번호를 올바르게 입력해주세요",Toast.LENGTH_SHORT).show(); //전화번호를 올바른 형식으로 입력하지 않았을 시

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //로그인 창으로 뒤로가기 진행
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    public void sendRequest() {
        Log.i("sendRequest", "진입");
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/join.jsp");

        StringRequest request = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String[] resPonse_split = response.split(" ");

                        switch (resPonse_split[0]) {
                            case "alreadyID":
                                Toast.makeText(getApplicationContext(), "이미 해당 아이디는 사용하고 있습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "systemError":
                                Toast.makeText(getApplicationContext(), "시스템 오류입니다. 다시 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Intent i = new Intent(getApplicationContext(), OrderListActivity.class);
                                i.putExtra("serialNumber", resPonse_split[1]);
                                startActivity(i);
                                finish();
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("id", id);
                params.put("market_name", market_name);
                params.put("market_addr", market_addr);
                params.put("market_phone", market_phone);
                params.put("encryption_pw", encryption_pw);
                params.put("token", getStaticData.getToken());

                return params;
            }
        };
        if (getStaticData.requestQueue == null) {
            getStaticData.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        request.setShouldCache(false);
        getStaticData.requestQueue.add(request);
    }
}
