package com.lvlz.gallery.model;

import java.util.ArrayList;

public class Gallery {

  private final long nextPointer;
  private final String[] images;

  public Gallery(long nextPointer, String[] images) {

    this.nextPointer = nextPointer;
    this.images = images;

  }

  public long getNextPointer() {

    return nextPointer;

  }

  public String[] getImages() {

    //String[] listImage = images.toArray(new String[images.size()]);

    return images; //listImage;

  }

}
