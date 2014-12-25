package com.fitbuilder.productbrowser.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Order {

    private int id;
    private String orderDate;
    private String status;
    private double price;

    public Order(int id, String orderDate, String status, double price) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static Order parseFromJSON(JSONObject o) throws JSONException{

        int id = o.getInt("id");
        String orderDate = o.getString("dateOrder");
        String status = o.getString("state");
        double price = o.getDouble("price");

        return new Order(id, orderDate, status, price);
    }
}
