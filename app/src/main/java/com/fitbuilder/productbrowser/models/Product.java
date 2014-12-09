package com.fitbuilder.productbrowser.models;

import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by igazl on 2014.12.02..
 */
public class Product implements Serializable {
    private String  name;
    private float   listPrice;
    private float   price;
    private float   discount;
    private int     vat;
    private String  description;
    private String  content;
    private String  image;

    public Product(String name, float listPrice, float price, float discount, int vat, String description, String content, String image) {
        this.name = name;
        this.listPrice = listPrice;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.content = content;
        this.vat = vat;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getListPrice() {
        return listPrice;
    }

    public void setListPrice(float listPrice) {
        this.listPrice = listPrice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getVat() {
        return vat;
    }

    public void setVat(int vat) {
        this.vat = vat;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Product parseFromJSON(JSONObject obj) throws JSONException {
        String name = obj.getString("name");
        float listPrice = (float) obj.getDouble("price");
        float price = (float) obj.getDouble("listPrice");
        float discount = (float) obj.getDouble("discount");
        String description = obj.getString("description");
        String content;
        String image = obj.getString("image_file");
        String c = obj.getString("content");
        if (c != null)
             content = Html.fromHtml(c).toString();
        else
            content = c;
        int vat = obj.getInt("vat");

        return new Product(name, listPrice, price, discount, vat, description, content, image);
    }

}
