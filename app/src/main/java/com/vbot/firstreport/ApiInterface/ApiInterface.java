package com.vbot.firstreport.ApiInterface;

import com.vbot.firstreport.VOs.TokenRequest;
import com.vbot.firstreport.VOs.TokenResponse;
import com.vbot.firstreport.VOs.UserBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("authenticate")
    Call<TokenResponse> authenticate(@Body TokenRequest tokenRequest);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("login/authenticateUser")
    Call<UserBean> authenticateUser(@Body UserBean userBean, @Header("Authorization") String sessionToken);

    @GET("forgotPassword")
    Call<Integer> forgotPassword(@Path("mobile") String mobile, @Path("userDomain") String userDomain);

    Call<Integer> resetPassword(UserBean loginmodel, String sessionToken);
}
