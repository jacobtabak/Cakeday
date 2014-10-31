package me.tabak.nerdery.data.reddit.model;

import com.google.gson.JsonElement;

import me.tabak.nerdery.data.reddit.RedditType;

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
