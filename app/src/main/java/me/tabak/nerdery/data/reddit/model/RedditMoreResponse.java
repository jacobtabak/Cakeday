package me.tabak.nerdery.data.reddit.model;

import java.util.ArrayList;
import java.util.List;

public class RedditMoreResponse {
  public class RedditJson {
    RedditData data;
  }

  public class RedditData {
    List<RedditObject> things;
  }

  RedditJson json;

  public List<RedditObject> getThings() {
    if (json.data != null && json.data.things != null) {
      return json.data.things;
    } else {
      return new ArrayList<>();
    }
  }
}
