package com.fitbuilder.productbrowser.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.config.Globals;
import com.fitbuilder.productbrowser.gui.ProductAdapter;
import com.fitbuilder.productbrowser.models.Category;
import com.fitbuilder.productbrowser.models.Product;
import com.fitbuilder.productbrowser.net.ProductsDownloader;

import java.util.ArrayList;

public class ProductsListFragment extends ListFragment implements
        ProductsDownloader.ProductsDownloaderInterface,
        AbsListView.OnScrollListener,
        CompoundButton.OnCheckedChangeListener {

    private Globals.ProductSort sortBy = Globals.ProductSort.NAME_ASC;
    private int itemDownloadSize = 10;
    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startingPageIndex = 0;
    private boolean actionsOnly = false;

    Category selectedCategory;

    private ProductsDownloader downloader;
    private ArrayList<Product> products;
    private ProductsListCallbacks callbacks;

    private boolean downloadStarted = false;

    private ToggleButton actionsButton;
    private Button categoriesButton;

    private LinearLayout grpButtons;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }


        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            loadMore(currentPage + 1, totalItemCount);
            loading = true;
        }

    }

    private void loadMore(int currentPage, int totalItemCount) {
        downloadStarted = true;
        downloader = new ProductsDownloader(this, getActivity());
        downloader.execute(generateUrl(currentPage));

        Log.d("FBLog", "SCROLL: Page:" + String.valueOf(currentPage) + "; TotalCount: " + String.valueOf(totalItemCount));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.btnActions) {
            actionsOnly = buttonView.isChecked();
            refreshList();
        }
    }

    public interface ProductsListCallbacks {
        public void productSelect(Product product);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("FBLog", "ProductListFragment onAttach()");
        try {
            callbacks = (ProductsListCallbacks) activity;
        } catch (ClassCastException classException) {
            throw new RuntimeException("Activity must implements ProductsListCallbacks");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("FBLog", "ProductsListFragment onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            products = (ArrayList<Product>) savedInstanceState.getSerializable("PRODUCTS");
            if (products != null) {
                ProductAdapter productAdapter = new ProductAdapter(getActivity(), products);
                setListAdapter(productAdapter);
                getListView().setOnScrollListener(this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("PRODUCTS", products);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("FBLog", "ProductsListFragment onCreateView");
        try {
            View v = inflater.inflate(R.layout.fragment_products_list, container, false);
            actionsButton = (ToggleButton) v.findViewById(R.id.btnActions);
            actionsButton.setOnCheckedChangeListener((android.widget.CompoundButton.OnCheckedChangeListener) this);
//            actionsButton.setEnabled(false);
            categoriesButton = (Button) v.findViewById(R.id.btnCategories);
            categoriesButton.setOnClickListener((View.OnClickListener) callbacks);

            grpButtons = (LinearLayout) v.findViewById(R.id.grpProductistBtns);
            if (products == null)
                grpButtons.setVisibility(LinearLayout.GONE);
            else
                grpButtons.setVisibility(LinearLayout.VISIBLE);
            return v;
        } catch (Exception e) {
            Log.d("FBLog", e.getMessage());
        }

        return null;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("FBLog", "ProductsListFragment onStart");
        if (products == null && !downloadStarted) {
            refreshList(null);
        }
        if (products != null)
            getListView().setOnScrollListener(this);
    }

    public void changeSort(Globals.ProductSort sort) {
        sortBy = sort;
        refreshList(selectedCategory);
    }

    public void refreshList() {
        products = null;
        downloadStarted = true;
        downloader = new ProductsDownloader(this, getActivity());
        downloader.execute(generateUrl());
    }

    public void refreshList(Category category) {
        products = null;
        downloadStarted = true;
        downloader = new ProductsDownloader(this, getActivity());
        if (category != null) {
            selectedCategory = category;
        }
        downloader.execute(generateUrl());
    }

    public String getName() {
        return "ProductListFragment";
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        callbacks.productSelect(products.get(position));
    }

    @Override
    public void productsDownloaded(ArrayList<Product> downloadedProducts) {
        grpButtons.setVisibility(LinearLayout.VISIBLE);
        downloader = null;
        downloadStarted = true;
        Log.d("FBLog", "productsDownloaded");
        if (getActivity() != null) {
            if (products == null) {
                products = downloadedProducts;
                ProductAdapter adapter = new ProductAdapter(getActivity(), products);
                setListAdapter(adapter);
                getListView().setOnScrollListener(this);
            } else {
                products.addAll(downloadedProducts);
                ((ProductAdapter) getListAdapter()).notifyDataSetChanged();
            }
        }
    }

    @Override
    public void productsDownloadFailed() {
        downloader = null;
        setListAdapter(null);

        grpButtons.setVisibility(LinearLayout.GONE);

        Toast.makeText(getActivity(),
                getString(R.string.msgProductDownloadError), Toast.LENGTH_SHORT).show();
    }


    private String generateUrl(int currentPage) {

        StringBuilder url = new StringBuilder(generateUrl());

        url.append("&page=").append(currentPage).append("&rows=").append(itemDownloadSize);

        Log.d("FBLog", url.toString());

        return url.toString();
    }

    private String generateUrl() {
        StringBuilder url = new StringBuilder(Globals.PRODUCT_LIST_URL);
        url.append("?sort=").append(sortBy);

        if (selectedCategory != null)
            url.append("&category=").append(selectedCategory.getId());

        if (actionsOnly)
            url.append("&actions=").append(1);


        Log.d("FBLog", url.toString());

        return url.toString();
    }

}
