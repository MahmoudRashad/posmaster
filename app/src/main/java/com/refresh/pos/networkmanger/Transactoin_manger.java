package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.refresh.pos.domain.sale.Register;
import com.refresh.pos.domain.sale.Sale;
import com.refresh.pos.techicalservices.Globalclass;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.techicalservices.URLS;
import com.refresh.pos.techicalservices.sale.SaleDao;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mahmoudrashad on 2/20/2018.
 */

public class Transactoin_manger {
    Activity activity;
    private Register regis;
    LoginSharedPreferences loginSharedPreferences;
    mycustomer_click_lisner listener;
    public Transactoin_manger(Activity activity) {
        this.activity = activity;
        try {
            regis = Register.getInstance();
        } catch (NoDaoSetException e) {
            e.printStackTrace();
        }
        loginSharedPreferences= new LoginSharedPreferences(activity);
    }

    public void setListener(mycustomer_click_lisner listener) {
        this.listener = listener;
    }

    public interface mycustomer_click_lisner{
        void onObjectReady(String response);
        void onFailed(String s);
    }

    public void get_Transactions(String start, String end ){
        AndroidNetworking.post(URLS.Get_Transactions)
                .addBodyParameter("ExpectedFrom",start)
                .addBodyParameter("ExpectedTo",end)
                .addHeaders("Authorization","Bearer " +loginSharedPreferences.getAccessToken())
                .setTag("test")
                .setContentType("application/json")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        //TODO handel get_Transactions response
                        if (listener != null){



                            if ( new ResultHandler().validateHandlerResult(activity,response)){

                                for (int i=0;i<response.length();i++)
                                {
                                    JSONObject tempj;
                                    try {
                                          tempj= response.getJSONObject(i);

                                    } catch (JSONException e) {
                                        tempj=null;
                                        e.printStackTrace();
                                    }
                                    if (tempj!=null)
                                    {
                                        Sale tmp = new Sale(tempj);
                                        regis.ensertendedSale(tmp, Globalclass.ENDED);
                                    }
                                }


                                listener.onObjectReady(response.toString() );

                            }

                        }

                        // do anything with response
                        Log.d("apidata", response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });



    }


}
