package me.tabak.cakeday.data.adapters;

import android.text.TextUtils;
import me.tabak.cakeday.data.reddit.RedditService;
import me.tabak.cakeday.data.reddit.model.RedditLink;
import me.tabak.cakeday.data.reddit.model.RedditListing;
import me.tabak.cakeday.data.reddit.model.RedditMoreCommentsResponse;
import me.tabak.cakeday.data.reddit.model.RedditResponse;
import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps the comment IDs from a "more" response to an observable that emits the actual comments.
 */
public class StubToListing implements Func1<RedditMoreCommentsResponse, Observable<RedditResponse<RedditListing>>> {
  private final RedditService mRedditService;
  private final RedditLink mLink;

  public StubToListing(RedditService service, RedditLink link) {
    mRedditService = service;
    mLink = link;
  }

  @Override
  public Observable<RedditResponse<RedditListing>> call(RedditMoreCommentsResponse response) {
    List<String> commentIds = new ArrayList<>();
    for (RedditMoreCommentsResponse.RedditMoreComment comment : response.getComments()) {
      commentIds.add(comment.getId());
    }
    String commaDelimitedCommentIds = TextUtils.join(",", commentIds);
    return mRedditService.getInfo(mLink.getSubreddit(), commaDelimitedCommentIds);
  }
}
