package me.tabak.cakeday.rx;

import rx.Observer;

public abstract class SimpleObserver<T> implements Observer<T> {
  @Override
  public void onCompleted() {

  }

  @Override
  public void onError(Throwable e) {

  }

  @Override
  public void onNext(T next) {

  }
}
