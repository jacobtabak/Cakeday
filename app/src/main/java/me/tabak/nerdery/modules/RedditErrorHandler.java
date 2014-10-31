package me.tabak.nerdery.modules;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class RedditErrorHandler implements ErrorHandler {
  @Override
  public Throwable handleError(RetrofitError cause) {
    // TODO: custom error handling for API errors
    return cause;
  }
}
