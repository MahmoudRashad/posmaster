package com.refresh.pos.networkmanger;

import android.app.Activity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.refresh.pos.domain.inventory.KEY_VALUE;
import com.refresh.pos.techicalservices.URLS;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mahmoudrashad on 3/4/2018.
 */

public class Sale_flags {

    Activity context;
    LoginSharedPreferences loginSharedPreferences;
    private MyCustomObjectListener listener;
    public interface MyCustomObjectListener {

        void onObjectReady(ArrayList<KEY_VALUE> list);
        void onFailed(String title);


    }

    public Sale_flags(Activity context) {
        this.context = context;
        loginSharedPreferences = new LoginSharedPreferences(context);
    }

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }

    public void get_spaner1_data ( )
    {

        AndroidNetworking.get(URLS.price_types)
                .addHeaders("Authorization","Bearer " +loginSharedPreferences.getAccessToken())
                .setTag("test")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<KEY_VALUE> list= new ArrayList<>();
                        for (int i =0;i<response.length();i++){
                            try {list.add(new KEY_VALUE(response.getJSONObject(i)));}
                            catch (JSONException e) {e.printStackTrace();}
                        }
                        if (listener != null){
                            listener.onObjectReady(list);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (listener != null){
                            listener.onFailed(anError.getMessage());
                        }
                    }
                });

    }
    public void get_spaner2_data ( )
    {

        AndroidNetworking.get(URLS.stores)
                .addHeaders("Authorization","Bearer " +loginSharedPreferences.getAccessToken())
                .setTag("test")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<KEY_VALUE> list= new ArrayList<>();
                        for (int i =0;i<response.length();i++){
                            try {list.add(new KEY_VALUE(response.getJSONObject(i)));}
                            catch (JSONException e) {e.printStackTrace();}
                        }
                        if (listener != null){
                            listener.onObjectReady(list);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (listener != null){
                            listener.onFailed(anError.getMessage());
                        }
                    }
                });

    }
    public void get_spaner3_data (String storeid)
    {

        ////////////
        JSONObject test0pj=new JSONObject();
        JSONArray filterarr=new JSONArray();
        try {
            test0pj.put("FilterStatement","WHERE");
            test0pj.put("ColumnName","StoreId");
            test0pj.put("DataType","int");
            test0pj.put("FilterCondition","0");
            test0pj.put("FilterValue",storeid);
            filterarr.put(0,test0pj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /////////////////



        AndroidNetworking.post(URLS.counters)
                .addHeaders("Authorization","Bearer " +loginSharedPreferences.getAccessToken())
                .addJSONArrayBody(filterarr)
                .setTag("test")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<KEY_VALUE> list= new ArrayList<>();
                        for (int i =0;i<response.length();i++){
                            try {list.add(new KEY_VALUE(response.getJSONObject(i)));}
                            catch (JSONException e) {e.printStackTrace();}
                        }
                        if (listener != null){
                            listener.onObjectReady(list);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (listener != null){
                            listener.onFailed(anError.getMessage());
                        }
                    }
                });

    }


}
