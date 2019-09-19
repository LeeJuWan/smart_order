package andbook.example.smartorder;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressList extends ListActivity {
    EditText editText;
    ArrayList<AddressDTO> items;
    ArrayList<AddressDTO> itemsClone;
    AddressAdapter adapter;

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ListView listView =(ListView) l;
        AddressDTO addressDTO=(AddressDTO)l.getItemAtPosition(position);
        /*v.findViewById()*/

        String st =addressDTO.getAddress();
        String st02 = addressDTO.getPlace_name();
        Log.i("출력값", st+" " + st02);
        AddressDTO sendAddrDTO = null;
        for(int i =0; i< itemsClone.size();i++){
            if(st.equals(itemsClone.get(i).getAddress()) && st02.equals(itemsClone.get(i).getPlace_name())){
                sendAddrDTO=itemsClone.get(i);
            }
        }

        Intent intent = new Intent(getApplicationContext(),OrderActivity.class);
        intent.putExtra("DTO",sendAddrDTO);
        startActivity(intent);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_list);



        Button button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);

        sendRequest();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editText.getText().toString();
                search(text);
            }
        });


    }

    public void search(String charText) {
        items.clear();


        if (charText.length() == 0) {
            items.addAll(itemsClone);
        } else {
            for (int i = 0; i < itemsClone.size(); i++) {
                String st = itemsClone.get(i).getAddress() + " " + itemsClone.get(i).getPlace_name();
                Log.i("묶여진", st);
                if (st.toLowerCase().contains(charText.toLowerCase()))
                {
                    items.add(itemsClone.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }


    public void sendRequest() {
        String url = "http://"+ GetIP.getIp()+"/an01/address.jsp";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            items = new ArrayList<AddressDTO>();
                            JSONObject jsonObj = response;
                            JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject row = jArray.getJSONObject(i);
                                AddressDTO dto = new AddressDTO();
                                dto.setPlace_name(row.getString("place_name"));
                                dto.setAddress(row.getString("address"));
                                dto.setWorkplace_num(row.getInt("workplace_num"));
                                dto.setPhone_number(row.getString("phone_number"));

                                items.add(dto);


                            }
                            itemsClone = new ArrayList<AddressDTO>();
                            itemsClone.addAll(items);

                            adapter = new AddressAdapter(
                                    AddressList.this, items);
                            setListAdapter(adapter);
                            //데이터 다모은 후.

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //데이터 저장하고 출력끝 이제 비동기적인 활성화


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });


        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }


        jsonObjectRequest.setShouldCache(false);
        AppHelper.requestQueue.add(jsonObjectRequest);
    }

    class AddressAdapter extends ArrayAdapter<AddressDTO> {

        public AddressAdapter(Context context, List<AddressDTO> objects) {
            super(context, R.layout.address_row, objects);
        }


        @Override
        public View getView(int position,  View convertView,  ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.address_row, null);

            }
            AddressDTO dto = items.get(position);
            if (dto != null) {
                TextView place_name = (TextView) v.findViewById(R.id.place_name);
                TextView address = (TextView) v.findViewById(R.id.address);
                TextView phone_number = (TextView) v.findViewById(R.id.phone_number);

                place_name.setText(dto.getPlace_name());
                address.setText(dto.getAddress());
                phone_number.setText(dto.getPhone_number());

            }

            return v;

        }
    }
}
