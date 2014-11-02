package me.tabak.cakeday.data.reddit;

import me.tabak.cakeday.data.reddit.model.RedditComment;
import me.tabak.cakeday.data.reddit.model.RedditLink;
import me.tabak.cakeday.data.reddit.model.RedditListing;
import me.tabak.cakeday.data.reddit.model.RedditMore;

public enum RedditType {
  t1(RedditComment.class),
  t3(RedditLink.class),
  Listing(RedditListing.class),
  more(RedditMore.class);

  private final Class mCls;

  RedditType(Class cls) {
    mCls = cls;
  }

  public Class getDerivedClass() {
    return mCls;
  }
}
