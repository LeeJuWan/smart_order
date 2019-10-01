package andbook.example.smartorder;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import data_source.getStaticData;
import otherUtill.AlarmChannels;


public class JoinActivity extends AppCompatActivity {

    private final String pw_regex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,}$"; // 비밀번호 정규식
    private final String ph_regex = "^\\d{2,3}\\d{3,4}\\d{4}$"; // 전화번호 정규식

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
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 제거

        EditText join_id = (EditText) findViewById(R.id.join_id);
        EditText join_pw = (EditText) findViewById(R.id.join_pw);
        EditText join_workplace_name = (EditText) findViewById(R.id.join_workplace_name);
        EditText join_workplace_adress = (EditText) findViewById(R.id.join_workplace_adress);
        EditText join_workplace_phoneNumber = (EditText) findViewById(R.id.join_workplace_phoneNumber);

        Button cancel_btn = (Button) findViewById(R.id.join_cancel);
        Button ok_btn = (Button) findViewById(R.id.join_ok);

        // 회원 가입 완료 버튼
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 가입 입력 값 검증 작업 진행
                if("".equals(join_id.getText().toString())) // 사용할 아이디 공백일 시
                    Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();
                else if("".equals(join_pw.getText().toString())) // 사용할 비밀번호 공백일 시
                     Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                else if("".equals(join_workplace_name.getText().toString())) // 사용할 상호명 공백일 시
                     Toast.makeText(getApplicationContext(),"매장 상호명을 입력해주세요.",Toast.LENGTH_SHORT).show();
                else if("".equals(join_workplace_adress.getText().toString())) // 사용할 주소 공백일 시
                     Toast.makeText(getApplicationContext(),"매장 주소를 입력해주세요",Toast.LENGTH_SHORT).show();
                else if("".equals(join_workplace_phoneNumber.getText().toString())) //사용할 전화번호 공백일 시
                    Toast.makeText(getApplicationContext(),"휴대전화 또는 매장 전화번호를 입력해주세요",Toast.LENGTH_LONG).show();
                else{
                    if (join_workplace_phoneNumber.getText().toString().matches(ph_regex)) { // 전화번호를 올바른 형식으로 입력했을 시
                        if (join_pw.getText().toString().matches(pw_regex)) { // 비밀번호를 올바른 형식으로 입력했을 시
                            id = join_id.getText().toString();
                            market_name = join_workplace_name.getText().toString();
                            market_addr = join_workplace_adress.getText().toString();
                            market_phone = join_workplace_phoneNumber.getText().toString();
                            encryption_pw = BCrypt.hashpw(join_pw.getText().toString(), BCrypt.gensalt(10)); //사용할 아이디 암호화

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 회원 가입 시 알람채널 생성
                                AlarmChannels.createChannel(getApplicationContext());
                            }
                            // 회원 가입 저장 진행
                            joinRequest();
                        }
                        else
                            // 비밀번호를 올바른 형식으로 입력하지 않았을 시
                            Toast.makeText(getApplicationContext(),"비밀번호를 특수문자+영문자+숫자 조합 8자 이상으로 입력해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else
                        // 전화번호를 올바른 형식으로 입력하지 않았을 시
                        Toast.makeText(getApplicationContext(),"전화번호를 올바르게 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 회원 가입 취소 버튼
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //로그인 창으로 뒤로가기 진행
                finish();
            }
        });
    }

    // 매장 관리자의 회원 정보를 DB에 저장 하기위한 메서드
    private void joinRequest() {
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/join.jsp");

        StringRequest stringRequest= new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response.trim()) {
                            case "alreadyID":
                                Toast.makeText(getApplicationContext(), "이미 해당 아이디는 사용하고 있습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case "systemError":
                                Toast.makeText(getApplicationContext(), "시스템 오류입니다. 다시 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                String[] resPonse_split = response.split(" "); // 매장 식별 키 추출
                                Toast.makeText(getApplicationContext(), "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
                                intent.putExtra("serialNumber", resPonse_split[1]); // 매장 식별 키 전달
                                startActivity(intent);
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
                // 회원가입 정보 push 진행
                params.put("id", id);
                params.put("place_name", market_name);
                params.put("address", market_addr);
                params.put("phone_number", market_phone);
                params.put("pw", encryption_pw);
                params.put("token", getStaticData.getToken());

                return params;
            }
        };

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        if (getStaticData.requestQueue == null) {
            getStaticData.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        stringRequest.setShouldCache(false);
        getStaticData.requestQueue.add(stringRequest);
    }
}
