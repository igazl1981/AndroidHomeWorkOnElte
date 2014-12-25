package com.fitbuilder.productbrowser.config;

public class Globals {

    public static final String ORDER_LIST_URL = "http://php.android.sportnutrition.hu/get_orders.php";
    public static final String CATEGORY_LIST_URL = "http://php.android.sportnutrition.hu/get_categories.php";
    public static final String PRODUCT_LIST_URL = "http://php.android.sportnutrition.hu/index.php";

    public static final String PRODUCT_IMAGE_URL = "http://www.sportnutrition.hu/gallery/product/100x100/";
    public static final String CURRENCY = "HUF";

    public enum ProductSort {
        NAME_ASC, NAME_DESC, PRICE_ASC, PRICE_DESC
    }
}
