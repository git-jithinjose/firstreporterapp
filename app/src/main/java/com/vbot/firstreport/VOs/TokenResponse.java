package com.vbot.firstreport.VOs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("sessionToken")
    @Expose
    private String sessionToken;

    public String getUserName() {
        return userName;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}
