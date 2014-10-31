package me.tabak.nerdery.ui.recycler.anim;

import android.view.animation.Interpolator;

import java.util.List;

import rx.Observable;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;

public class InterpolatingMap<T> implements Func1<List<T>, Observable<T>> {
  private final Interpolator mInterpolator;
  private final long mStart;
  private final long mEnd;

  public InterpolatingMap(Interpolator interpolator, long start, long end) {
    mInterpolator = interpolator;
    mStart = start;
    mEnd = end;
  }

  @Override
  public Observable<T> call(List<T> list) {
    for (int i = 0; i < list.size(); i++) {
      float interpolation = mInterpolator.getInterpolation((float) i / list.size());
      long diff = mEnd - mStart;
      long delay = (long) (mStart + diff * interpolation);
      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        throw OnErrorThrowable.from(e);
      }
    }
    return null;
  }
}
