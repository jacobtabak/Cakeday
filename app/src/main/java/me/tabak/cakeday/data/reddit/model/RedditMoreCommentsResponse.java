package me.tabak.cakeday.data.reddit.model;

import java.util.ArrayList;
import java.util.List;

public class RedditMoreCommentsResponse {
  public class RedditJson {
    RedditData data;
  }

  public class RedditData {
    List<RedditThing> things;
  }

  public class RedditThing {
    RedditMoreComment data;
  }

  public class RedditMoreComment {
    String parent;
    String id;
    String link;

    public boolean isTopLevel() {
      return parent.equals(link);
    }

    public String getId() {
      return id;
    }
  }

  RedditJson json;

  public List<RedditMoreComment> getComments() {
    if (json.data != null && json.data.things != null) {
      List<RedditMoreComment> comments = new ArrayList<>();
      for (RedditThing thing : json.data.things) {
        comments.add(thing.data);
      }
      return comments;
    } else {
      return new ArrayList<>();
    }
  }
}
