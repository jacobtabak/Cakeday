package me.tabak.nerdery.data.reddit.model;

import org.joda.time.DateTime;

public class RedditSubmission extends RedditObject {
  String banned_by;
  String subreddit;
  boolean saved;
  String id;
  int gilded;
  String author;
  int score;
  String name;
  long created;
  String author_flair_text;
  DateTime created_utc;
  int ups;

  public String getBannedBy() {
    return banned_by;
  }

  public String getSubreddit() {
    return subreddit;
  }

  public boolean isSaved() {
    return saved;
  }

  public String getId() {
    return id;
  }

  public int getGilded() {
    return gilded;
  }

  public String getAuthor() {
    return author;
  }

  public int getScore() {
    return score;
  }

  public String getName() {
    return name;
  }

  public long getCreated() {
    return created;
  }

  public String getAuthorFlairText() {
    return author_flair_text;
  }

  public DateTime getCreatedUtc() {
    return created_utc;
  }

  public int getUps() {
    return ups;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof RedditSubmission) {
      return getName().equals(((RedditSubmission) o).getName());
    }
    if (o instanceof String) {
      return getName().equals(o);
    }
    return super.equals(o);
  }
}
