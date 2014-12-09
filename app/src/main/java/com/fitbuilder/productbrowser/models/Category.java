package com.fitbuilder.productbrowser.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by igazl on 2014.12.03..
 */
public class Category implements Serializable {

    private int id;
    private String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Category parseFromJSON(JSONObject o) throws JSONException {

        int id = o.getInt("id");
        String name = o.getString("name");

        return new Category(id, name);
    }
}
