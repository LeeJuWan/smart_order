package andbook.example.smartorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class AdminCheckListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceStat){
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_adminchecklist);
        
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거
    }
}
