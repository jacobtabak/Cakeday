package me.tabak.nerdery.data.reddit;


import me.tabak.nerdery.data.reddit.model.RedditComment;
import me.tabak.nerdery.data.reddit.model.RedditListing;
import me.tabak.nerdery.data.reddit.model.RedditMoreResponse;
import me.tabak.nerdery.data.reddit.model.RedditResponse;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

import java.util.List;

public interface RedditService {
  @GET("/{listing}.json?limit=25")
  Observable<RedditResponse<RedditListing>> getLinks(
      @Path("listing") String listing,
      @Query("limit") Integer limit,
      @Query("after") String after,
      @Query("before") String before);

  @GET("/r/{subreddit}/comments/{id}.json")
  Observable<List<RedditResponse<RedditListing>>> getComments(
      @Path("subreddit") String subreddit,
      @Path("id") String id,
      @Query("sort") String sort,
      @Query("limit") Integer limit,
      @Query("depth") Integer depth);

  @FormUrlEncoded
  @POST("/api/morechildren")
  Observable<RedditMoreResponse<RedditComment>> getMoreComments(
      @Field("api_type") String apiType,
      @Field("link_id") String linkId,
      @Field("sort") String sort,
      @Field("children") String children);
}
