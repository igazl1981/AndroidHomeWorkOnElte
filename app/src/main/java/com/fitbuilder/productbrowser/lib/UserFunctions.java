package com.fitbuilder.productbrowser.lib;

import android.content.Context;

import com.fitbuilder.productbrowser.db.UserDbHandler;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igazl on 2014.12.08..
 */
public class UserFunctions  {
    private JSONParser parser;
    private static String loginURL = "http://php.android.sportnutrition.hu/login.php";
    private static String login_tag = "login";

    // constructor
    public UserFunctions(){
        parser = new JSONParser();
    }

    public JSONObject loginUser(String username, String password){
        List params = new ArrayList();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = parser.getJSONFromUrl(loginURL, params);
        return json;
    }

    public boolean logoutUser(Context context){
        UserDbHandler db = new UserDbHandler(context);
        db.resetTables();
        return true;
    }

    public boolean isLoggedIn(Context context){
        UserDbHandler db = new UserDbHandler(context);

        return db.getRowCount() > 0;
    }
}
