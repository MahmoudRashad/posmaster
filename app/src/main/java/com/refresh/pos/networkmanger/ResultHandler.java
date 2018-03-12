package com.refresh.pos.networkmanger;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                return true;
            } catch (Exception e) {
//                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                Log.d("validateHandlerResult: ", e.getMessage());
                return false;
            }


        }
        return response != "";
    }
}
