package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.refresh.pos.domain.sale.Sale;
import com.refresh.pos.techicalservices.Globalclass;
import com.refresh.pos.techicalservices.URLS;
import com.refresh.pos.techicalservices.utils.LoginSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.refresh.pos.networkmanger.cookes.okHttpClient;

/**
 * Created by mahmoudrashad on 2/13/2018.
 */

public class Submit_order_Manger {

    Activity activity;
    LoginSharedPreferences loginSharedPreferences;
    mycustomer_click_lisner listener;
    public Submit_order_Manger(Activity activity) {
        this.activity = activity;
        loginSharedPreferences= new LoginSharedPreferences(activity);
    }

    public void setListener(mycustomer_click_lisner listener) {
        this.listener = listener;
    }

    public void Submit_order(Sale sale,
                             String change,
                             String CustomerId,
                             String CustomerName,
                             String Tender,
                             String date
                               ){

        JSONObject jsonopj=new JSONObject();
        JSONArray arropj= sale.itemstoJSONArray();
        try {
            jsonopj.put("BookDate",date);
            jsonopj.put("Change",change);
            jsonopj.put("CostCenterId","1");
            jsonopj.put("CounterId",CustomerId);
            jsonopj.put("CustomerId","1");
            jsonopj.put("CustomerName",CustomerName);
            jsonopj.put("PriceTypeId", Globalclass.price_type);
            jsonopj.put("ShipperId","1");
            jsonopj.put("StoreId",Globalclass.storeid);
            jsonopj.put("Tender",Tender);
            jsonopj.put("ValueDate",date);
            jsonopj.put("Details", arropj);


        } catch (JSONException e) {
            jsonopj= new JSONObject();
            e.printStackTrace();
        }


        AndroidNetworking.post(URLS.Submit_order)
                .addHeaders("Authorization","Bearer " +loginSharedPreferences.getAccessToken())
                .addHeaders("Content-Type", "application/json")

//                .addBodyParameter("BookDate",""+date)
//                .addBodyParameter("Change",""+change)
//                .addBodyParameter("CostCenterId",""+CustomerId)
//                .addBodyParameter("CustomerId","1")
//                .addBodyParameter("CustomerName",""+CustomerName)
//                .addBodyParameter("PriceTypeId",""+ Globalclass.price_type)
//                .addBodyParameter("ShipperId","1")
//                .addBodyParameter("StoreId",""+ Globalclass.storeid)
//                .addBodyParameter("Tender",""+Tender)
//                .addBodyParameter("ValueDate",""+ date)
//                .addBodyParameter("Details",arropj.toString())

                .setOkHttpClient(okHttpClient)
                .addJSONObjectBody(jsonopj)

                .build()

                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null){
                            Toast.makeText(activity,response,Toast.LENGTH_SHORT).show();

                            if (new ResultHandler().validateHandlerResult(activity, response, 1)) {
                                listener.onObjectReady(response );
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
                        }catch (Exception e) { // handle error
                            Log.d("apidata", anError.getErrorBody().toString());
                        }



                    }
                });
    }

    public interface mycustomer_click_lisner {
        void onObjectReady(String response);

        void onFailed(String s);
    }


}
