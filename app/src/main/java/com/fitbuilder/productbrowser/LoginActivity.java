package com.fitbuilder.productbrowser;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fitbuilder.productbrowser.db.UserDbHandler;
import com.fitbuilder.productbrowser.lib.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends Activity {
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "user_id";
    private static String KEY_USERNAME = "username";
    private static String KEY_FIRSTNAME = "firstname";
    private static String KEY_LASTNAME = "lastname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";

    Button btnLogin;
    EditText edtUsername,
            edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        edtUsername = (EditText) findViewById(R.id.edit_login_username);
        edtPassword = (EditText) findViewById(R.id.edit_login_password);

        btnLogin = (Button) findViewById(R.id.btn_login_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!edtUsername.getText().toString().equals("")) && (!edtPassword.getText().toString().equals(""))) {
                    //new ProcessLogin().execute();
                    NetAsync(v);
                } else if ((!edtPassword.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.textLoginPasswordEmpty), Toast.LENGTH_SHORT).show();
                } else if ((!edtPassword.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.textLoginUsernameEmpty), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.textBothEmpty), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class NetCheck extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog checkDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Log.d("FBLog", "NetCheck PreExecute");
            checkDialog = new ProgressDialog(LoginActivity.this);
            checkDialog.setTitle(getString(R.string.msgCheckNetwork));
            checkDialog.setMessage(getString(R.string.msgLoading));
            checkDialog.setIndeterminate(false);
            checkDialog.setCancelable(true);
            checkDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean network) {
            if (checkDialog != null)
                checkDialog.dismiss();
            if (network) {
                new ProcessLogin().execute();
            }
            else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msgNetworkError), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ProcessLogin extends AsyncTask<String, Void, JSONObject> {

        private ProgressDialog pDialog;
        String username, password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            edtUsername = (EditText) findViewById(R.id.edit_login_username);
            edtPassword = (EditText) findViewById(R.id.edit_login_password);
            username = edtUsername.getText().toString();
            password = edtPassword.getText().toString();

            pDialog = ProgressDialog.show(LoginActivity.this, getString(R.string.textMsgContacting), getString(R.string.textMsgLoggingIn), true);
            pDialog.setCancelable(true);

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(username, password);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    String res = json.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) == 1){
                        pDialog.setMessage(getString(R.string.textMsgGettingUserSpace));
                        pDialog.setTitle(getString(R.string.textMsgGettingUserData));
                        UserDbHandler db = new UserDbHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(
                                json_user.getString(KEY_FIRSTNAME),
                                json_user.getString(KEY_LASTNAME),
                                json_user.getString(KEY_EMAIL),
                                json_user.getString(KEY_USERNAME),
                                json_user.getString(KEY_UID),
                                json_user.getString(KEY_CREATED_AT)
                        );

                        pDialog.dismiss();

                        Intent intent = new Intent();
                        intent.putExtra("USER_LOGGED_IN", true);

                        setResult(RESULT_OK, intent);

                        finish();

                        return;
                    }else{
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.textMsgLoggingErrorPassUser), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void NetAsync(View view) {
        new NetCheck().execute();
    }
}
