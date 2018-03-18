package com.refresh.pos.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.refresh.pos.R;
import com.refresh.pos.domain.Branche;
import com.refresh.pos.domain.LanguageController;
import com.refresh.pos.networkmanger.Get_Branches_Manger;
import com.refresh.pos.networkmanger.LogInManager;
import com.refresh.pos.techicalservices.Globalclass;

import java.util.ArrayList;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends Activity {


    Spinner mySpinner;
    Spinner mySpinner_lang;
    ArrayList<Branche> branches_list_s;
    boolean selct_lang = false;
    private EditText Email;
    private EditText password;
    private Button skipbtn;
    private Button loginbtn;
    private String officeid="1";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            String lang = LanguageController.getInstance().getLanguage();
            if (lang.equals("ar")) {
                findViewById(R.id.login_lout).setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                ((TextView) findViewById(R.id.lang_TV)).setText(getResources().getString(R.string.login_lang_en));
            } else {
                findViewById(R.id.login_lout).setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                ((TextView) findViewById(R.id.lang_TV)).setText(getResources().getString(R.string.login_lang_ar));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        setdata();
        initializationUI();
        get_Branches();
        get_lang();

    }

    private void setdata() {
        try {

            Email.setText(getIntent().getStringExtra("Email"));
            password.setText(getIntent().getStringExtra("password"));
            mySpinner.setSelection(getIntent().getIntExtra("mySpinner_index", 0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void get_lang() {


        mySpinner_lang = findViewById(R.id.lang_sp);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.lang));
        mySpinner_lang.setAdapter(spinnerArrayAdapter);
        mySpinner_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selct_lang = i != 0;
                if (i == 1)
                    setLanguage("ar");
                else if (i == 2)
                    setLanguage("en");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    /**
     * Set language
     *
     * @param localeString
     */
    private void setLanguage(String localeString) {
        Locale locale = new Locale(localeString);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        LanguageController.getInstance().setLanguage(localeString);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        Intent intent = getIntent();
        intent.putExtra("Email", Email.getText().toString());
        intent.putExtra("password", password.getText().toString());
        intent.putExtra("mySpinner_index", mySpinner.getSelectedItemPosition());


        finish();
        startActivity(intent);
    }

    private void get_Branches() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getResources().getString(R.string.LOADING));
        pDialog.setCancelable(false);
        pDialog.show();

        Get_Branches_Manger get_branches_manger= new Get_Branches_Manger(LoginActivity.this);
        get_branches_manger.setCustomObjectListener(new Get_Branches_Manger.MyCustomObjectListener() {
            @Override
            public void onObjectReady(ArrayList<Branche> branches_list) {

                branches_list_s=branches_list;
                Branche branche = new Branche("-1", getResources().getString(R.string.selectoffice));
                branches_list_s.add(0, branche);
                ArrayAdapter<Branche> adapter =
                        new ArrayAdapter<Branche>(getApplicationContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                branches_list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mySpinner.setAdapter(adapter);
                loginbtn.setEnabled(true);
                pDialog.dismiss();

            }

            @Override
            public void onFailed(String title) {
                pDialog.setTitleText(getResources().getString(R.string.network_error_title))
                        .setContentText(getResources().getString(R.string.network_error_contant))
                        .setConfirmText(getResources().getString(R.string.confirm))
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
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


        findViewById(R.id.lang_TV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TextView) findViewById(R.id.lang_TV)).getText().toString() == getString(R.string.login_lang_ar)) {
                    setLanguage("ar");
                } else {
                    setLanguage("en");
                }
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
        loginbtn.setEnabled(false);




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
            pDialog.setTitleText(getResources().getString(R.string.LOADING));
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
        if (mySpinner.getSelectedItem() == null || ((Branche) mySpinner.getSelectedItem()).getOfficeId() == "-1") {
            ((TextView) mySpinner.getSelectedView()).setError(getString(R.string.errorpassword));
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