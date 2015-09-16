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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uber.executer.R;
import com.uber.executer.Singletons.Vars;
import com.uber.executer.activities.EventBookedDetails;
import com.uber.executer.activities.EventPage;

import java.util.HashMap;
import java.util.Map;

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
    String address = null;
    String id = null;
    int icon = 0;
    long when = 0;
    String reminderTime = null;
    String productString = null;
    String summary = null;
    String reminder = null;
    try{
      id = intent.getStringExtra ("id");
      reminderTime = intent.getStringExtra ("start");
      address= intent.getStringExtra ("destination");
      productString = intent.getStringExtra ("type");
      reminder = intent.getStringExtra ("reminder");
      summary = intent.getStringExtra ("summary");

      icon = R.mipmap.ic_launcher;
      when = System.currentTimeMillis();

    }catch (Exception e){
      e.printStackTrace ();
    }
    try{
      RequestQueue queue = Volley.newRequestQueue (getApplicationContext ());
      final String requestId = id;
      final String finalProductString = productString;
      StringRequest request = new StringRequest (Request.Method.POST, "http://andelahack.herokuapp.com/trips/"+ Vars.user.response.uuid, new Response.Listener<String> () {
        @Override
        public void onResponse (String response) {
          Toast.makeText (getApplicationContext (), finalProductString + " is on its way for your event", Toast.LENGTH_SHORT).show ();
          Toast.makeText (getApplicationContext (),response,Toast.LENGTH_SHORT).show ();
        }
      }, new Response.ErrorListener () {
        @Override
        public void onErrorResponse (VolleyError error) {
          Toast.makeText (getApplicationContext (), error+"", Toast.LENGTH_SHORT).show ();
        }
      })
      {
        @Override
        protected Map<String, String> getParams () throws AuthFailureError {
          super.getParams ();
          Map<String,String> params = new HashMap<String, String> ();
          params.put ("request_id", requestId);
          return params;
        }

        @Override
        public Map<String, String> getHeaders () throws AuthFailureError {
          super.getHeaders ();
          Map<String,String> params = new HashMap<String, String>();
          params.put("Content-Type","application/x-www-form-urlencoded");
          return params;
        }
      };
      int socketTimeout = 30000;//30 seconds - change to what you want
      RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
      request.setRetryPolicy(policy);

      queue.add (request);

    }catch (Exception e){
      e.printStackTrace ();
    }

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
    notificationIntent.putExtra ("id",id);
    notificationIntent.putExtra ("summary", summary);
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
