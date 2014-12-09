package com.fitbuilder.productbrowser.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.models.Category;

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

/**
 * Created by igazl on 2014.12.03..
 */
public class CategoryDownloader extends AsyncTask<String, Void, String> {

    private ProgressDialog progressDialog;

    public interface CategoryDownloaderInterface {
        void categoriesDownloaded(ArrayList<Category> categories);
        void categoriesDownloadFailed();
    }

    CategoryDownloaderInterface categoryListener;
    Context context;

    public CategoryDownloader(CategoryDownloaderInterface categoryListener, Context context) {
        this.categoryListener = categoryListener;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.msgCategoryDownload));
        progressDialog.setMessage(context.getString(R.string.msgLoading));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        if (params.length == 0) return null;

        try {
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
        if (categoryListener != null) {
            if (s == null) {
                categoryListener.categoriesDownloadFailed();
            }
            else {
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray errors = obj.getJSONArray("errors");
                    Log.d("FBLog", "Errors: " + String.valueOf(errors.length()));
                    if (errors.length() != 0) categoryListener.categoriesDownloadFailed();
                    else {
                        ArrayList<Category> result = new ArrayList<>();
                        JSONObject data = obj.getJSONObject("data");
                        JSONArray categories = data.getJSONArray("categories");
                        Log.d("FBLog", "Categories: " + String.valueOf(categories.length()));
                        for (int i = 0; i < categories.length(); i++) {
                            Category category = Category.parseFromJSON(categories.getJSONObject(i));
                            result.add(category);
                        }
                        categoryListener.categoriesDownloaded(result);
                    }

                } catch (JSONException e) {
                    Log.d("FITBUILDERlog", e.getMessage());
                    categoryListener.categoriesDownloadFailed();
                }
            }
        }
    }
}
