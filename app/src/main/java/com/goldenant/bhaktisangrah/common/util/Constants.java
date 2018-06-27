package com.goldenant.bhaktisangrah.common.util;

/**
 * Created by Jaydeep Jikadra on 1/2/2018.
 */
public class Constants
{

   // private final static String API_DOMAIN_CONSUMER = "http://goldenant.in/AndroidAudioApp/api/";

    private final static String API_DOMAIN_CONSUMER = "http://goldenant.in/BhaktiSagar/api/";    //liveApp Url
    private final static String API_FOLDER = "v1/";

    //Base Url
    private final static String API_BASE_URL = API_DOMAIN_CONSUMER + API_FOLDER;

    //Api Name
    private final static String API_GET_CATEGORY = "get_category";
    private final static String API_GET_ITEM_BY_CAT_ID = "get_item_by_cat_id";
    private final static String API_ABOUT_US = "get_about_us";
    private final static String API_GCM_ID = "gcm_register";


    //Whole url
    public final static String API_GET_CATEGORY_URL = API_BASE_URL + API_GET_CATEGORY;
    public final static String API_GET_ITEM_BY_CAT_ID_URL = API_BASE_URL + API_GET_ITEM_BY_CAT_ID;
    public final static String API_ABOUT_US_URL = API_BASE_URL + API_ABOUT_US;
    public final static String API_GCM_ID_URL = API_BASE_URL + API_GCM_ID;

    // string constants
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_IMAGE = "category_image";
    public static final String item_id = "item_id";
    public static final String item_name = "item_name";
    public static final String item_description = "item_description";
    public static final String item_file = "item_file";
    public static final String item_image = "item_image";
    public static final String download_name = "download_name";

    public static final String GCM_ID = "gcm_id";
    public static final String DEVICE_ID = "device_id";


    //Common variable
    public static final String BACK_MODE = "back_mode";
    public static final String Bottom_Banner_placement_id = "903723256364055_1508568682546173";
    public static final String Big_Banner_placement_id = "903723256364055_1508570959212612";

}
