package me.tabak.nerdery;

import me.tabak.nerdery.MyApplication;
import me.tabak.nerdery.modules.MyModule;

public final class Modules {
  static Object[] list(MyApplication app) {
    return new Object[]{
        new MyModule(app)
    };
  }

  private Modules() {
    // No instances.
  }
}
