package com.uber.executer.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by goodson on 5/14/15.
 */
public class User {

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public UserResponse response;
}
