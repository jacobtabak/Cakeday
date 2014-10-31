package me.tabak.nerdery.ui.recycler.anim;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.Collection;
import java.util.Iterator;

import rx.Observable;
import rx.Subscriber;

public class AcceleratingObservable<T> implements Observable.OnSubscribe<T> {
  private final Interpolator mInterpolator = new AccelerateInterpolator(2);
  private final Collection<T> mCollection;
  private final long mVariance;
  private final long mInterval;

  public AcceleratingObservable(Collection<T> collection, long interval, long variance) {
    mCollection = collection;
    mInterval = interval;
    mVariance = variance;
  }

  @Override
  public void call(Subscriber<? super T> subscriber) {
    Iterator<T> iterator = mCollection.iterator();
    for (int i = 0; i < mCollection.size(); i++) {
      float input = (float)i / mCollection.size();
      float interpolation = mInterpolator.getInterpolation(input);
      float variance = mVariance - interpolation * mVariance;
      long sleep = (long) (mInterval - variance);
      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      subscriber.onNext(iterator.next());
    }
  }
}
