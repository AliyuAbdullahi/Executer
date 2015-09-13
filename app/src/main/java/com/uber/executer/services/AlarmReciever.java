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
    Intent myIntent = new Intent(context, NotificationService.class);
    context.startService(myIntent);
  }
}
