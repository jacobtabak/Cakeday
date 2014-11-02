package me.tabak.nerdery.data.adapters;

import me.tabak.nerdery.data.reddit.model.RedditComment;
import me.tabak.nerdery.data.reddit.model.RedditListing;
import me.tabak.nerdery.data.reddit.model.RedditObject;
import me.tabak.nerdery.data.reddit.model.RedditResponse;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps RedditResponse<RedditListing> to an actual list of comments (including depth information)
 */
public class ListingToCommentList implements Func1<RedditResponse<RedditListing>, List<RedditComment>> {
  @Override
  public List<RedditComment> call(RedditResponse<RedditListing> response) {
    List<RedditComment> comments = new ArrayList<>();
    List<RedditObject> objects = response.getData().getChildren();
    @SuppressWarnings("unchecked") List<RedditComment> castedObjects = (List) objects;
    for (RedditComment comment : castedObjects) {
      // determine the depth of this child
      if (comments.size() > 0) {
        RedditComment lastComment = comments.get(comments.size() - 1);
        if (lastComment.getName().equals(comment.getParentId())) {
          comment.setDepth(lastComment.getDepth() + 1);
        }
      }
      comments.add(comment);
    }
    return comments;
  }
}
