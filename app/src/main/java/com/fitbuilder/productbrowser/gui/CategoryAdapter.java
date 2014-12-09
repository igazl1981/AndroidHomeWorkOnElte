package com.fitbuilder.productbrowser.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fitbuilder.productbrowser.R;
import com.fitbuilder.productbrowser.models.Category;
import com.fitbuilder.productbrowser.models.Product;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {

    static class ItemHolder {
        TextView nameTextView;
    }

    private ArrayList<Category> categories;
    private LayoutInflater inflater;

    public CategoryAdapter(Context context, ArrayList<Category> objects) {
        super(context, 0, objects);

        categories = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder;

        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.category_list_item, parent, false);
            holder = new ItemHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.categoryListName);
            convertView.setTag(holder);
        }
        else {
            holder = (ItemHolder) convertView.getTag();
        }

        Category category = categories.get(position);

        holder.nameTextView.setText(category.getName());

        return convertView;

    }
}
