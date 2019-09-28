package andbook.example.smartorder;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import data_source.getStaticData;
import dto.AddressDTO;


public class AddressListActivity extends ListActivity {

    private ArrayList<AddressDTO> items;
    private ArrayList<AddressDTO> itemsClone;
    private AddressAdapter adapter;

    @Override
    protected void onListItemClick(ListView getList, View v, int position, long id) {
        super.onListItemClick(getList, v, position, id);

        // 리스트뷰 객체 가져오기
        ListView listView = (ListView) getList;
        // 객체의 index 데이터 (아이템 위치 데이터)
        AddressDTO addressDTO = (AddressDTO) getList.getItemAtPosition(position);

        String market_Address = addressDTO.getAddress();
        String market_Place = addressDTO.getPlace_name();

        AddressDTO sendAddrDTO = null;
        int itemsClone_Size = itemsClone.size();

        for (int i = 0; i < itemsClone_Size; i++) {
            if (market_Address.equals(itemsClone.get(i).getAddress()) && market_Place.equals(itemsClone.get(i).getPlace_name()))
                sendAddrDTO = itemsClone.get(i);
        }

        Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
        intent.putExtra("DTO", sendAddrDTO); // DTO 형태를 OrderActivity로 데이터 전달
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //상태바 제거

        final EditText seach_Line = (EditText) findViewById(R.id.seach_line);

        // 로그인 or 회원가입 시 자동으로 현재 매장정보 제공
        sendRequest();

        seach_Line.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            // 검색 진행 시
            @Override
            public void afterTextChanged(Editable editable) {
                String text_Value = seach_Line.getText().toString();
                search(text_Value);
            }
        });
    }

    private void search(String text_Value) {
        // 기존 데이터 삭제
        items.clear();

        if (text_Value.length() == 0)
            items.addAll(itemsClone);
        else {
            int itemsClone_Size = itemsClone.size();
            for (int i = 0; i < itemsClone_Size; i++) {

                String market_Address = itemsClone.get(i).getAddress() + " " + itemsClone.get(i).getPlace_name();
                if (market_Address.toLowerCase().contains(text_Value.toLowerCase()))
                    items.add(itemsClone.get(i));
            }
        }
        adapter.notifyDataSetChanged(); // 사용자가 매장 검색 시 리스트뷰 내용 변경
    }


    // 사용자에게 제공할 매장 정보 데이터를 DB에서 가져오기 위한 메서드
    private void sendRequest() {
        StringBuffer url = new StringBuffer("http://" + getStaticData.getIP() + "/an01/address.jsp");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                String.valueOf(url), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            items = new ArrayList<AddressDTO>();
                            JSONArray jArray = (JSONArray) response.get("sendData");

                            int jArray_Size = jArray.length();

                            for (int i = 0; i < jArray_Size; i++) {

                                JSONObject row = jArray.getJSONObject(i);
                                AddressDTO dto = new AddressDTO();
                                // 매장 정보 입력 진행
                                dto.setPlace_name(row.getString("place_name"));
                                dto.setAddress(row.getString("address"));
                                dto.setWorkplace_num(row.getInt("workplace_num"));
                                dto.setPhone_number(row.getString("phone_number"));

                                items.add(dto);
                            }
                            itemsClone = new ArrayList<AddressDTO>();
                            itemsClone.addAll(items);

                            adapter = new AddressAdapter(AddressListActivity.this, items);
                            setListAdapter(adapter);
                            // 데이터 all add 진행

                        } catch (JSONException e) {
                            Log.i("AddressListActivity","JSONException error");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        if (getStaticData.requestQueue == null)
            getStaticData.requestQueue = Volley.newRequestQueue(getApplicationContext());

        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
        // 항상 새로운 데이터를 위해 false
        jsonObjectRequest.setShouldCache(false);
        getStaticData.requestQueue.add(jsonObjectRequest);
    }

    class AddressAdapter extends ArrayAdapter<AddressDTO> {

        AddressAdapter(Context context, List<AddressDTO> objects) {
            super(context, R.layout.address_row, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater li =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.address_row, null);

            }
            AddressDTO dto = items.get(position);
            if (dto != null) {
                TextView place_name = (TextView)view.findViewById(R.id.place_name);
                TextView address = (TextView)view.findViewById(R.id.address);
                TextView phone_number = (TextView)view.findViewById(R.id.phone_number);

                // 주소 밑줄 긋기 작업
                SpannableString underline = new SpannableString(dto.getAddress());
                underline.setSpan(new UnderlineSpan(),0,underline.length(),0);

                place_name.setText(dto.getPlace_name());
                phone_number.setText(dto.getPhone_number());
                address.setText(underline); // 주소 밑줄 긋기
            }
            return view;
        }
    }
}
