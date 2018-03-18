package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mahmoudrashad on 2/11/2018.
 */

class ResultHandler {
    public boolean validateHandlerResult(Activity context, JSONObject response) {
        //  Handl response
        return true;
    }
    public boolean validateHandlerResult(Activity context, JSONArray response) {
        //  Handl response
        return true;
    }

    public boolean validateHandlerResult(Activity context, String response) {
        return response!="";
    }

    public boolean validateHandlerResult(Activity context, String response, int x) {
        if (x == 1) {
            try {
                int trancatoin_id = Integer.parseInt(response);

                SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                pDialog.setTitleText(response);
                pDialog.setCancelable(true);
                pDialog.setConfirmClickListener(null);
                pDialog.show();
                return true;
            } catch (Exception e) {

                Log.d("validateHandlerResult: ", e.getMessage());
                return false;
            }


        }
        return response != "";
    }
}
