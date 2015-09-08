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
import com.uber.executer.activities.BookedEvents;
import com.uber.executer.activities.MainActivity;

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


    int icon = R.mipmap.ic_launcher;
    long when = System.currentTimeMillis();
    Notification notification = new Notification(icon, "Custom Notification", when);

    NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
    contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
    contentView.setTextViewText(R.id.title, "Custom notification");
    contentView.setTextViewText(R.id.text, "This is a custom layout");
    notification.contentView = contentView;

    Intent notificationIntent = new Intent(this, BookedEvents.class);
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    notification.contentIntent = contentIntent;

    notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
    notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
    notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
    notification.defaults |= Notification.DEFAULT_SOUND; // Sound

    mNotificationManager.notify(1, notification);
  }


}
