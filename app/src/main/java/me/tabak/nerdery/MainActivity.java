package me.tabak.nerdery;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.tabak.nerdery.ui.fragments.MyWebViewFragment;

public class MainActivity extends ActionBarActivity {
  @InjectView(R.id.toolbar) Toolbar mToolbar;
  @InjectView(R.id.container) ViewGroup mContainer;

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
}
