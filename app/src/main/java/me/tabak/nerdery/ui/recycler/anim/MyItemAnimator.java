package me.tabak.nerdery.ui.recycler.anim;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class MyItemAnimator extends DefaultItemAnimator {
  private final Interpolator mInterpolator = new AccelerateInterpolator();
  private final LinearLayoutManager mLayoutManager;

  public MyItemAnimator(LinearLayoutManager layoutManager) {
    mLayoutManager = layoutManager;
  }

  public boolean animateRemove(RecyclerView.ViewHolder holder) {
    final View view = holder.itemView;
    int width = getWidth(holder);
    ViewCompat.animate(view).cancel();
    float interpolation = mInterpolator.getInterpolation((float) view.getTop() / mLayoutManager.getHeight());

    ViewCompat.animate(view)
        .setStartDelay((long) (500 * interpolation))
        .translationX(width)
        .alpha(0)
        .setInterpolator(new DecelerateInterpolator());
    return true;
  }

  @Override
  public boolean animateAdd(RecyclerView.ViewHolder holder) {
    final View view = holder.itemView;
    int width = getWidth(holder);
    ViewCompat.animate(view).cancel();
    ViewCompat.setTranslationX(holder.itemView, (float) (-width * .5));
    ViewCompat.setScaleX(view, 0f);
    ViewCompat.setScaleY(view, 0f);
    ViewCompat.setAlpha(view, 0f);
    float interpolation = mInterpolator.getInterpolation((float) view.getTop() / mLayoutManager.getHeight());
    long startDelay = (long) (500 * interpolation);
    ViewCompat.animate(view)
        .setStartDelay(Math.max(0, startDelay))
        .translationX(0)
        .scaleX(1)
        .scaleY(1)
        .alpha(1)
        .setInterpolator(new AccelerateInterpolator());
    return true;
  }

  public int getWidth(RecyclerView.ViewHolder holder) {
    return getWidth(holder.itemView);
  }

  public int getWidth(View itemView) {
    return itemView.getMeasuredWidth() + itemView.getPaddingRight() + ((RecyclerView.LayoutParams) itemView.getLayoutParams()).rightMargin;
  }
}
