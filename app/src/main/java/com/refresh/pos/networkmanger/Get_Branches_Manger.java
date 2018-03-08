package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.refresh.pos.R;
import com.refresh.pos.domain.Branche;
import com.refresh.pos.domain.inventory.Product;
import com.refresh.pos.techicalservices.URLS;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by mahmoudrashad on 2/12/2018.
 */

public class Get_Branches_Manger {
    Activity context;
    LoginSharedPreferences loginSharedPreferences;
    ProgressDialog _ProgressDialog;
    private MyCustomObjectListener listener;
    //    public static int log=0;
    public Get_Branches_Manger(Activity context) {
        this.context = context;
        loginSharedPreferences= new LoginSharedPreferences(context);
    }

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }

    public interface MyCustomObjectListener {

        void onObjectReady(ArrayList<Branche> title);
        void onFailed(String title);


    }

    public void get_Branches( )

    {


        AndroidNetworking.get(URLS.Get_Branches)
                .addHeaders("Authorization","Bearer " +loginSharedPreferences.getAccessToken())
                .setTag("test")
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if ( new ResultHandler().validateHandlerResult(context,response)){
                            ArrayList<Branche> branches=new ArrayList<>();

                            for (int i=0;i<response.length();i++)
                            {

                                JSONObject row = null;
                                try {
                                    row = response.getJSONObject(i);
                                    Branche branche = new Branche(row);
                                    branches.add(branche);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            if (listener != null){
                                listener.onObjectReady(branches);
                            }
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


}