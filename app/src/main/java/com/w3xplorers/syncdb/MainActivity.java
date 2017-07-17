package com.w3xplorers.syncdb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText Name;
    Button Submit;
    RecyclerView.LayoutManager layoutManger;

    RecyclerAdapter adapter;
    ArrayList<Contacts> arrayList = new ArrayList<>();

    BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Name = (EditText) findViewById(R.id.editTxtname);
        Submit = (Button) findViewById(R.id.submit);

        layoutManger = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManger);
        recyclerView.setHasFixedSize(true);

        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        readFromLocalStorage();
        broadcastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };


    }

    private void readFromLocalStorage(){

        arrayList.clear();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.readFromLocalDb(database);

        while (cursor.moveToNext()){
            String name= cursor.getString(cursor.getColumnIndex(DBContact.NAME));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DBContact.STATUS));
            arrayList.add(new Contacts(name,sync_status));
        }

        adapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }

    private void saveToAppServer(final String name){



            if(checkNetworkConnection()){
                StringRequest stringRequest = new StringRequest(Request.Method.POST, DBContact.SERVER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String Response = jsonObject.getString("response");
                                    if(Response.equals("OK")){
                                        saveToLocalStorage(name,DBContact.sync_status_ok);
                                    }else{
                                        Log.d("Response",Response);
                                        saveToLocalStorage(name,DBContact.sync_status_failed);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("Response",volleyError.toString());
                        saveToLocalStorage(name,DBContact.sync_status_failed);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> param = new HashMap<>();
                        param.put("name",name);
                        return param;
                    }
                };

                MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
            }else{
                saveToLocalStorage(name,DBContact.sync_status_failed);
            }


    }

    private void saveToLocalStorage(String name,int sync){

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbHelper.saveToLocalDatabase(name,sync,database);

        readFromLocalStorage();
        dbHelper.close();
    }

    //check internet connection is available or not

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

    public void submitToDb(View view) {
        String name = Name.getText().toString();
        Log.d("name",name);
        saveToAppServer(name);
        Name.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(broadcastReceiver,new IntentFilter(DBContact.UI_UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
