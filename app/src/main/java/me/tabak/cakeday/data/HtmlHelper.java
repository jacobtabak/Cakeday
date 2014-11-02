package me.tabak.cakeday.data;

import android.text.Html;
import org.apache.commons.lang.StringEscapeUtils;

public class HtmlHelper {
  public static CharSequence prepareHtml(CharSequence html) {
    CharSequence spanned = Html.fromHtml(StringEscapeUtils.unescapeHtml(html.toString()));
    while (spanned.charAt(spanned.length() - 1) == '\n') {
      spanned = spanned.subSequence(0, spanned.length() - 1);
    }
    return spanned;
  }
}
