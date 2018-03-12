package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.refresh.pos.domain.sale.Register;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.techicalservices.URLS;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;

import org.json.JSONArray;

import static com.refresh.pos.networkmanger.cookes.okHttpClient;

/**
 * Created by mahmoudrashad on 3/12/2018.
 */

public class TransationitemsManger {
    Activity activity;
    LoginSharedPreferences loginSharedPreferences;
    TransationitemsManger.mycustomer_click_lisner listener;
    private Register regis;

    public TransationitemsManger(Activity activity) {
        this.activity = activity;
        try {
            regis = Register.getInstance();
        } catch (NoDaoSetException e) {
            e.printStackTrace();
        }
        loginSharedPreferences = new LoginSharedPreferences(activity);
    }

    public void setListener(TransationitemsManger.mycustomer_click_lisner listener) {
        this.listener = listener;
    }

    public void Transationitem(String TranId) {

        AndroidNetworking.post(URLS.transationitem)
                .addBodyParameter("TranId", TranId)
                .setOkHttpClient(okHttpClient)
                .addHeaders("Authorization", "Bearer " + loginSharedPreferences.getAccessToken())
                .setTag("test")
                .setContentType("application/json")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // handel get_Transactions response
                        if (listener != null) {
                            if (new ResultHandler().validateHandlerResult(activity, response)) {
                                listener.onObjectReady(response);
                            }
                        }

                        // do anything with response
                        Log.d("apidata", response.toString());

                    }

                    @Override
                    public void onError(ANError anError) {
                        if (listener != null)
                            listener.onFailed(anError.getMessage());
                        Log.d("apidata", anError.getResponse().toString());
                    }
                });


    }

    public interface mycustomer_click_lisner {
        void onObjectReady(JSONArray response);

        void onFailed(String s);
    }


}
