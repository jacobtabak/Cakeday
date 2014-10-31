package me.tabak.nerdery.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.tabak.nerdery.MyApplication;

@Module(
    includes = {
        DataModule.class,
        UiModule.class
    }
)
public class MyModule {
  private final MyApplication mApp;

  public MyModule(MyApplication app) {
    mApp = app;
  }

  @Provides @Singleton
  MyApplication provideApplication() {
    return mApp;
  }
}
