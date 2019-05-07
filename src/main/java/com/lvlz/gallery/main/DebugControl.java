package com.lvlz.gallery.main;

public class DebugControl {

  private static DebugControl mInstance;

  private final boolean enable = true;

  private DebugControl() {

  }

  private static void lazyLoad() {

    if (mInstance == null) {

      mInstance = new DebugControl();

    }

  }

  public static void process(Exception e) {

    DebugControl.lazyLoad();

    if (mInstance.enable) {

      e.printStackTrace();

    }

  }

}
