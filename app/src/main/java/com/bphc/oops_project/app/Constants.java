package com.bphc.oops_project.app;

public class Constants {

    public static final String IMGUR_CLIENT_ID = "ba2ab5177fa8205";
    public static final String IMGUR_CLIENT_SECRET = "58fcdb683ff8e0d6670245bfe1da3c5b2e924fe7";

    public static final String BASE_URL = "http://3.88.234.13:3000/";

    public static final String GOOGLE_AUTH = "auth/google";
    public static final String PHONE_AUTH = "account/otp";
    public static final String ADD_USER_DETAILS = "account/complete";

    public static final String DASHBOARD = "dashboard";

    public static final String CREATE_CATEGORY = "dashboard/category/create";
    public static final String DELETE_CATEGORY = "dashboard/category/delete";
    public static final String EDIT_CATEGORY = "dashboard/category/edit";

    public static final String CREATE_ITEM = "/dashboard/item/create";
    public static final String DELETE_ITEM = "/dashboard/item/delete";
    public static final String UPDATE_ITEM_QUANTITY = "/dashboard/item/quantity";

    public static String getImgurClientId() {
        return "Client-ID " + IMGUR_CLIENT_ID;
    }

}
