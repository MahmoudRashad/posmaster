package com.refresh.pos.techicalservices.utils;

import android.content.Context;
import android.content.SharedPreferences;



public class LoginSharedPreferences {

    private Context context;
    private String Email, password, accessToken;
    SharedPreferences.Editor editor;
    SharedPreferences settings;
    //notification config


    public LoginSharedPreferences(Context context) {
        this.context = context;
        settings = context.getSharedPreferences("login", 0);
        editor = settings.edit();
    }



    public String getEmail() {
        settings = context.getSharedPreferences("login", 0);
        return settings.getString("Email","");
    }

    public void setEmail(String phoneNumber) {
        this.Email = phoneNumber;
        settings = context.getSharedPreferences("login", 0);
        editor = settings.edit();
        editor.putString("Email", this.Email);
        editor.commit();
    }

    public String getPassword() {
        settings = context.getSharedPreferences("login", 0);
        return settings.getString("password","");
    }

    public void setPassword(String password) {
        this.password = password;
        settings = context.getSharedPreferences("login", 0);
        editor = settings.edit();
        editor.putString("password", this.password);
        editor.commit();
    }

    public String getAccessToken() {
        settings = context.getSharedPreferences("login", 0);
        return settings.getString("accessToken", "");
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        settings = context.getSharedPreferences("login", 0);
        editor = settings.edit();
        editor.putString("accessToken", accessToken);
        editor.commit();

    }


    public void removeLogin(Context context) {
        settings = context.getSharedPreferences("login", 0);
        editor = settings.edit();
        editor.remove("accessToken");
        editor.remove("Email");
        editor.remove("password");
        editor.commit();
    }
}
