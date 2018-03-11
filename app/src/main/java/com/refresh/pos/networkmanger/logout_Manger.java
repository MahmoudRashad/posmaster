package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.refresh.pos.techicalservices.URLS;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;

import org.json.JSONArray;

import static com.refresh.pos.networkmanger.cookes.okHttpClient;

/**
 * Created by mahmoudrashad on 3/10/2018.
 */

public class logout_Manger {

    Activity context;
    LoginSharedPreferences loginSharedPreferences;
    private logout_Manger.MyCustomObjectListener listener;

    public logout_Manger(Activity context) {
        this.context = context;
        loginSharedPreferences = new LoginSharedPreferences(context);
    }

    public void setCustomObjectListener(logout_Manger.MyCustomObjectListener listener) {
        this.listener = listener;
    }

    public void logout() {

        AndroidNetworking.get(URLS.logout)
                .addHeaders("Authorization", "Bearer " + loginSharedPreferences.getAccessToken())
                .setOkHttpClient(okHttpClient)
                .setTag("test")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("onResponse:logout ", response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("onResponse:logout ", anError.getResponse().toString());
                    }
                });


    }

    public interface MyCustomObjectListener {

        void onObjectReady(JSONArray title);

        void onFailed(String title);


    }


}
