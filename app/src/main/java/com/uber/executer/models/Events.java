package com.uber.executer.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aliyuolalekan on 9/11/15.
 */
public class Events {
  public Events(){

  }
  @SerializedName("start")
  public String start;

  @SerializedName("end")
  public String end;

  @SerializedName("location")
  public String location;

  @SerializedName("status")
  public String status;

  @SerializedName("summary")
  public String summary;

  public String getStart () {

    return start;
  }

  public String getEnd () {
    return end;
  }

  public String getLocation () {
    return location;
  }

  public String getStatus () {
    return status;
  }

  public String getSummary () {
    return summary;
  }
}
