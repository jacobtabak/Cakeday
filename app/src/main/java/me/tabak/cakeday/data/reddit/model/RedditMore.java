package me.tabak.cakeday.data.reddit.model;

import java.util.ArrayList;
import java.util.List;

public class RedditMore extends RedditObject {
  String parent_id;
  String id;
  String name;
  List<String> children;

  public String getParentId() {
    return parent_id;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<String> getChildren() {
    return children;
  }

  /**
   * Take up to count children from the front list.  They will be removed.
   * @param count
   * @return
   */
  public List<String> takeChildren(int count) {
    List<String> subList = getChildren().subList(0, Math.min(getChildren().size(), count));
    List<String> output = new ArrayList<>(subList);
    subList.clear();
    return output;
  }
}
