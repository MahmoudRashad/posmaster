package com.refresh.pos.techicalservices;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.refresh.pos.domain.sale.Sale;
import com.refresh.pos.domain.sale.SaleLedger;
import com.refresh.pos.networkmanger.Submit_order_Manger;
import com.refresh.pos.networkmanger.Transactoin_manger;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mahmoudrashad on 2/11/2018.
 */

public class Globalclass {

    public static Activity activity=null;
    public static String price_type="1";
    public static String storeid ="1";
    public static String counterid ="1";


    public static String wiat_syncserver="wiat_syncserver";
    public static String ENDED="ENDED";
    public static boolean sync=false;
    public static boolean fristlogin=false;
    public static int curr_year = 2018;
    static SaleLedger saleLedger = null;
    static String formattedDate;
    private static String startdate="2018-02-01 00:00:00";

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void sync_last_orders(Activity activity) {
        sync=true;


        try {
            saleLedger = SaleLedger.getInstance();
        } catch (NoDaoSetException e) {
            e.printStackTrace();
        }
        List<Sale> list = null;
        list = saleLedger.getAll_local_Sale();
        for (int i=0;i<list.size();i++)
        {
            submit_order(list.get(i),activity);
        }
        sync= false;
    }
    public static void Gettransactoin (Activity activity){
        Transactoin_manger transactoin_manger = new Transactoin_manger(activity);
        transactoin_manger.setListener(new Transactoin_manger.mycustomer_click_lisner() {
            @Override
            public void onObjectReady(String response) {

            }

            @Override
            public void onFailed(String s) {

            }
        });
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        transactoin_manger.get_Transactions(startdate,date);
    }

    public  static void submit_order(final Sale sale, Activity activity){
        sync=true;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
              formattedDate = df.format(sale.getEndTime());
        }catch (Exception e){
              formattedDate ="";
        }




        Submit_order_Manger submit_order_manger = new Submit_order_Manger(activity);
        submit_order_manger.setListener(new Submit_order_Manger.mycustomer_click_lisner() {
            @Override
            public void onObjectReady(String response) {
                if (response!=""){
                    saleLedger.updateSaleEnded(sale);
                    sync =false;
//					Toast.makeText(getActivity(),getResources().getString(R.string.orderSubmited),Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailed(String s) {
                Log.e("error submit order ", s );
                sync =false;

            }
        });

        double Tender = sale.getTotal();
        submit_order_manger.Submit_order(sale,"0","1",
                "JOTAY",""+Tender,formattedDate);

    }

    private String getsha512(String pass) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(pass.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }catch (Exception e){
            Log.e( "onCreate: ","sha512 error " );
            return "";
        }

    }


}
