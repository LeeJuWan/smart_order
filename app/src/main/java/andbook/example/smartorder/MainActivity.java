package andbook.example.smartorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceStat){
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        Button admin_flow = (Button)findViewById(R.id.admin_Flow);
        Button custom_flow = (Button)findViewById(R.id.customer_Flow);

        // 관리자용 버튼
        admin_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        // 손님용 버튼
        custom_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //손님용 주문 리스트로 이동
                startActivity(new Intent(getApplicationContext(),AddressListActivity.class));
            }
        });
    }
}
