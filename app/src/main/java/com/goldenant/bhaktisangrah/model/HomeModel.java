package com.goldenant.bhaktisangrah.model;

/**
 * Created by ankita on 1/2/2016.
 */
public class HomeModel extends MasterModel
{
    private static final long serialVersionUID = 6104048947898684570L;

    private String category_id;
    private String category_name;
    private String category_image;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }
}
