package com.bphc.oops_project.helper;

import com.bphc.oops_project.models.ImageResponse;
import com.bphc.oops_project.models.ServerResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.bphc.oops_project.app.Constants.ADD_USER_DETAILS;
import static com.bphc.oops_project.app.Constants.CREATE_CATEGORY;
import static com.bphc.oops_project.app.Constants.CREATE_ITEM;
import static com.bphc.oops_project.app.Constants.CREATE_TASK;
import static com.bphc.oops_project.app.Constants.DASHBOARD;
import static com.bphc.oops_project.app.Constants.DELETE_CATEGORY;
import static com.bphc.oops_project.app.Constants.DELETE_ITEM;
import static com.bphc.oops_project.app.Constants.DELETE_TASK;
import static com.bphc.oops_project.app.Constants.EDIT_CATEGORY;
import static com.bphc.oops_project.app.Constants.GOOGLE_AUTH;
import static com.bphc.oops_project.app.Constants.PHONE_AUTH;
import static com.bphc.oops_project.app.Constants.TODO;
import static com.bphc.oops_project.app.Constants.UPDATE_ITEM_QUANTITY;

public interface Webservices {

    @POST(GOOGLE_AUTH)
    Call<ServerResponse> authWithGoogle(@Body HashMap<String, String> idToken);

    @GET(PHONE_AUTH)
    Call<ServerResponse> requestOTP(@Query("authToken") String authToken,
                                    @Query("phone") String phone,
                                    @Query("hash") String hash);

    @POST(PHONE_AUTH)
    Call<ServerResponse> verifyOTP(@Body HashMap<String, String> credentials);

    @POST(ADD_USER_DETAILS)
    Call<ServerResponse> addUserDetails(@Body HashMap<String, String> userDetails);

    @GET(DASHBOARD)
    Call<ServerResponse> getUserDashboard(@Query("authToken") String authToken);

    @POST(CREATE_CATEGORY)
    Call<ServerResponse> createCategory(@Body HashMap<String, String> categoryDetails);

    @POST(DELETE_CATEGORY)
    Call<ServerResponse> deleteCategory(@Body HashMap<String, String> deleteCategory);

    @POST(EDIT_CATEGORY)
    Call<ServerResponse> editCategory(@Body HashMap<String, String> editDetails);

    @POST(CREATE_ITEM)
    Call<ServerResponse> createItem(@Body HashMap<String, Object> itemDetails);

    @POST(DELETE_ITEM)
    Call<ServerResponse> deleteItem(@Body HashMap<String, Object> itemDetails);

    @POST(UPDATE_ITEM_QUANTITY)
    Call<ServerResponse> updateItemQuantity(@Body HashMap<String, Object> itemDetails);

    @FormUrlEncoded
    @POST("https://api.imgur.com/3/image")
    Call<ImageResponse> postImage(@Header("Authorization") String auth,
                                  @Field("image") String encoded);

    @POST(CREATE_TASK)
    Call<ServerResponse> createTask(@Body HashMap<String, String> taskDetails);

    @GET(TODO)
    Call<ServerResponse> getUserToDos(@Query("authToken") String authToken);

    @POST(DELETE_TASK)
    Call<ServerResponse> deleteTask(@Body HashMap<String, Object> taskDetails);

}
