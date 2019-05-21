package com.lvlz.gallery.data.learn;

import org.bytedeco.opencv.opencv_core.Rect;

public class FaceRect {

  public float confidence;

  public Rect face;

  public FaceRect(float confidence, Rect face) {

    this.confidence = confidence;
    this.face = face;

  }

}
