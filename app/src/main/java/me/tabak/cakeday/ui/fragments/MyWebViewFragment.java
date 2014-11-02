package me.tabak.cakeday.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.tabak.cakeday.R;
import timber.log.Timber;

public class MyWebViewFragment extends Fragment {
  public static final String EXTRA_URL = "url";
  private boolean mIsLoading;
  @InjectView(R.id.webview) WebView mWebView;
  @InjectView(R.id.webview_progressbar) ProgressBar mProgressBar;
  @InjectView(R.id.webview_back) ImageView mBackButton;
  @InjectView(R.id.webview_forward) ImageView mForwardButton;
  @InjectView(R.id.webview_refresh) ImageView mRefreshButton;
  @InjectView(R.id.toolbar) Toolbar mToolbar;

  public static MyWebViewFragment newInstance(String url) {
    MyWebViewFragment fragment = new MyWebViewFragment();
    Bundle args = new Bundle();
    args.putString(EXTRA_URL, url);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_webview, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mToolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
    if (Build.VERSION.SDK_INT >= 11) {
      mWebView.getSettings().setDisplayZoomControls(false);
    }
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.getSettings().setBuiltInZoomControls(true);
    mWebView.getSettings().setUseWideViewPort(true);
    mWebView.getSettings().setDomStorageEnabled(true);
    mWebView.setInitialScale(1);
    mWebView.setWebViewClient(new CustomWebViewClient());
    mWebView.setWebChromeClient(new WebChromeClient() {
      public boolean onConsoleMessage(ConsoleMessage cm) {
        Timber.d("Webview Console", cm.message() + " -- From line "
            + cm.lineNumber() + " of "
            + cm.sourceId());
        return true;
      }
    });
    prepareSplitActionBar();
  }

  public void prepareSplitActionBar() {
    mBackButton.setEnabled(mWebView.canGoBack());
    mForwardButton.setEnabled(mWebView.canGoForward());
    mRefreshButton.setImageResource(mIsLoading ? R.drawable.ic_action_cancel : R.drawable.ic_action_reload);
  }

  @OnClick(R.id.webview_back)
  void onBackClicked() {
    mWebView.goBack();
    prepareSplitActionBar();
  }

  @OnClick(R.id.webview_forward)
  void onForwardClicked() {
    mWebView.goForward();
    prepareSplitActionBar();
  }

  @OnClick(R.id.webview_refresh)
  void onRefreshClicked() {
    if (mIsLoading) {
      mWebView.stopLoading();
    } else {
      mWebView.reload();
    }
  }

  @OnClick(R.id.webview_browser)
  void onBrowserClicked() {
    try {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mWebView.getUrl())));
    } catch (ActivityNotFoundException e) {
      Timber.e(e, "Unable to open browser");
      Toast.makeText(getActivity(), "Unable to open browser", Toast.LENGTH_SHORT).show();
    }
  }

  private class CustomWebViewClient extends WebViewClient {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      Timber.d("Loading webview: " + url);
      mIsLoading = true;
      if (url.contains("wine.com") && url.contains("&") && !url.contains("?")) {
        int pos = url.indexOf("&");
        url = url.substring(0, pos) + "?" + url.substring(pos + 1);
        view.loadUrl(url);
      }

      prepareSplitActionBar();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      super.onReceivedError(view, errorCode, description, failingUrl);
      Timber.e("Webview Error: " + description);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
      Timber.e("Webview SSL Error: " + error.toString());
      handler.proceed();
    }

    @Override
    public void onPageFinished(WebView view, final String url) {
      mProgressBar.setVisibility(View.GONE);
      mIsLoading = false;
      prepareSplitActionBar();
    }
  }

  @Override
  public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
    //Check if the superclass already created the animation
    Animation animation = super.onCreateAnimation(transit, enter, nextAnim);

    //If not, and an animation is defined, load it now
    if (animation == null && nextAnim != 0) {
      animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
    }

    if (animation != null) {
      animation.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
          // Do nothing.
        }

        @Override
        public void onAnimationEnd(Animation animation) {
          animation.setAnimationListener(null);
          onAnimationComplete(enter);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
          // Do nothing.
        }
      });
    } else {
      onAnimationComplete(enter);
    }
    return animation;
  }

  @Override
  public void onPause() {
    super.onPause();
    if (Build.VERSION.SDK_INT >= 11) {
      // Prevents glitchy animation on the webview as it's leaving.
      mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
  }

  private void onAnimationComplete(boolean enter) {
    if (enter) {
      mWebView.loadUrl(getArguments().getString(EXTRA_URL));
    }
  }
}
