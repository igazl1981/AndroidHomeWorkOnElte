package com.fitbuilder.productbrowser.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.models.Product;

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

public class ProductsDownloader extends AsyncTask<String, Void, String> {

    public interface ProductsDownloaderInterface {
        void productsDownloaded(ArrayList<Product> products);
        void productsDownloadFailed();
    }

    ProgressDialog progressDialog;

    ProductsDownloaderInterface listener;
    Context context;

    public ProductsDownloader(ProductsDownloaderInterface listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.msgProductDownload));
        progressDialog.setMessage(context.getString(R.string.msgLoading));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("FITBUILDERlog", String.valueOf(strings));
        if (strings.length == 0) return null;
        try {
            Log.d("FITBUILDERlog", "DOWNLOADING");
            URL url = new URL(strings[0]);

            Log.d("FITBUILDERlog", url.toString());
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            String line = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                Log.d("FITBUILDERlog", line);
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
        Log.d("FITBUILDERlog", "onPostExecute");
        Log.d("FITBUILDERlog", "String: " + s);
        if (listener != null) {
            if (s == null) {
                listener.productsDownloadFailed();
            }
            else {
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray errors = obj.getJSONArray("errors");
                    Log.d("FITBUILDERlog", "Errors: " + String.valueOf(errors.length()));
                    if (errors.length() != 0) listener.productsDownloadFailed();
                    else {
                        ArrayList<Product> result = new ArrayList<>();
                        JSONObject data = obj.getJSONObject("data");
                        JSONArray products = data.getJSONArray("products");
                        Log.d("FITBUILDERlog", "Products: " + String.valueOf(products.length()));
                        for (int i = 0; i < products.length(); i++) {
                            Product product = Product.parseFromJSON(products.getJSONObject(i));
                            result.add(product);
                        }
                        listener.productsDownloaded(result);
                    }

                } catch (JSONException e) {
                    Log.d("FITBUILDERlog", e.getMessage());
                    listener.productsDownloadFailed();
                }
            }
        }
    }
}
