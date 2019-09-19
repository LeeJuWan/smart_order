package andbook.example.smartorder;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity{

    private String check_id = ""; //로그인 아이디
    private String access_pw = ""; //로그인 비밀번호
    private String token = ""; //로그인 시 현재토큰

    @Override
    protected void onCreate(Bundle savedInstanceStat) {
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        final EditText login_id = (EditText) findViewById(R.id.login_id);
        final EditText login_pw = (EditText) findViewById(R.id.login_pw);

        Button login_btn = (Button) findViewById(R.id.admin_login);
        Button join_btn = (Button) findViewById(R.id.admin_join);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override //로그인 진행
            public void onClick(View v) {
                if (login_id.getText().toString() == null || "".equals(login_id.getText().toString()))
                    Toast.makeText(getApplicationContext(),"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show();
                else if (login_pw.getText().toString() == null || "".equals(login_pw.getText().toString()))
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
                else {
                    check_id = login_id.getText().toString(); //아이디 검색을 통하여 해당 아이디에 매칭 되는 비밀번호를 가져오기 위한 변수
                    access_pw = login_pw.getText().toString(); //사용자가 입력한 비밀번호

                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this,
                            new OnSuccessListener<InstanceIdResult>() {
                        // 현재 토큰을 가져온다.
                        //1. 단말기 변경으로 인해 회원가입은 되어 있으나 단말기의 토큰이 다를 경우
                        //2. 같은 아이디로 다른 단말기로 접속했을 경우
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            token = instanceIdResult.getToken(); //현재 등록 토큰 확인, 등록된 토큰이 없는 경우 토큰이 업데이트 및 새 발급이 이뤄짐
                        }
                    });
                    if(token == null){
                        //등록된 토큰이 없기때문에 새 발급이 이뤄진걸로 판단
                        token=GetIP.getToken();
                    }
                    sendRequest();
                }
            }
        });

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override //회원 가입 진행
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), JoinActivity.class));
                finish();
            }
        });
    }

    public void sendRequest() {
        String url = "http://" + GetIP.getIp() + "/an01/login.jsp";

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("응답","응답");
                        String receiveSt = response;

                        Log.i("전달받음",receiveSt);
                        String[] spSt = receiveSt.split(" ");

                        boolean vaild = BCrypt.checkpw(access_pw, spSt[0]);
                        //JSP에서 비밀번호 긁어오고 사용자가 입력한 값과 같으면 true 틀리면 , false
                        if (vaild) {
                            //해당 하는 아이디에 매칭되는 DB 비밀번호
                            Intent i = new Intent(getApplicationContext(),OrderListActivity.class);
                            i.putExtra("serialNumber", spSt[1]);
                            startActivity(i);
                            finish();
                        } else {
                            switch (spSt[0]){
                                //만약 비밀번호가 없다면, 아이디를 잘못 입력한것임
                                case "아이디불일치":
                                    Toast.makeText(getApplicationContext(),"아이디를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                                    break;
                                default :
                                    //비밀번호 불일치시 이동.
                                    Toast.makeText(getApplicationContext(),"비밀번호를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                                    break;

                            }

                        }
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
                param.put("id",check_id);
                param.put("token,",token ); //추가 코드
                Log.i("아이디",check_id);
                Log.i("토큰",token);
                return param;
            }
        };

        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        sr.setShouldCache(false);
        AppHelper.requestQueue.add(sr);
    }
}
