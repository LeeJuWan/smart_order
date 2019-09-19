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
import java.util.ArrayList;


//Order Activity는 손님이 해당 음식점을 고른 뒤, 해당 음식정에서 주문을 하기 위한 Activity 임
//모든 매장은 OrderActivity를 공용으로 사용 , 어차피 객체라서 실제로는 다 따로 사용하는것과 같음
public class OrderActivity extends AppCompatActivity {

    private EditText tableNumber;
    private EditText order_confirm;
    private Button order_btn;

    private final int PERMISSIONREQUEST_RESULT = 100; // 콜백 호출시 requestcode로 넘어가는 구분자
    //음성 인식을 위해 선언
    private Intent intent;
    private SpeechRecognizer recognizer;

    private String serialNumber = "";
    private MyFirebasePushServer pushServer;

    @Override
    protected void onCreate(Bundle savedInstanceStat) {
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_order);
        //값 받아주기
        Intent getIntent = getIntent();
        AddressDTO addrDTO = (AddressDTO) getIntent.getSerializableExtra("DTO");
        Log.i("오더액티비티",String.valueOf(addrDTO.getWorkplace_num()));


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        //음성인식을 위한 intent & recognizer initializing
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //음성 인식 intent 생성
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName()); // 데이터 설정
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); //음성 인식 언어 설정

        tableNumber = (EditText) findViewById(R.id.table_num);
        order_btn = (Button) findViewById(R.id.Order);

        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override //음식주문 진행
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //마시멜로우 이상 버전일 시 권한 확인 진행
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {  //권한 허용 시 음식 주문 그대로 진행
                        if (tableNumber.getText().toString() == null || "".equals(tableNumber.getText().toString())) {  //"" <- 스페이스바 방지
                            new AlertDialog.Builder(OrderActivity.this) //테이블 번호 미입력 시 다이얼로그 출력
                                    .setTitle("테이블 번호 미입력")
                                    //.setIcon("//") 아직 디자인 없음
                                    .setMessage("고객님의 테이블 번호를 입력해주세요")
                                    .setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                        } else {
                            recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext()); //음성인식 객체
                            recognizer.setRecognitionListener(listener); //음성인식 리스너 등록
                            try {
                                recognizer.startListening(intent); //음성 인식 시작
                            } catch (SecurityException e) {
                                System.err.println("SecurityException error");
                            }
                            new AlertDialog.Builder(OrderActivity.this) //정보 제공 출력
                                    .setTitle("주문 진행")
                                    //.setIcon("//") 아직 디자인 없음
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
                        CheckPermission(); //권한 미허용 시 권한을 얻게함
                    }
                } else {  //마시멜로우 버전 미만일 시 권한 확인 없이 진행
                    if (tableNumber.getText().toString() == null || "".equals(tableNumber.getText().toString())) {  //"" <- 스페이스바 방지
                        new AlertDialog.Builder(OrderActivity.this) //테이블 번호 미입력 시 다이얼로그 출력
                                .setTitle("테이블 번호 미입력")
                                //.setIcon("//") 아직 디자인 없음
                                .setMessage("고객님의 테이블 번호를 입력해주세요")
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                    } else {
                        recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext()); //음성인식 객체
                        recognizer.setRecognitionListener(listener); //음성인식 리스너 등록
                        try {
                            recognizer.startListening(intent); //음성 인식 시작
                        } catch (SecurityException e) {
                            System.err.println("SecurityException error");
                        }
                        new AlertDialog.Builder(OrderActivity.this) //정보 제공 출력
                                .setTitle("주문 진행")
                                //.setIcon("//") 아직 디자인 없음
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

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {// 사용자가 말하기 시작할 준비가되면 호출됩니다.
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
        public void onError(int error) {  // 네트워크 또는 인식 오류가 발생했을 때 호출됩니다.
            String message;
            //음성 인식 주문 오류 추적 코드
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "음성 인식 오류가 발생하였습니다 ! : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) { // 인식 결과가 준비되면 호출됩니다.
            // 아래 코드는 음성인식된 결과를 ArrayList로 모아오기
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String voice_value = "";
            voice_value = matches != null ? matches.get(0) : "음성인식 오류";

            order_confirm = new EditText(getApplicationContext()); //음성 주문 시 ,수정가능한 형태로 변환 진행
            order_confirm.setText(voice_value); //음성 주문 입력 값 EditText에 삽입
            //여기서 음성결과 출력
            new AlertDialog.Builder(OrderActivity.this) //테이블 번호 미입력 시 다이얼로그 출력
                    .setCancelable(false) //화면 밖 터치 다이얼로그 안사라짐
                    .setTitle("주문 결과 확인")
                    .setView(order_confirm)
                    //.setIcon("//") 아직 디자인 없음
                    .setPositiveButton("주문 완료",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //order_confirm.getText().toString(); 여기에 주문 값 들어있음
                                    //여기서 db랑 연결해서 주문 정보로 보내줘야됨
                                    pushServer = new MyFirebasePushServer();
                                    Toast.makeText(getApplicationContext(), tableNumber.getText().toString()+"번 테이블"
                                            +order_confirm.getText().toString()+" 주문 완료되었습니다 !", Toast.LENGTH_SHORT).show();
                                    GetIP.setSerialNumber(serialNumber); //현재 매장 고유키 저장
                                    pushServer.sendFCMRequest(tableNumber.getText().toString()+order_confirm.getText().toString(),getApplicationContext());
                                    dialog.dismiss();
                                    stopSpeechRecognizer(); //음성 주문 완료로 인한 , 음성 객체 반환

                                }
                            })
                    .setNegativeButton("주문 취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "다시 주문 하시겠어요?", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    stopSpeechRecognizer(); //음성 주문 취소로 인한 , 음성 객체 반환
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
    //음성 인식 종료 시 객체 반환 함수
    private void stopSpeechRecognizer(){
        /*음성 인식 객체를 종료 하지않으면 메모리에 남아있게 되며 그와동시에 다른 음성 인식이 죽기때문에
         * 항상 StopListing을 해주어 null로 변환 시킨다.
         */
        if (recognizer != null) {
            recognizer.destroy();
            recognizer.cancel();
            recognizer = null;
        }
    }

    //퍼미션 권한 진행 함수
    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            //사용자의 최초 퍼미션 허용을 확인         -true: 사용자 퍼미션 거부 , -false: 사용자 동의 미 필요
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(getApplicationContext(), "음성 주문을 위해서는 다음의 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSIONREQUEST_RESULT);
            }
        }
    }

    //permission call back 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        if (requestCode == PERMISSIONREQUEST_RESULT) {
            if (grantResult.length > 0) {
                for (int aGrantResult : grantResult) {
                    if (aGrantResult == PackageManager.PERMISSION_DENIED) {
                        //권한이 하나라도 거부 될 시
                        new AlertDialog.Builder(this)
                                .setTitle("사용 권한의 문제발생")
                                //.setIcon(R.drawable.big) 아직 디자인 없음
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
