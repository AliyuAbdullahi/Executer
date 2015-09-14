package com.uber.executer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.uber.executer.R;
import com.uber.executer.activities.EventBookedDetails;
import com.uber.executer.activities.EventPage;

/**
 * Created by aliyuolalekan on 8/19/15.
 */
public class NotificationService extends Service {
  private NotificationManager mManager;
  NotificationManager manager;
  @Nullable
  @Override
  public IBinder onBind (Intent intent) {
    return null;
  }

  @Override
  public void onCreate () {
    super.onCreate ();
  }

  @Override
  public void onStart (Intent intent, int startId) {
    super.onStart (intent, startId);

    String reminderTime = intent.getStringExtra ("start");
    String address = intent.getStringExtra ("destination");
    String productString = intent.getStringExtra ("type");
    String reminder = intent.getStringExtra ("reminder");
    int icon = R.mipmap.ic_launcher;
    long when = System.currentTimeMillis();
    Notification notification = new Notification(icon, address, when);

    NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
    contentView.setImageViewResource (R.id.image, R.mipmap.ic_launcher);
    contentView.setTextViewText(R.id.title, address);
    contentView.setTextViewText(R.id.text, "Please Click to review");
    notification.contentView = contentView;

    Intent notificationIntent = new Intent(this, EventBookedDetails.class);
    notificationIntent.putExtra ("start", reminderTime);
    notificationIntent.putExtra ("destination", address);
    notificationIntent.putExtra ("reminder", reminder);
    notificationIntent.putExtra ("type", productString);
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    notification.contentIntent = contentIntent;

    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
    notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
    notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
    notification.defaults |= Notification.DEFAULT_SOUND; // Sound

    mNotificationManager.notify(1, notification);
  }


}
