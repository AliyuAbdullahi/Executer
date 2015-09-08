package com.uber.executer.context;

import android.app.Application;
import android.content.Context;

/**
 * Created by aliyuolalekan on 8/6/15.
 */
public class AppContext extends Application {
  private static AppContext myApplication;
  @Override
  public void onCreate () {
    super.onCreate ();
    myApplication = this;
  }

  public static AppContext getInstance(){
    return myApplication;
  }
  public static Context getAppContext(){
    return myApplication.getApplicationContext ();
  }
}
