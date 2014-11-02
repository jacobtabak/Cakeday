package me.tabak.cakeday;

import me.tabak.cakeday.modules.MyModule;

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
