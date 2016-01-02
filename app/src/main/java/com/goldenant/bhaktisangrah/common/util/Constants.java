package com.goldenant.bhaktisangrah.common.util;

/**
 * Created by ankita on 1/2/2016.
 */
public class Constants
{
    //http://192.168.0.108/mantra/api/v1/get_category
    private final static String API_DOMAIN_CONSUMER = "http://192.168.0.108/mantra/api/";
    private final static String API_FOLDER = "v1/";

    //Base Url
    private final static String API_BASE_URL = API_DOMAIN_CONSUMER + API_FOLDER;

    //Api Name
    private final static String API_GET_CATEGORY = "get_category";

    //Whole url
    public final static String API_GET_CATEGORY_URL = API_BASE_URL + API_GET_CATEGORY;

    // string constants
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_IMAGE = "category_image";
}
