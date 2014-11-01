package me.tabak.nerdery;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import me.tabak.nerdery.data.reddit.model.RedditLink;
import me.tabak.nerdery.ui.fragments.MyWebViewFragment;
import me.tabak.nerdery.ui.fragments.RedditLinkDetailFragment;

public class MainActivity extends ActionBarActivity {
  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.container) ViewGroup mContainer;
  @InjectView(R.id.detail_container) ViewGroup mDetailContainer;
  @InjectView(R.id.list_container) ViewGroup mListContainer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    setSupportActionBar(mToolbar);
  }

  public Toolbar getToolbar() {
    return mToolbar;
  }

  public void showWebView(String url) {
    MyWebViewFragment webViewFragment = MyWebViewFragment.newInstance(url);
    getSupportFragmentManager()
        .beginTransaction()
        .setCustomAnimations(R.anim.slide_up_in, R.anim.fade_slight_out, R.anim.fade_slight_in, R.anim.slide_down_out)
        .add(android.R.id.content, webViewFragment)
        .addToBackStack("webview")
        .commitAllowingStateLoss();
  }

  public ViewPropertyAnimatorCompat animateToolbarVisibility(boolean visible) {
    return ViewCompat.animate(mToolbar)
        .setInterpolator(new AccelerateInterpolator())
        .setDuration(200)
        .translationY(visible ? 0 : -mToolbar.getHeight());
  }

  public void showDetailFragment(RedditLink link) {
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.detail_container, RedditLinkDetailFragment.newInstance(link))
        .commitAllowingStateLoss();

    float listWeight = getResources().getInteger(R.integer.list_collapsed_weight);
    float detailWeight = getResources().getInteger(R.integer.detail_expanded_weight);
    if (Build.VERSION.SDK_INT >= 11) {
      animateWeight("detail", mDetailContainer, detailWeight);
      animateWeight("list", mListContainer, listWeight);
    } else {
      mListContainer.setLayoutParams(
          new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, listWeight));
      mDetailContainer.setLayoutParams(
          new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, detailWeight));
    }
  }

  public void hideDetailFragment(RedditLink link) {

  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void animateWeight(String tag, View view, float destWeight) {
    float currentWeight = ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
    Log.d(tag, "*** ANIMATING FROM " + currentWeight + " TO " + destWeight);
    if (currentWeight == destWeight) {
      return;
    }
    ValueAnimator detailAnimator = ValueAnimator.ofFloat(currentWeight, destWeight);
    detailAnimator.addUpdateListener(animation -> {
      float weight = (float) animation.getAnimatedValue();
      Log.d(tag, "Animating weight to " + weight);
      view.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight));
    });
    detailAnimator.setDuration(250).start();
  }
}
