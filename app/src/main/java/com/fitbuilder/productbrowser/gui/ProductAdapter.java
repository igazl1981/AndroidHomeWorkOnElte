package com.fitbuilder.productbrowser.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.config.Globals;
import com.fitbuilder.productbrowser.image.ImageLoader;
import com.fitbuilder.productbrowser.models.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by igazl on 2014.12.02..
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    static class ItemHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView listPriceTextView;
        TextView discountTextView;
        TextView priceTextView;
        TextView vatTextView;
    }

    private ArrayList<Product> products;
    private LayoutInflater inflater;
    ImageLoader imageLoader;

    public ProductAdapter(Context context, ArrayList<Product> objects) {

        super(context, 0, objects);

        products = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(context.getApplicationContext());
        imageLoader.clearCache();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;

        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.product_list_item, parent, false);
            holder = new ItemHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.listItemImage);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.listItemName);
            holder.priceTextView = (TextView) convertView.findViewById(R.id.listItemListPrice);
            holder.listPriceTextView = (TextView) convertView.findViewById(R.id.listItemPrice);
            holder.discountTextView = (TextView) convertView.findViewById(R.id.listItemDiscount);
            //holder.vatTextView = (TextView) convertView.findViewById(R.id.listItemVat);
            convertView.setTag(holder);
        }
        else {
            holder = (ItemHolder) convertView.getTag();
        }

        Product product = products.get(position);

        imageLoader.DisplayImage(product.getImage(), holder.imageView);

        holder.nameTextView.setText(product.getName());
        DecimalFormat df = new DecimalFormat("###,###");
        holder.listPriceTextView.setText(String.valueOf(df.format(product.getListPrice()) + " " + Globals.CURRENCY));
        holder.priceTextView.setText(String.valueOf(df.format(product.getPrice()) + " " + Globals.CURRENCY));
        if (product.getDiscount() > 0)
            holder.discountTextView.setText(String.valueOf("-" + df.format(product.getDiscount()) + "%"));
        else
            holder.discountTextView.setText("");
        //holder.vatTextView.setText(String.valueOf(product.getVat() + "%"));

        return convertView;

    }
}
