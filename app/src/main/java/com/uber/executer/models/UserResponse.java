package com.uber.executer.models;

import com.google.gson.annotations.SerializedName;
import com.uber.executer.Vars;

/**
 * Created by goodson on 7/8/15.
 */
public class UserResponse {

    @SerializedName("first_name")
    public String first_name;

    @SerializedName("last_name")
    public String last_name;

    @SerializedName(Vars.KEY_EMAIL)
    public String email;

    @SerializedName("promo_code")
    public String promo_code;

    @SerializedName("accessToken")
    public String accessToken;

    @SerializedName(Vars.KEY_IMG)
    public String picture;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("google_token")
    public String google_token;

}
