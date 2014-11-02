package me.tabak.cakeday.data.reddit.model;

import com.google.gson.JsonElement;

import me.tabak.cakeday.data.reddit.RedditType;

public class RedditObjectWrapper {
  RedditType kind;
  JsonElement data;

  public RedditType getKind() {
    return kind;
  }

  public JsonElement getData() {
    return data;
  }
}
