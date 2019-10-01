package andbook.example.smartorder;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data_source.getStaticData;
import dto.OrderListDTO;
import otherUtill.BackPressCloseHandler;

public class OrderListActivity extends ListActivity {

    // 두 번 빠르게 누를 시 종료하기 위한 백버튼 핸들러
    private BackPressCloseHandler backPressCloseHandler;

    // 매장 관련 정보
    private ArrayList<OrderListDTO> items;
    private String serialNumber = "";
    private String token = "";

    private TextView appView;
    private Button update_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바 제거

        backPressCloseHandler = new BackPressCloseHandler(OrderListActivity.this);


        Intent intent = getIntent();
        serialNumber = intent.getExtras().getString("serialNumber");


        update_btn = (Button) findViewById(R.id.update);
        appView = (TextView)findViewById(R.id.app_name);

        // 리스트활성화
        sendRequest();

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 리스트 새로고침
                sendRequest();
            }
        });
    }

    // 뒤로가기 체크
    @Override
    public void onBackPressed(){
        backPressCloseHandler.onBackPressed();
    }


    // 관리자가 자신의 매장 주문 내역을 DB에서 확인하기 위한 메서드
    public void sendRequest() {
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/list.jsp");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(OrderListActivity.this,
                            new OnSuccessListener<InstanceIdResult>() {
                                // 현재 토큰을 가져온다.
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    // 현재 등록 토큰 확인, 등록된 토큰이 없는 경우 토큰이 업데이트 및 새 발급이 이뤄짐
                                    token = instanceIdResult.getToken();
                                    Log.d("진입 OrderListAct token", token);
                                }
                            });
                    if (token == null) {
                        // 등록된 토큰이 없기때문에 새 발급이 이뤄진걸로 판단
                        token = getStaticData.getToken();
                    }

                    Thread.sleep(100); // 토큰을 받아오는 시간을 고려하여 잠시 sleep

                    getStaticData.stringRequest = new StringRequest(
                            Request.Method.POST, String.valueOf(url),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {

                                        JSONObject jsonObj = new JSONObject(response);
                                        items = new ArrayList<OrderListDTO>();

                                        // 매장 주문 정보
                                        int market_ORDER_COUNT = 0; // 매장 주문 건수
                                        StringBuilder market_Informaion = new StringBuilder(); // 주문 건수 담는 변수
                                        JSONArray jArray = (JSONArray)jsonObj.get("sendData"); // 매장 주문 내역

                                        int jArray_Size = jArray.length();

                                        for (int i = 0; i < jArray_Size ; i++) {

                                            JSONObject row = jArray.getJSONObject(i);
                                            OrderListDTO dto = new OrderListDTO();
                                            market_ORDER_COUNT += 1;
                                            // 매장 주문 정보 담기
                                            dto.setOrder_time(row.getString("order_time"));
                                            dto.setOrder_info(row.getString("order_info"));
                                            dto.setWorkplace_num(row.getInt("workplace_num"));
                                            dto.setTable_number(Integer.parseInt(row.getString("table_number")));

                                            items.add(dto);
                                        }
                                        market_Informaion
                                                .append("실시간 주문량")
                                                .append(market_ORDER_COUNT)
                                                .append("건입니다.");
                                        appView.setText(market_Informaion);

                                        OrderAdapter adapter = new OrderAdapter(
                                                OrderListActivity.this, R.layout.order_row, items);
                                        setListAdapter(adapter);
                                    } catch (JSONException e) {
                                        Log.i("OrderListActivty error","JSONException error");
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

                            // OrderListActivity에서는 token값을 주기적으로 확인하여 주문 알람을 받을 단말기를
                            // DB로 보내 꾸준히 업데이트 시켜줌
                            // 매장의 식별키를 보내어 해당 관리자를 식별하여 token 업데이트
                            param.put("serialNumber",serialNumber);
                            param.put("token",token);

                            Log.d("진입 access token",token);
                            Log.d("진입 access serialNumber",serialNumber);
                            return param;
                        }
                    };
                    if (getStaticData.requestQueue == null) {
                        getStaticData.requestQueue = Volley.newRequestQueue(getApplicationContext());
                    }
                    // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
                    // 항상 새로운 데이터를 위해 false
                    getStaticData.stringRequest .setShouldCache(false);
                    getStaticData.requestQueue.add(getStaticData.stringRequest );
                } catch (InterruptedException e) {
                    Log.i("OrderListActivty","InterruptedException error");
                }
            }
        }).start();
    }

    class OrderAdapter extends ArrayAdapter<OrderListDTO>{

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
                orderInfo.append(dto.getTable_number()).append(" 번 테이블");

                order_info.setText(orderInfo);

                // 주문 정보를 삭제 하기 위한 버튼
                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 삭제 전 확인 절차 진행
                        new AlertDialog.Builder(OrderListActivity.this)
                                .setCancelable(false)
                                .setTitle("주문 완료")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage("POS기기에 주문 내용을 입력하셨나요? 하셨다면 주문 완료를 눌러주세요.")
                                .setPositiveButton("주문완료", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        OrderListDTO dto = items.get(position);
                                        deleteRequest(dto);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("돌아가기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                });
            }
            return v;
        }
    }

    // 매장 관리자가 해당 주문 내역을 삭제할 시 DB에 해당 데이터를 삭제하기 위한 메서드
    private void deleteRequest(final OrderListDTO dto) {
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/delete_list.jsp");

         StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //삭제응답을 받고난 후 리스트 최신화를 위한 데이터 다시 불러오기
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

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        stringRequest.setShouldCache(false);
        getStaticData.requestQueue.add(stringRequest);
    }
}