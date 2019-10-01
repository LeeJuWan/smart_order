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

public class LoginActivity extends AppCompatActivity{

    private String check_id = ""; // 로그인 아이디
    private String access_pw = ""; // 로그인 비밀번호

    @Override
    protected void onCreate(Bundle savedInstanceStat) {
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 제거

        EditText login_id = (EditText) findViewById(R.id.login_id);
        EditText login_pw = (EditText) findViewById(R.id.login_pw);

        Button login_btn = (Button) findViewById(R.id.admin_login);
        Button join_btn = (Button) findViewById(R.id.admin_join);

        // 로그인 진행 버튼
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override // 로그인 하기위한 입력 값 검증 진행
            public void onClick(View v) {
                if ("".equals(login_id.getText().toString()))
                    Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();
                else if ( "".equals(login_pw.getText().toString()))
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                else {
                    check_id = login_id.getText().toString();
                    access_pw = login_pw.getText().toString();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //로그인 시 알람채널 생성
                        AlarmChannels.createChannel(getApplicationContext());
                    loginRequest();
                }
            }
        });

        // 회원 가입 진행 버튼
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), JoinActivity.class));
            }
        });
    }

    // 매장 관리자가 입력한 id,pw와 DB에 저장된 id,pw가 동일 한지 확인하기 위한 메서드
    private void loginRequest() {
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/login.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.trim().equals("NotFoundID"))
                            Toast.makeText(getApplicationContext(),"아이디를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                        else{
                            String[] resPonse_split = response.split(" ");
                            // 입력한 아이디가 있을 시 암호화된 비밀번호를 DB에서 가져옴
                            // 암호화된 비밀번호와 입력한 비밀번호가 일치할 시 id,pw 검증 완료 -> 로그인 진행
                            boolean vaild = BCrypt.checkpw(access_pw, resPonse_split[0]); // 암호화된 비밀번호 추출 및 일치 여부 체크
                            if (vaild) {
                                Intent i = new Intent(getApplicationContext(),OrderListActivity.class);
                                i.putExtra("serialNumber", resPonse_split[1]); // 매장 식별 키 전달
                                startActivity(i);
                                finish();
                            }
                            else   // 비밀번호 불 일치
                                Toast.makeText(getApplicationContext(),"비밀번호를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
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
                Map<String, String> param = new HashMap<String, String>();
                // 로그인 정보 push 진행
                param.put("id",check_id);

                return param;
            }
        };

        if (getStaticData.requestQueue == null) {
            getStaticData.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        getStaticData.requestQueue.add(stringRequest);
    }
}
