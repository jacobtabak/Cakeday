package me.tabak.cakeday;

import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.doesNotExist;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.equalTo;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
  private static final String LINK_TITLE_TEXTVIEW = "test";
  private static final String LINK_TOPLINE_TEXTVIEW = "topline";
  private static final String COMMENT_BODY_TEXTVIEW = "body";
  private RecyclerView mDetailRecyclerView;
  private RecyclerView mListRecycler;
  private MainActivity mActivity;

  public MainActivityTest() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mActivity = getActivity();
    Thread.sleep(1000);
  }

  public void testWebView() throws Exception {
    mListRecycler = (RecyclerView) mActivity.findViewById(R.id.list_recyclerview);
    mListRecycler.getChildAt(0).findViewById(R.id.link_title_textview).setTag(LINK_TITLE_TEXTVIEW);
    onView(withTagValue(equalTo(LINK_TITLE_TEXTVIEW))).perform(click());
    Thread.sleep(1000);
    onView(withId(R.id.webview)).check(matches(isDisplayed()));
    mActivity.getSupportFragmentManager().popBackStack();
    Thread.sleep(1000);
    onView(withId(R.id.list_recyclerview)).check(matches(isDisplayed()));
    onView(withId(R.id.webview)).check(doesNotExist());
  }

  public void testDetailView() throws Exception {
    mListRecycler = (RecyclerView) mActivity.findViewById(R.id.list_recyclerview);
    mListRecycler.getChildAt(0).findViewById(R.id.link_top_line_textview).setTag(LINK_TOPLINE_TEXTVIEW);
    onView(withTagValue(equalTo(LINK_TOPLINE_TEXTVIEW))).perform(click());
    Thread.sleep(1000);
    mDetailRecyclerView = (RecyclerView) getActivity().findViewById(R.id.detail_recyclerview);
    mDetailRecyclerView.getChildAt(1).findViewById(R.id.comment_body_textview).setTag(COMMENT_BODY_TEXTVIEW);
    onView(withTagValue(equalTo(COMMENT_BODY_TEXTVIEW))).check(matches(isDisplayed()));
    if (mActivity.isDualPane()) {
      onView(withId(R.id.list_recyclerview)).check(matches(isDisplayed()));
    } else {
      mActivity.getSupportFragmentManager().popBackStack();
      Thread.sleep(1000);
      onView(withId(R.id.detail_recyclerview)).check(doesNotExist());
    }
  }
}
