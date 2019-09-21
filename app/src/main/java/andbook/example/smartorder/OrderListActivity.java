package andbook.example.smartorder;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListActivity extends ListActivity {

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

        Button update_btn = (Button) findViewById(R.id.update);
        TextView appView = (TextView) findViewById(R.id.app_name);

        //초기 리스트활성화
        sendRequest();

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //리트스활성화를 못믿는 나이가 잇으신분들을 위한 새로고침
                sendRequest();
            }
        });
    }

    private void sendRequest() {
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/list.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
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
                                dto.setTable_number(Integer.parseInt(row.getString("table_number")));

                                items.add(dto);
                            }
                            OrderAdapter adapter = new OrderAdapter(
                                    OrderListActivity.this, R.layout.order_row, items);
                            setListAdapter(adapter);
                        } catch (JSONException e) {
                            Log.i("OrderListActivty error","JSONException");
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
                param.put("serialNumber", serialNumber);
                return param;
            }
        };
        if (getStaticData.requestQueue == null) {
            getStaticData.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        stringRequest.setShouldCache(false);
        getStaticData.requestQueue.add(stringRequest);
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
                StringBuilder orderInfo = new StringBuilder();
                TextView order_info = (TextView) v.findViewById(R.id.order_info);
                Button delete_btn = (Button) v.findViewById(R.id.delete);

                orderInfo.append(dto.getOrder_info()).append("\n");
                orderInfo.append(dto.getOrder_time()).append("\n");
                orderInfo.append(dto.getWorkplace_num()).append("\n");
                orderInfo.append(dto.getTable_number());

                order_info.setText(orderInfo);

                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OrderListDTO dto = items.get(position);
                        deleteList(dto);
                    }
                });
            }
            return v;
        }
    }


    private void deleteList(final OrderListDTO dto) {
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/delete_list.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //삭제응답을 받고난 후, 리스트 최신화를 위한 데이터 다시 불러오기.
                        sendRequest();
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
                param.put("order_time", dto.getOrder_time());
                param.put("order_info", dto.getOrder_info());
                param.put("workplace_num", String.valueOf(dto.getWorkplace_num()));
                param.put("table_number", String.valueOf(dto.getTable_number()));

                return param;
            }
        };
        if (getStaticData.requestQueue == null) {
            getStaticData.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        stringRequest.setShouldCache(false);
        getStaticData.requestQueue.add(stringRequest);
    }
}
