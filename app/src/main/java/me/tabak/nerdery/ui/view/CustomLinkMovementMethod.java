package me.tabak.nerdery.ui.view;

import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import me.tabak.nerdery.MainActivity;

public class CustomLinkMovementMethod extends LinkMovementMethod {
  private final MainActivity mActivity;

  public CustomLinkMovementMethod(MainActivity activity) {
    mActivity = activity;
  }

  public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, android.view.MotionEvent event) {
    int action = event.getAction();

    if (action == MotionEvent.ACTION_UP) {
      int x = (int) event.getX();
      int y = (int) event.getY();

      x -= widget.getTotalPaddingLeft();
      y -= widget.getTotalPaddingTop();

      x += widget.getScrollX();
      y += widget.getScrollY();

      Layout layout = widget.getLayout();
      int line = layout.getLineForVertical(y);
      int off = layout.getOffsetForHorizontal(line, x);

      URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
      if (link.length != 0) {
        String url = link[0].getURL();
        // handle relative links
        if (url.startsWith("/")) {
          url = "http://www.reddit.com" + url;
        }
        if (url.startsWith("https") || url.startsWith("http")) {
          mActivity.showWebView(url);
          return true;
        }
      }
    }
    return super.onTouchEvent(widget, buffer, event);
  }
}