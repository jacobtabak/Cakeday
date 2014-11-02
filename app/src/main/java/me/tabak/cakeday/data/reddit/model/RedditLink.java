package me.tabak.cakeday.data.reddit.model;

import java.io.Serializable;

public class RedditLink extends RedditSubmission implements Serializable {
  String domain;
  String selftext_html;
  String selftext;
  String link_flair_text;
  boolean clicked;
  boolean hidden;
  String thumbnail;
  boolean is_self;
  String permalink;
  boolean stickied;
  String url;
  String title;
  int num_comments;
  boolean visited;

  public String getDomain() {
    return domain;
  }

  public String getSelftextHtml() {
    return selftext_html;
  }

  public String getSelftext() {
    return selftext;
  }

  public String getLinkFlairText() {
    return link_flair_text;
  }

  public boolean isClicked() {
    return clicked;
  }

  public boolean isHidden() {
    return hidden;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public String getSubreddit() {
    return subreddit;
  }

  public boolean isSelf() {
    return is_self;
  }

  public String getPermalink() {
    return permalink;
  }

  public boolean isStickied() {
    return stickied;
  }

  public String getUrl() {
    return url;
  }

  public String getTitle() {
    return title;
  }

  public int getNumComments() {
    return num_comments;
  }

  public boolean isVisited() {
    return visited;
  }
}
