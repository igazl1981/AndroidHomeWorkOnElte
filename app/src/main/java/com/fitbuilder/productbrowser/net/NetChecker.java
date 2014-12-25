package com.fitbuilder.productbrowser.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fitbuilder.productbrowser.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetChecker extends AsyncTask<String, Void, Boolean> {

    public interface NetCheckerInterface {
        public void NetCheckSuccess();
        public void NetCheckFailed();
    }

    private ProgressDialog checkDialog;
    private NetCheckerInterface netCheckerListener;
    private Context context;
    private boolean showProgress;

    public NetChecker(NetCheckerInterface netCheckerListener, Context context) {
        this(netCheckerListener, context, true);
    }

    public NetChecker(NetCheckerInterface netCheckerListener, Context context, boolean showProgress) {
        this.showProgress = showProgress;
        this.context = context;
        this.netCheckerListener = netCheckerListener;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        Log.d("FBLog", "NetCheck PreExecute");
        checkDialog = new ProgressDialog((Context) context);
        checkDialog.setTitle(context.getString(R.string.msgCheckNetwork));
        checkDialog.setMessage(context.getString(R.string.msgLoading));
        checkDialog.setIndeterminate(false);
        checkDialog.setCancelable(true);
        checkDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            netCheckerListener.NetCheckSuccess();
        }
        else {
            netCheckerListener.NetCheckFailed();
        }
    }
}