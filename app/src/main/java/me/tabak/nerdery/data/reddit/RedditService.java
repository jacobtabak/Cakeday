package me.tabak.nerdery.data.reddit;


import java.util.List;

import me.tabak.nerdery.data.reddit.model.RedditListing;
import me.tabak.nerdery.data.reddit.model.RedditResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface RedditService {
  @GET("/r/{subreddit}/comments/{id}.json")
  List<RedditResponse<RedditListing>> getComments(
      @Path("subreddit") String subreddit,
      @Path("id") String id
  );

  @GET("/r/{subreddit}/comments/{id}.json")
  void getComments(
      @Path("subreddit") String subreddit,
      @Path("id") String id,
      Callback<List<RedditResponse<RedditListing>>> callback
  );

  @GET("/r/{subreddit}.json")
  RedditResponse<RedditListing> getSubreddit(@Path("subreddit") String subreddit);

  @GET("/r/{subreddit}.json")
  void getSubreddit(
      @Path("subreddit") String subreddit,
      Callback<RedditResponse<RedditListing>> callback);

  @GET("/{listing}.json?limit=25")
  Observable<RedditResponse<RedditListing>> getLinks(
      @Path("listing") String listing,
      @Query("limit") Integer limit,
      @Query("after") String after,
      @Query("before") String before);
}
