package com.fitbuilder.productbrowser.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.config.Globals;
import com.fitbuilder.productbrowser.gui.OrderAdapter;
import com.fitbuilder.productbrowser.models.Order;
import com.fitbuilder.productbrowser.net.OrdersDownloader;

import java.util.ArrayList;

public class OrdersListFragment extends ListFragment implements OrdersDownloader.OrderDownloaderInterface {

    private ArrayList<Order> orders;
    private OrdersDownloader downloader;
    private int currentUserId = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            orders = (ArrayList<Order>) savedInstanceState.getSerializable("ORDERS");
            if (orders == null) {
                OrderAdapter adapter = new OrderAdapter(getActivity(), orders);
                setListAdapter(adapter);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            currentUserId = (int) getArguments().getSerializable("USER_ID");

        }
        if (currentUserId > 0) {
            Log.d("FBLog", "OrderListFragment onStart");
            downloader = new OrdersDownloader(this, getActivity());
            downloader.execute(Globals.ORDER_LIST_URL + "?user_id=" + currentUserId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("ORDERS", orders);
    }

    @Override
    public void ordersDownloaded(ArrayList<Order> downloadedOrders) {
        downloader = null;
        if(getActivity() != null) {
            if(orders == null) {
                orders = downloadedOrders;
                OrderAdapter adapter = new OrderAdapter(getActivity(), orders);
                setListAdapter(adapter);
            } else {
                orders.addAll(downloadedOrders);
                ((OrderAdapter)getListAdapter()).notifyDataSetChanged();
            }
        }
    }

    @Override
    public void ordersDownloadFailed() {
        downloader = null;
        setListAdapter(null);

        Toast.makeText(getActivity(),
                getString(R.string.msgCategoryDownloadError), Toast.LENGTH_SHORT).show();
    }
}
