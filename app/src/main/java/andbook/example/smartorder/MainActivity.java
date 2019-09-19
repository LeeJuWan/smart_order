package andbook.example.smartorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
//
//힙이 아닌 고정메모리에 올라가기에 속도가빠르며 (퍼포먼스) 메모리를 효율적으로 사용 할 수 있다.
//그러나 고정메모리 이기에 사용되지 않는 static 클래스는 자원낭비 및 메모리 릭을 유발할 수 있다.
//절대적인 값을 고정할 시 final static 명시
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceStat){
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        Button admin_flow = (Button)findViewById(R.id.admin_Flow);
        Button custom_flow = (Button)findViewById(R.id.customer_Flow);

        admin_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //로그인으로 이동
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        custom_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //손님용 주문 리스트로 이동
                startActivity(new Intent(getApplicationContext(),AddressList.class));
                finish();
            }
        });
    }
}
