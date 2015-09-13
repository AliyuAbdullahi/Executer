package com.uber.executer.models;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by aliyuolalekan on 8/12/15.
 */
public class BookedEventData {

  @SerializedName("startTime")
  public String startTime;

  @SerializedName("uid")
  public String uid;

  @SerializedName("reminder")
  public String reminder;

  @SerializedName("duration")
  public String duration;

  @SerializedName("created")
  public String created;
  @SerializedName ("address")
  public String address;
  @SerializedName ("destination")
  public JSONObject destination;
  public void setStartTime (String startTime) {
    this.startTime = startTime;
  }

  public void setUid (String uid) {
    this.uid = uid;
  }

  public void setReminder (String reminder) {
    this.reminder = reminder;
  }

  public void setDuration (String duration) {
    this.duration = duration;
  }

  public void setCreated (String created) {
    this.created = created;
  }

  public void setAddress (String address) {
    this.address = address;
  }

  public String getStartTime () {
    return startTime;
  }

  public String getUid () {
    return uid;
  }

  public String getReminder () {
    return reminder;
  }

  public String getDuration () {
    return duration;
  }

  public String getCreated () {
    return created;
  }

  public String getAddress () {
    return address;
  }
}

