package com.fitbuilder.productbrowser.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.models.Category;
import com.fitbuilder.productbrowser.models.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class OrdersDownloader extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;

    public interface OrderDownloaderInterface {
        void ordersDownloaded(ArrayList<Order> orders);
        void ordersDownloadFailed();
    }

    OrderDownloaderInterface orderListener;
    Context context;

    public OrdersDownloader(OrderDownloaderInterface orderListener, Context context) {
        this.orderListener = orderListener;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.msgOrderDownload));
        progressDialog.setMessage(context.getString(R.string.msgLoading));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        if (params.length == 0) return null;

        try {
            Log.d("FBLog", params[0]);
            URL url = new URL(params[0]);
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            String line = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();

            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        if (orderListener != null) {
            if (s == null) {
                orderListener.ordersDownloadFailed();
            }
            else {
                try {
                    Log.d("FBLog", s);
                    JSONObject obj = new JSONObject(s);
                    JSONArray errors = obj.getJSONArray("errors");
                    Log.d("FBLog", "Errors: " + String.valueOf(errors.length()));
                    if (errors.length() != 0) orderListener.ordersDownloadFailed();
                    else {
                        ArrayList<Order> result = new ArrayList<>();
                        JSONObject data = obj.getJSONObject("data");
                        JSONArray orders = data.getJSONArray("orders");
                        Log.d("FBLog", "Categories: " + String.valueOf(orders.length()));
                        for (int i = 0; i < orders.length(); i++) {
                            Order order = Order.parseFromJSON(orders.getJSONObject(i));
                            result.add(order);
                        }
                        orderListener.ordersDownloaded(result);
                    }

                } catch (JSONException e) {
                    Log.d("FITBUILDERlog", e.getMessage());
                    orderListener.ordersDownloadFailed();
                }
            }
        }
    }
}
