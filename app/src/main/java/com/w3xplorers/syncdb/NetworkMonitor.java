package com.w3xplorers.syncdb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 5/27/2017.
 */

public class NetworkMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if(checkNetworkConnection(context)){
            final DBHelper dbHelper = new DBHelper(context);
            final DBHelper dbHelperN = new DBHelper(context);
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            Cursor cursor = dbHelperN.readFromLocalDb(database);

            while (cursor.moveToNext()){

                int sync_status = cursor.getInt(cursor.getColumnIndex(DBContact.STATUS));
                if(sync_status==DBContact.sync_status_failed){
                    final String name = cursor.getString(cursor.getColumnIndex(DBContact.NAME));
                    StringRequest stringRequest= new StringRequest(Request.Method.POST, DBContact.SERVER_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        String Response = jsonObject.getString("response");
                                        if(Response.equals("OK")){
                                            dbHelperN.updateDatabase(name,DBContact.sync_status_ok,database);
                                            context.sendBroadcast(new Intent(DBContact.UI_UPDATE_BROADCAST));

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String ,String> params = new HashMap<>();
                            params.put("name",name);
                            return params;
                        }
                    };

                    MySingleton.getInstance(context).addToRequestQueue(stringRequest);
                }
            }
            //dbHelperNew.close();
            //dbHelper.close();
        }
    }

    public boolean checkNetworkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }
}
