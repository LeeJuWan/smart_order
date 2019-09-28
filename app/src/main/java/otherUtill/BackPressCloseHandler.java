package otherUtill;

import android.app.Activity;
import android.widget.Toast;



// 백 버튼 핸들러
public class BackPressCloseHandler {
    private long backKeyClickTime = 0;
    private Activity activity;

    public BackPressCloseHandler(Activity activity){
        this.activity = activity;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyClickTime + 3000){ // 1회 누를 시 Toast
            backKeyClickTime = System.currentTimeMillis();
            Toast.makeText(activity,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(System.currentTimeMillis() <= backKeyClickTime + 3000){ // 연속 2회 누를 시 activty shutdown
            activity.finishAffinity(); // 모든 액티비티 삭제
        }
    }
}
