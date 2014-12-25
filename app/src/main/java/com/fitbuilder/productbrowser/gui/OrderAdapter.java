package com.fitbuilder.productbrowser.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.config.Globals;
import com.fitbuilder.productbrowser.models.Category;
import com.fitbuilder.productbrowser.models.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter<Order> {

    static class ItemHolder {
        TextView textDateOrder,
            textStatus,
            textPrice;
    }

    ArrayList<Order> orders;
    LayoutInflater inflater;

    public OrderAdapter(Context context, ArrayList<Order> objects) {
        super(context, 0, objects);

        orders = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;

        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.order_list_item, parent, false);
            holder = new ItemHolder();
            holder.textDateOrder = (TextView) convertView.findViewById(R.id.orderItemDate);
            holder.textPrice = (TextView) convertView.findViewById(R.id.orderItemPrice);
            holder.textStatus = (TextView) convertView.findViewById(R.id.orderItemStatus);
            convertView.setTag(holder);
        }
        else {
            holder = (ItemHolder) convertView.getTag();
        }

        Order order = orders.get(position);
        DecimalFormat df = new DecimalFormat("###,###");

        holder.textDateOrder.setText(order.getOrderDate());
        holder.textPrice.setText(String.valueOf(df.format(order.getPrice()) + " " + Globals.CURRENCY));
        holder.textStatus.setText(order.getStatus());

        return convertView;
    }
}
