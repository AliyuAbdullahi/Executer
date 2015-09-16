package com.uber.executer.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by aliyuolalekan on 8/19/15.
 */
public class AlarmReciever extends BroadcastReceiver {

  @Override
  public void onReceive (Context context, Intent intent) {
    String productId = intent.getStringExtra ("id");
    String reminderTime = intent.getStringExtra ("start");
    String address = intent.getStringExtra ("destination");
    String productString = intent.getStringExtra ("type");
    String reminder = intent.getStringExtra ("reminder");
    String summary = intent.getStringExtra ("summary");
    Intent myIntent = new Intent(context, NotificationService.class);
    myIntent.putExtra ("start", reminderTime);
    myIntent.putExtra ("destination", address);
    myIntent.putExtra ("type", productString);
    myIntent.putExtra ("reminder", reminder);
    myIntent.putExtra ("id", productId);
    myIntent.putExtra ("summary", summary);

    context.startService(myIntent);
  }
}
