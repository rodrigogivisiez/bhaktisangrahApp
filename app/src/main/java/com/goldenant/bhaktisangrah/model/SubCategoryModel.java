package com.goldenant.bhaktisangrah.model;

/**
 * Created by Adite on 02-01-2016.
 */
public class SubCategoryModel extends MasterModel
{
    private static final long serialVersionUID = 6104048947898684570L;
    private String item_id;
    private String item_name;
    private String item_description;
    private String item_file;
    private String download_name;
    private String item_image;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_description() {
        return item_description;
    }

    public void setItem_description(String item_description) {
        this.item_description = item_description;
    }

    public String getItem_file() {
        return item_file;
    }

    public void setItem_file(String item_file) {
        this.item_file = item_file;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getDownload_name() {
        return download_name;
    }

    public void setDownload_name(String download_name) {
        this.download_name = download_name;
    }
}
