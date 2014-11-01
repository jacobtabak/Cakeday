package me.tabak.nerdery.data.reddit.model;

import java.util.ArrayList;
import java.util.List;

public class RedditMoreResponse<T> {
  public class RedditJson {
    RedditData data;
  }

  public class RedditData {
    List<T> things;
  }

  RedditJson json;

  public List<T> getThings() {
    if (json.data != null && json.data.things != null) {
      return json.data.things;
    } else {
      return new ArrayList<>();
    }
  }
}
