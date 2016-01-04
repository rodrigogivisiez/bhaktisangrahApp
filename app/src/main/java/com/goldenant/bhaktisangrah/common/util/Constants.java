package com.goldenant.bhaktisangrah.common.util;

/**
 * Created by ankita on 1/2/2016.
 */
public class Constants
{
    //http://192.168.0.108/mantra/api/v1/get_category
    private final static String API_DOMAIN_CONSUMER = "http://goldenant.in/mantra/api/";
    private final static String API_FOLDER = "v1/";

    //Base Url
    private final static String API_BASE_URL = API_DOMAIN_CONSUMER + API_FOLDER;

    //Api Name
    private final static String API_GET_CATEGORY = "get_category";
    private final static String API_GET_ITEM_BY_CAT_ID = "get_item_by_cat_id";


    //Whole url
    public final static String API_GET_CATEGORY_URL = API_BASE_URL + API_GET_CATEGORY;
    public final static String API_GET_ITEM_BY_CAT_ID_URL = API_BASE_URL + API_GET_ITEM_BY_CAT_ID;

    // string constants
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_IMAGE = "category_image";
    public static final String item_id = "item_id";
    public static final String item_name = "item_name";
    public static final String item_description = "item_description";
    public static final String item_file = "item_file";
    public static final String item_image = "item_image";


}
