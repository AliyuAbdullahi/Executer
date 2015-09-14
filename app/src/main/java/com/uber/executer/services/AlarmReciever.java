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
    String reminderTime = intent.getStringExtra ("start");
    String address = intent.getStringExtra ("destination");
    String productString = intent.getStringExtra ("type");
    String reminder = intent.getStringExtra ("reminder");
    Intent myIntent = new Intent(context, NotificationService.class);
    myIntent.putExtra ("start", reminderTime);
    myIntent.putExtra ("destination", address);
    myIntent.putExtra ("type", productString);
    myIntent.putExtra ("reminder", reminder);
    context.startService(myIntent);
  }
}
