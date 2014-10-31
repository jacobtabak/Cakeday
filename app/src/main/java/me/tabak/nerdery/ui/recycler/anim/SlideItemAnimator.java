package me.tabak.nerdery.ui.recycler.anim;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

public class SlideItemAnimator extends PendingItemAnimator {
  public SlideItemAnimator() {
    setAddDuration(500);
    setRemoveDuration(500);
    setMoveDuration(500);
  }

  @Override
  protected boolean prepHolderForAnimateRemove(RecyclerView.ViewHolder holder) {
    return true;
  }

  protected ViewPropertyAnimatorCompat animateRemoveImpl(RecyclerView.ViewHolder holder) {
    final View view = holder.itemView;
    ViewCompat.animate(view).cancel();
    return ViewCompat.animate(view)
        .translationX(DisplayUtils.getScreenDimensions(holder.itemView.getContext()).x)
        .setInterpolator(new AnticipateOvershootInterpolator());
  }

  @Override
  protected void onRemoveCanceled(RecyclerView.ViewHolder holder) {
    ViewCompat.setTranslationX(holder.itemView, 0);
  }

  @Override
  protected boolean prepHolderForAnimateAdd(RecyclerView.ViewHolder holder) {
    final View view = holder.itemView;
    int width = getWidth(holder);
    ViewCompat.setTranslationX(holder.itemView, (float) (-width * .5));
    ViewCompat.setScaleX(view, 0f);
    ViewCompat.setScaleY(view, 0f);
    ViewCompat.setAlpha(view, 0f);
    return true;
  }

  protected ViewPropertyAnimatorCompat animateAddImpl(RecyclerView.ViewHolder holder) {
    final View view = holder.itemView;
    ViewCompat.animate(view).cancel();
    return ViewCompat.animate(view)
        .translationX(0)
        .scaleX(1)
        .scaleY(1)
        .alpha(1)
        .setInterpolator(new AccelerateInterpolator());
  }

  @Override
  protected void onAddCanceled(RecyclerView.ViewHolder holder) {
    ViewCompat.setTranslationX(holder.itemView, 0);
  }

  public int getWidth(RecyclerView.ViewHolder holder) {
    return getWidth(holder.itemView);
  }

  public int getWidth(View itemView) {
    return itemView.getMeasuredWidth() + itemView.getPaddingRight() + ((RecyclerView.LayoutParams) itemView.getLayoutParams()).rightMargin;
  }
}