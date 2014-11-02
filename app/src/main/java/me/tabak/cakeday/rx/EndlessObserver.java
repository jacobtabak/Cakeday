package me.tabak.cakeday.rx;

import rx.Observer;

public abstract class EndlessObserver<T> implements Observer<T> {
  @Override
  public void onCompleted() {
    // do nothing
  }
}
