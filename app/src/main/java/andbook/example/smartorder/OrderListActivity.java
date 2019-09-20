package andbook.example.smartorder;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//알람에서 넘어갈때 시리얼 넘버가 없어 생기는 문제점
public class OrderListActivity extends ListActivity {
    TextView textView;
    ArrayList<OrderListDTO> items;
    String serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        Intent i = getIntent();
        serialNumber = i.getExtras().getString("serialNumber");

        Button button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        //초기 리스트활성화
        sendRequest();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //리트스활성화를 못믿는 나이가 잇으신분들을 위한 새로고침
                sendRequest();
            }
        });
    }

    public void sendRequest() {
        String url = "http://"+ getStaticData.getIP()+"/an01/list.jsp";

        StringRequest sr = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                                JSONObject jsonObj = new JSONObject(response);

                            items = new ArrayList<OrderListDTO>();

                            JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject row = jArray.getJSONObject(i);
                                OrderListDTO dto = new OrderListDTO();
                                dto.setOrder_time(row.getString("order_time"));
                                dto.setOrder_info(row.getString("order_info"));
                                dto.setWorkplace_num(row.getInt("workplace_num"));
                                items.add(dto);
                            }
                            OrderAdapter adapter = new OrderAdapter(
                                    OrderListActivity.this, R.layout.order_row, items);
                            setListAdapter(adapter);
                        }catch(Exception e){
                            e.printStackTrace();
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
               Map<String,String> param = new HashMap<String,String>();
                param.put("serialNumber",serialNumber);
                return param;
            }
        };
        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        sr.setShouldCache(false);
        AppHelper.requestQueue.add(sr);
    }

    class OrderAdapter extends ArrayAdapter<OrderListDTO> {

        OrderAdapter(Context context, int resource, List<OrderListDTO> objects) {
            super(context, resource, objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.order_row, null);

            }
            OrderListDTO dto = items.get(position);
            if (dto != null) {
                TextView order_info = (TextView) v.findViewById(R.id.order_info);
                TextView order_time = (TextView) v.findViewById(R.id.order_time);
                TextView workplace_num = (TextView) v.findViewById(R.id.workplace_num);
                order_info.setText(dto.getOrder_info());
                order_time.setText(dto.getOrder_time());
                workplace_num.setText(String.valueOf(dto.getWorkplace_num()));
            }
            return v;
        }
    }
}
