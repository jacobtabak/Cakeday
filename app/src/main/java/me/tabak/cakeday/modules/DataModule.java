package me.tabak.cakeday.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.tabak.cakeday.MyApplication;
import me.tabak.cakeday.data.reddit.DateTimeDeserializer;
import me.tabak.cakeday.data.reddit.RedditObjectDeserializer;
import me.tabak.cakeday.data.reddit.RedditService;
import me.tabak.cakeday.data.reddit.model.RedditObject;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

@Module(
    complete = false,
    library = true,
    injects = { }
)
public class DataModule {
  static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

  @Provides @Singleton
  OkHttpClient provideOkHttpClient(MyApplication app) {
    OkHttpClient client = new OkHttpClient();
    // Install an HTTP cache in the application cache directory.
    try {
      File cacheDir = new File(app.getCacheDir(), "http");
      Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
      client.setCache(cache);
    } catch (IOException e) {
      Timber.e(e, "Unable to install disk cache.");
    }

    return client;
  }

  @Provides @Singleton
  Gson provideGson() {
    return new GsonBuilder()
        .registerTypeAdapter(RedditObject.class, new RedditObjectDeserializer())
        .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
        .create();
  }

  @Provides @Singleton
  OkClient provideRetrofitClient(OkHttpClient client) {
    return new OkClient(client);
  }

  @Provides @Singleton
  RestAdapter.Log provideLog() {
    return message -> Timber.i(message);
  }

  @Provides @Singleton
  RestAdapter provideRestAdapter(Gson gson, OkClient client) {
    return new RestAdapter.Builder()
        .setConverter(new GsonConverter(gson))
        .setEndpoint("http://www.reddit.com")
        .setClient(client)
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setErrorHandler(new RedditErrorHandler())
        .setLog(message -> Timber.i(message))
        .build();
  }

  @Provides @Singleton
  RedditService provideRedditService(RestAdapter restAdapter) {
    return restAdapter.create(RedditService.class);
  }
}
