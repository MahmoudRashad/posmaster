package com.refresh.pos.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.refresh.pos.R;
import com.refresh.pos.domain.Branche;
import com.refresh.pos.networkmanger.Get_Branches_Manger;
import com.refresh.pos.networkmanger.LogInManager;
import com.refresh.pos.techicalservices.Globalclass;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends Activity {


    Spinner mySpinner;
    ArrayList<Branche> branches_list_s;
    private EditText Email;
    private EditText password;
    private Button skipbtn;
    private Button loginbtn;
    private String officeid="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializationUI();
        get_Branches();
    }

    private void get_Branches() {

        Get_Branches_Manger get_branches_manger= new Get_Branches_Manger(LoginActivity.this);
        get_branches_manger.setCustomObjectListener(new Get_Branches_Manger.MyCustomObjectListener() {
            @Override
            public void onObjectReady(ArrayList<Branche> branches_list) {
                branches_list_s=branches_list;
                ArrayAdapter<Branche> adapter =
                        new ArrayAdapter<Branche>(getApplicationContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                branches_list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mySpinner.setAdapter(adapter);

            }

            @Override
            public void onFailed(String title) {

                Log.e("onFailed:getbranches ",title );

            }
        });
      get_branches_manger.get_Branches();




    }

    private void initializationUI() {
        Email = findViewById(R.id.email);
        Email.setText("haytham115@hotmail.com");
        password = findViewById(R.id.passWord);
        password.setText("123456");

        skipbtn = findViewById(R.id.skipbtn);
        loginbtn = findViewById(R.id.loginButton);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButtonAction();
            }
        });


        mySpinner = findViewById(R.id.Branches_sp);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                officeid = branches_list_s.get(i).getOfficeId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }

    void loginButtonAction() {

        if (validation()) {

            final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(true);
            pDialog.show();

            LogInManager logInManager = new LogInManager(LoginActivity.this);
            logInManager.setCustomObjectListener(new LogInManager.MyCustomObjectListener() {
                @Override
                public void onObjectReady(String title) {


                    pDialog.setTitleText(title)
                            .setConfirmText(getResources().getString(R.string.dialog_ok))
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        Log.d("onObjectReady: ",title);
                        go();
                    pDialog.dismiss();



                }

                @Override
                public void onFailed(String jsobnMessage) {
                    try {

                        pDialog.setTitleText(getResources().getString(R.string.invalidlogin))
                                .setConfirmText(getResources().getString(R.string.dialog_ok))
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);

//                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText(title)
//                                .setContentText(message)
//                                .setConfirmText(getResources().getString(R.string.dialog_ok))
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        sDialog.dismissWithAnimation();
//                                    }
//                                })
//                                .show();
//                        Toast.makeText(LoginActivity.this,message,Toast.LENGTH_LONG).show();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            logInManager.logIn(Email.getText().toString(), password.getText().toString(),officeid);

        }
    }

    private boolean validation() {

        if (Email.getText().toString().trim().length() == 0) {
            Email.setError(getString(R.string.erroremail));
            return false;
        } else if (password.getText().toString().trim().length() == 0) {
            password.setError(getString(R.string.errorpassword));
            return false;
        }

        return true;
    }
    private void go() {

        Intent newActivity = new Intent(LoginActivity.this,
                MainActivity.class);
        Globalclass.fristlogin = true;
        startActivity(newActivity);
        LoginActivity.this.finish();
    }
}