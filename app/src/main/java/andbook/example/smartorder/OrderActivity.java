package andbook.example.smartorder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data_source.getStaticData;
import dto.AddressDTO;
import firebase_service.MyFirebasePushServer;


public class OrderActivity extends AppCompatActivity {

    // 주문정보 DTO
    private AddressDTO addrDTO;
    // 주문정보
    private StringBuffer order_Information_Data;
    private EditText tableNumber;
    private EditText order_Confirm;
    private Button order_Btn;

    // 콜백 호출시 requestcode로 넘어가는 구분자
    private final int PERMISSIONREQUEST_RESULT = 100;

    // 음성 인식을 위해 선언
    private Intent intent;
    private SpeechRecognizer recognizer;

    // FCM 알람을 위한 Server 객체
    private MyFirebasePushServer pushServer;

    @Override
    protected void onCreate(Bundle savedInstanceStat) {
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_order);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        tableNumber = (EditText) findViewById(R.id.table_num);
        order_Btn = (Button) findViewById(R.id.order);

        // AddressListActivity의 전달 값 받아주기
        Intent getIntent = getIntent();
        addrDTO = (AddressDTO) getIntent.getSerializableExtra("DTO");

        // 음성인식을 위한 intent & recognizer initializing
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // 음성 인식 intent 생성
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName()); // 데이터 설정
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); // 음성 인식 언어 설정

        // 음식주문 진행 버튼
        order_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 마시멜로우 이상 버전일 시 권한 확인 진행
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                    {
                        // 권한 허용 시 음식 주문 그대로 진행 & 테이블 번호 입력 검증 진행
                        if (tableNumber.getText().toString() == null || "".equals(tableNumber.getText().toString())) {
                            new AlertDialog.Builder(OrderActivity.this)
                                    .setCancelable(false)
                                    .setTitle("테이블 번호 미입력")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage("고객님의 테이블 번호를 입력해주세요")
                                    .setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                        } else {
                            recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext()); // 음성인식 객체
                            recognizer.setRecognitionListener(listener); // 음성인식 리스너 등록
                            try {
                                recognizer.startListening(intent); // 음성 인식 시작
                            } catch (SecurityException e) {
                                Log.i("OrderActivty","SecurityException error");
                            }
                            new AlertDialog.Builder(OrderActivity.this)
                                    .setCancelable(false)
                                    .setTitle("주문 진행")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage("지금부터 음성으로 주문해주세요\n" +
                                            "주문을 다하셨다면 OK를 눌러주세요.\n" +
                                            "오랫동안 말하지않으면 주문이 안됩니다 !")
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                        }
                    } else {
                        CheckPermission(); // 권한 미허용 시 권한을 얻게함
                    }
                } else {
                    // 마시멜로우 버전 미만일 시 권한 확인 없이 진행  & 테이블 번호 입력 검증 진행
                    if (tableNumber.getText().toString() == null || "".equals(tableNumber.getText().toString())) {
                        new AlertDialog.Builder(OrderActivity.this)
                                .setCancelable(false)
                                .setTitle("테이블 번호 미입력")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage("고객님의 테이블 번호를 입력해주세요")
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                    } else {
                        recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext()); // 음성인식 객체
                        recognizer.setRecognitionListener(listener); // 음성인식 리스너 등록
                        try {
                            recognizer.startListening(intent); // 음성 인식 시작
                        } catch (SecurityException e) {
                            Log.i("OrderActivty","SecurityException error");
                        }
                        new AlertDialog.Builder(OrderActivity.this)
                                .setCancelable(false)
                                .setTitle("주문 진행")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage("지금부터 음성으로 주문해주세요\n" +
                                        "주문을 다하셨다면 OK를 눌러주세요.\n" +
                                        "오랫동안 말하지않으면 주문이 안됩니다 !")
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                    }
                }
            }
        });
    }

    // 위의 recognizer.setRecognitionListener(listener) 통해 리스너 등록 시
    // 아래의 콜백 메서드 호출
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) { // 사용자가 말하기 시작할 준비가되면 호출됩니다.
            Toast.makeText(getApplicationContext(), "주문을 시작해주세요 !", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() { // 사용자가 말하기 시작했을 때 호출됩니다.
            Toast.makeText(getApplicationContext(), "주문을 시작해주세요 !", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRmsChanged(float rmsdB) {  // 입력받는 소리의 크기를 알려줍니다.
        }

        @Override
        public void onBufferReceived(byte[] buffer) {  // 사용자가 말을 시작하고 인식이 된 단어를 buffer에 담습니다.
        }

        @Override
        public void onEndOfSpeech() { // 사용자가 말하기를 중지하면 호출됩니다.
            Toast.makeText(getApplicationContext(), "주문을 인식했습니다. 주문 내역을 확인해주세요!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int error) {  // 네트워크 또는 인식 오류가 발생했을 때 호출 진행
            StringBuilder message = new StringBuilder();
            // 음성 인식 주문 오류 추적 코드
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message.append("오디오 오류");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message.append("클라이언트 오류");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message.append("허용된 권한이 없습니다.");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message.append("네트워크 오류");
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message.append("네트워크 시간초과");
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message.append("찾을 수 없습니다.");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message.append("음성인식 과부하");
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message.append("서버 오류");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message.append("음성 주문 시간초과");
                    break;
                default:
                    message.append("알 수 없는 오류");
                    break;
            }
            message.append(" <- 음성 인식 오류가 발생하였습니다.");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        // 인식 결과가 준비되면 호출
        @Override
        public void onResults(Bundle results) {
            // 아래 코드는 음성인식된 결과를 ArrayList로 모아오기
            String voice_value = "";

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            voice_value = matches != null ? matches.get(0) : "음성인식 오류";

            order_Confirm = new EditText(getApplicationContext()); // 음성 주문 시 수정가능한 형태로 변환 진행
            order_Confirm.setText(voice_value); // 음성 주문 입력 값 EditText에 삽입
            order_Information_Data = new StringBuffer();

            // 여기서 음성결과 출력
            new AlertDialog.Builder(OrderActivity.this)
                    .setCancelable(false)
                    .setTitle("주문 결과 확인")
                    .setView(order_Confirm)
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("주문 완료",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 음성 인식 정보를 append 진행
                                    order_Information_Data.append(order_Confirm.getText().toString());
                                    order_Information_Data.append(tableNumber.getText().toString()).append("번 테이블");

                                    // 여기서 DB 연결 후, 주문 정보 DB에 주문 내역 저장 및 해당 단말기 소유자에게 알람 진행
                                    sendData(); //음식정보 저장 및 FCM 서버 전송 및 단말기 token GET

                                    dialog.dismiss();
                                    stopSpeechRecognizer(); // 음성 주문 완료로 인한 , 음성 객체 반환
                                }
                            })
                    .setNegativeButton("주문 취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "다시 주문 하시겠어요?", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    stopSpeechRecognizer(); // 음성 주문 취소로 인한 , 음성 객체 반환
                                }
                            }).show();

        }

        @Override
        public void onPartialResults(Bundle partialResults) { // 부분 인식 결과를 사용할 수 있을 때 호출됩니다.
        }

        @Override
        public void onEvent(int eventType, Bundle params) { // 향후 이벤트를 추가하기 위해 예약됩니다.
        }
    };

    // 음성 인식 종료 시 객체 반환 함수
    private void stopSpeechRecognizer() {
        /* 음성 인식 객체를 종료 하지않으면 메모리에 남아있게 되며 그와동시에 다른 음성 인식이 죽기때문에
         * 항상 StopListing을 해주어 null로 변환 시킨다. */
        if (recognizer != null) {
            recognizer.destroy();
            recognizer.cancel();
            recognizer = null;
        }
    }

    // 사용자의 음성 주문 정보를 해당 매장의 주문 정보DB에 저장 하기위한 메서드
    private void sendData() {
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/insert_Orderlist.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("성공")) {
                            Toast.makeText(getApplicationContext(), tableNumber.getText().toString() + "번 테이블 "
                                    + order_Confirm.getText().toString() + " 주문 완료되었습니다. 감사합니다.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), tableNumber.getText().toString() + "번 테이블 "
                                    + order_Confirm.getText().toString() + "주문 오류 다시 시도 해주세요.", Toast.LENGTH_LONG).show();
                        }
                        // 해당 관리자의 단말기로 주문 알람을 전송하기 위해 Token 값 Get 진행
                        sendTokenRequest();
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
                Map<String, String> map = new HashMap<String, String>();
                map.put("orderInfo", order_Confirm.getText().toString());
                map.put("workplace_num", String.valueOf(addrDTO.getWorkplace_num()));
                map.put("tableNumber", tableNumber.getText().toString());

                return map;
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

    // 주문을 받은 해당 매장의 관리자의 스마트 폰 Token을 얻기 위한 메서드
    private void sendTokenRequest() {
        StringBuffer url = new StringBuffer("http://"+getStaticData.getIP()+"/an01/getSerial_setToken.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 매장 관리자 토큰 정보 response 받음
                        pushServer = new MyFirebasePushServer();
                        // FCM 서버에 해당 매장의 관리자 Token , 주문 정보, 매장 고유키 담아 보내기
                        pushServer.sendFCMRequest(order_Information_Data, response , String.valueOf(addrDTO.getWorkplace_num()) );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("진입 error","not found");
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("serialNumber",String.valueOf(addrDTO.getWorkplace_num()));

                return map;
            }
        };
        if (getStaticData.requestQueue == null) {
            getStaticData.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        stringRequest.setShouldCache(false); // 이전 결과가 있더라도 새로 요청해서 응답을 보여줌
        getStaticData.requestQueue.add(stringRequest);
    }

    // 퍼미션 권한 진행 메서드
    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            // 사용자의 최초 퍼미션 허용을 확인
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(getApplicationContext(), "음성 주문을 위해서는 다음의 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSIONREQUEST_RESULT);
            }
        }
    }

    // 퍼미션 콜백 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        if (requestCode == PERMISSIONREQUEST_RESULT) {
            if (grantResult.length > 0) {
                for (int aGrantResult : grantResult) {
                    if (aGrantResult == PackageManager.PERMISSION_DENIED) {
                        // 권한이 하나라도 거부 될 시
                        new AlertDialog.Builder(this)
                                .setTitle("사용 권한의 문제발생")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage("음성 주문을 위해서는 다음의 권한이 필요합니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
