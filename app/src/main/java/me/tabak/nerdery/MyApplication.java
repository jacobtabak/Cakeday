package me.tabak.nerdery;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;
import timber.log.Timber;

public class MyApplication extends Application {
  private ObjectGraph mObjectGraph;

  @Override
  public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      // TODO: Set up custom release logger to send unexpected errors to an analytics service
    }
    buildObjectGraphAndInject();
  }

  public void inject(Object o) {
    mObjectGraph.inject(o);
  }

  public void buildObjectGraphAndInject() {
    mObjectGraph = ObjectGraph.create(Modules.list(this));
    mObjectGraph.injectStatics();
  }

  public static MyApplication get(Context context) {
    return (MyApplication) context.getApplicationContext();
  }
}
