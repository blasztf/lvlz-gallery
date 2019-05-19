package com.lvlz.gallery.assets;

import java.io.File;

public class Asset {

  private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "assets";

  public static String getPath(String name) {

    return ROOT_PATH + File.separator + name;

  }

  public static String getPath(Class clazz, String name) {

    return ROOT_PATH + clazz.getPackage().getName().replaceAll("\\.", File.separator) + name;

  }

  public static String getPath(Object obj, String name) {

    return getPath(obj.getClass(), name);

  }

}
