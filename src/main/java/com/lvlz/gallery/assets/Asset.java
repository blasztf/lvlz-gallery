package com.lvlz.gallery.assets;

import java.lang.Class;

public class Asset {

  private static Asset mInstance;

  private Class mClass;

  private static void lazyLoad(Class clazz) {

    if (mInstance == null) {

      mInstance = new Asset();

    }

    if (clazz != null) {

      mInstance.mClass = clazz;

    }
    else {

      mInstance.mClass = mInstance.getClass();

    }

  }

  public static Asset with(Class clazz) {

    lazyLoad(clazz);

    return mInstance;

  }

  public static Asset with(Object obj) {

    lazyLoad(obj.getClass());

    return mInstance;

  }

  public static String getPath(String path) {

    lazyLoad(null);

    return mInstance.getAssetPath(path);

  }

  private Asset() {

  }

  private Asset(Class clazz) {

    mClass = clazz;
  
  }

  private final void clear() {

    mClass = null;

  }

  public String getAssetPath(String name) {

    String path =  mClass.getResource(name).toString();

    clear();

    return path;

  }

}
