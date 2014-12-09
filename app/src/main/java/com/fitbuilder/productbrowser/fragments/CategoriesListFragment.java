package com.fitbuilder.productbrowser.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.gui.CategoryAdapter;
import com.fitbuilder.productbrowser.models.Category;
import com.fitbuilder.productbrowser.net.CategoryDownloader;

import java.util.ArrayList;

/**
 * Created by igazl on 2014.12.03..
 */
public class CategoriesListFragment extends ListFragment implements CategoryDownloader.CategoryDownloaderInterface {

    public interface CategoryListCallbacks {
        public void categorySelected(Category category);
    }

    private CategoryListCallbacks callback;
    private ArrayList<Category> categories;
    private CategoryDownloader downloader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (CategoryListCallbacks) activity;
        } catch (ClassCastException classException) {
            throw new RuntimeException("Activity must implements CategoryListCallbacks");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            categories = (ArrayList<Category>) savedInstanceState.getSerializable("CATEGORIES");
            if (categories != null) {
                CategoryAdapter adapter = new CategoryAdapter(getActivity(), categories);
                setListAdapter(adapter);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("CATEGORIES", categories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cateogries_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (categories == null) {
            downloader = new CategoryDownloader(this, getActivity());
            downloader.execute("http://php.android.sportnutrition.hu/get_categories.php");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        try {
            callback.categorySelected(categories.get(position));
        }
        catch (Exception e) {
            Log.d("FBLog", e.getMessage());
        }
    }

    @Override
    public void categoriesDownloaded(ArrayList<Category> downloadedCategories) {
        downloader = null;
        if(getActivity() != null) {
            if(categories == null) {
                categories = downloadedCategories;
                CategoryAdapter adapter = new CategoryAdapter(getActivity(), categories);
                setListAdapter(adapter);
            } else {
                categories.addAll(downloadedCategories);
                ((CategoryAdapter)getListAdapter()).notifyDataSetChanged();
            }
        }
    }

    @Override
    public void categoriesDownloadFailed() {
        downloader = null;
        setListAdapter(null);

        Toast.makeText(getActivity(),
                getString(R.string.msgCategoryDownloadError), Toast.LENGTH_SHORT).show();
    }

    public String getName() {
        return "CategoriesListFragment";
    }
}
