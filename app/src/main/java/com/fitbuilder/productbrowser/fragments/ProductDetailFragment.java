package com.fitbuilder.productbrowser.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ProductDetailFragment extends Fragment {

    private Product currentProduct;

    private ImageView imageDetailImage;

    private TextView textDetailName,
        textDetailPrice,
        textDetailListPrice,
        textDetailDiscount,
        textDetailDescription,
        textDetailContent
    ;
    private ImageLoader imageLoader;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            currentProduct = (Product) savedInstanceState.getSerializable("PRODUCT");
            if (currentProduct != null) {
                fill(currentProduct);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("PRODUCT", currentProduct);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_detail, container, false);

        imageLoader = new ImageLoader(getActivity());

        imageDetailImage = (ImageView) v.findViewById(R.id.image_detail_image);

        textDetailName = (TextView) v.findViewById(R.id.text_detail_name);
        textDetailPrice = (TextView) v.findViewById(R.id.text_detail_price);
        textDetailListPrice = (TextView) v.findViewById(R.id.text_detail_list_price);
        textDetailDiscount = (TextView) v.findViewById(R.id.text_detail_discount);
        textDetailDescription = (TextView) v.findViewById(R.id.text_detail_description);
        textDetailContent = (TextView) v.findViewById(R.id.text_detail_content);

        return v;
    }

    public void fill(Product product) {
        currentProduct = product;
        DecimalFormat df = new DecimalFormat("###,###");

        imageLoader.DisplayImage(product.getImage(), imageDetailImage);
        textDetailName.setText(String.valueOf(product.getName()));
        textDetailPrice.setText(String.valueOf(String.valueOf(df.format(product.getPrice()) + " " + Globals.CURRENCY)));
        textDetailListPrice.setText(String.valueOf(String.valueOf(df.format(product.getListPrice()) + " " + Globals.CURRENCY)));

        if (product.getDiscount() > 0)
            textDetailDiscount.setText(String.valueOf("-" + df.format(product.getDiscount()) + "%"));
        else
            textDetailDiscount.setText("");
        if (product.getDescription() != null)
            textDetailDescription.setText(product.getDescription());
        else
            textDetailDescription.setText("");
        if (product.getContent() != null)
            textDetailContent.setText(product.getContent());
        else
            textDetailContent.setText("");

    }

    @Override
    public void onStart() {
        super.onStart();

        if (getArguments() != null && currentProduct == null) {
            currentProduct = (Product) getArguments().getSerializable("PRODUCT");

        }
        if (currentProduct != null)
            fill(currentProduct);
    }

    public String getName() {
        return "ProductDetailFragment";
    }
}
