package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.refresh.pos.R;
import com.refresh.pos.techicalservices.Globalclass;
import com.refresh.pos.techicalservices.URLS;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;
import com.refresh.pos.ui.MainActivity;
import com.refresh.pos.ui.SplashScreenActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by mahmoudrashad on 2/11/2018.
 */

public class LogInManager {

    Activity context;
    LoginSharedPreferences loginSharedPreferences;
    ProgressDialog _ProgressDialog;
    private MyCustomObjectListener listener;
    //    public static int log=0;
    public LogInManager(Activity context) {
        this.context = context;
        loginSharedPreferences= new LoginSharedPreferences(context);
    }

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }

    public interface MyCustomObjectListener {

        void onObjectReady(String title);
        void onFailed(String title);


    }

    public void logIn(final String Email, final String Password, String OfficeId)

    {


        AndroidNetworking.post(URLS.login)
                .addBodyParameter("Email", Email)
                .addBodyParameter("Password", Password)
                .addBodyParameter("OfficeId", OfficeId)
                .addBodyParameter("Challenge", "en-US")
                .addBodyParameter("Culture", "")
                .setTag("test")
                .setContentType("application/x-www-form-urlencoded")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null){
                            response=response.substring(1, response.length()-1);


                            if ( new ResultHandler().validateHandlerResult(context,response)){

                                    loginSharedPreferences = new LoginSharedPreferences(context);
                                    loginSharedPreferences.setAccessToken(response);
                                    loginSharedPreferences.setEmail(Email);
                                    loginSharedPreferences.setPassword(Password);

                                    Log.v("TOKEN",loginSharedPreferences.getAccessToken());


                                listener.onObjectReady(response.toString());

                            }

                        }

                        // do anything with response
                        Log.d("apidata", response.toString());

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