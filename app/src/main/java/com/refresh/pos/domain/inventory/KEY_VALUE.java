package com.refresh.pos.domain.inventory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mahmoudrashad on 3/4/2018.
 */

public class KEY_VALUE {
    String Key,Value;


    public KEY_VALUE(JSONObject opj){
        try {Key=opj.getString("Key");} catch (JSONException e) {Key="";e.printStackTrace();}
        try {Value=opj.getString("Value");} catch (JSONException e) {Value="";e.printStackTrace();}

    }

    public KEY_VALUE(String key, String value) {

        Key = key;
        Value = value;
    }

    @Override
    public String toString() {
        return Value;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }


}
