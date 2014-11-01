package me.tabak.nerdery;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import me.tabak.nerdery.data.reddit.model.RedditLink;
import me.tabak.nerdery.ui.fragments.MyWebViewFragment;
import me.tabak.nerdery.ui.fragments.RedditLinkDetailFragment;
import me.tabak.nerdery.ui.fragments.RedditLinkListFragment;

public class MainActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {
  @InjectView(R.id.toolbar) Toolbar mToolbar;

  @InjectView(R.id.primary_container) ViewGroup mPrimaryContainer;
  @Optional @InjectView(R.id.secondary_container) ViewGroup mSecondaryContainer;
  private boolean mDetailVisible;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    setSupportActionBar(mToolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    getSupportFragmentManager().addOnBackStackChangedListener(this);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.primary_container, new RedditLinkListFragment(), "list")
        .commitAllowingStateLoss();
  }

  public Toolbar getToolbar() {
    return mToolbar;
  }

  public void showWebView(String url) {
    MyWebViewFragment webViewFragment = MyWebViewFragment.newInstance(url);
    getSupportFragmentManager()
        .beginTransaction()
        .setCustomAnimations(
            R.anim.slide_up_in, R.anim.fade_slight_out, R.anim.fade_slight_in, R.anim.slide_down_out)
        .add(android.R.id.content, webViewFragment)
        .addToBackStack("webview")
        .commitAllowingStateLoss();
  }

  public void showDetailFragment(RedditLink link) {
    mDetailVisible = true;

    RedditLinkDetailFragment fragment = RedditLinkDetailFragment.newInstance(link);
    if (mSecondaryContainer == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left,
              R.anim.slide_in_from_left, R.anim.slide_out_to_right)
          .replace(R.id.primary_container, fragment)
          .addToBackStack("detail")
          .commitAllowingStateLoss();
    } else {
      getSupportFragmentManager()
          .beginTransaction()
          .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
          .replace(R.id.secondary_container, fragment)
          .commitAllowingStateLoss();

      float listWeight = getResources().getInteger(R.integer.list_collapsed_weight);
      float detailWeight = getResources().getInteger(R.integer.detail_expanded_weight);
      if (Build.VERSION.SDK_INT >= 11) {
        animateWeight("detail", mSecondaryContainer, detailWeight);
        animateWeight("list", mPrimaryContainer, listWeight);
      } else {
        mPrimaryContainer.setLayoutParams(
            new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, listWeight));
        mSecondaryContainer.setLayoutParams(
            new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, detailWeight));
      }
    }
  }

  public void hideDetailFragment() {
    mDetailVisible = false;

    if (mSecondaryContainer == null) {
      getSupportFragmentManager().popBackStack();
    } else {
      float listWeight = getResources().getInteger(R.integer.list_expanded_weight);
      float detailWeight = getResources().getInteger(R.integer.detail_collapsed_weight);
      if (Build.VERSION.SDK_INT >= 11) {
        animateWeight("detail", mSecondaryContainer, detailWeight);
        animateWeight("list", mPrimaryContainer, listWeight);
      } else {
        mPrimaryContainer.setLayoutParams(
            new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, listWeight));
        mSecondaryContainer.setLayoutParams(
            new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, detailWeight));
      }
    }
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

  @Override
  public void onBackPressed() {
    if (mDetailVisible) {
      hideDetailFragment();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    if (mDetailVisible) {
      hideDetailFragment();
      return true;
    } else {
      return super.onSupportNavigateUp();
    }
  }

  @Override
  public void onBackStackChanged() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
  }
}
