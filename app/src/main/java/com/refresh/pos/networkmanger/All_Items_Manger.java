package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.refresh.pos.techicalservices.URLS;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;

import org.json.JSONArray;


/**
 * Created by mahmoudrashad on 2/12/2018.
 */

public class All_Items_Manger {
    Activity context;
    LoginSharedPreferences loginSharedPreferences;
    private MyCustomObjectListener listener;
    public All_Items_Manger(Activity context) {
        this.context = context;
        loginSharedPreferences = new LoginSharedPreferences(context);
    }

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }

    public void get_items_list( )

    {


        AndroidNetworking.get(URLS.get_All_Items)
                .addHeaders("Authorization","Bearer " +loginSharedPreferences.getAccessToken())
                .setTag("test")

                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (new ResultHandler().validateHandlerResult(context,response))
                            if (listener != null){
                                listener.onObjectReady(response);
                            }

                    }

                    @Override
                    public void onError(ANError anError) {
                        try {

                            if (listener != null){
                                listener.onFailed(anError.getErrorBody().toString());
                            }
                        }catch (Exception e) {
                            // handle error

                            Log.d("apidata", anError.getErrorBody().toString());
                        }
                    }
                });


    }

    public interface MyCustomObjectListener {

        void onObjectReady(JSONArray title);

        void onFailed(String title);


    }


}
