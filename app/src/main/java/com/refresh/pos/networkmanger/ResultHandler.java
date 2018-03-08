package com.refresh.pos.networkmanger;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mahmoudrashad on 2/11/2018.
 */

class ResultHandler {
    public boolean validateHandlerResult(Activity context, JSONObject response) {
        //TODO Handl response
        return true;
    }
    public boolean validateHandlerResult(Activity context, JSONArray response) {
        //TODO Handl response
        return true;
    }

    public boolean validateHandlerResult(Activity context, String response) {
        return response!="";
    }
}
