package com.vbot.firstreport.ApiClient;

/**
 * Created by Vivek on 09/09/2020.
 */

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

     public static final String BASE_URL = " http://demo.kran.in:8080/farmerassist/";



    public static Retrofit retrofit = null;

    public static Retrofit getRetrofitApiClient() {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }


}