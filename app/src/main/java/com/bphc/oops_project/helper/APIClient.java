package com.bphc.oops_project.helper;

import com.bphc.oops_project.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bphc.oops_project.app.Constants.BASE_URL;

public class APIClient {

    private static Retrofit retrofit = null;
    private static Webservices webservices = null;

    private static HttpLoggingInterceptor interceptor =
            new HttpLoggingInterceptor();
    private static OkHttpClient.Builder builder = new OkHttpClient.Builder();

    private APIClient() {
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(interceptor);
            }

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(builder.build())
                    .build();
        }
        return retrofit;
    }


}
