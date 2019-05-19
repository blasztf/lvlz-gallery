package com.lvlz.gallery;

import com.lvlz.gallery.data.learn.ClassifierNative;

//import org.bytedeco.javacv.CanvasFrame;
//import org.bytedeco.javacv.OpenCVFrameConverter;

import org.bytedeco.opencv.opencv_core.*;

public class Test {

  public Test(String[] args) {

   // OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    Mat image = new ClassifierNative().detectAndDraw(args[0]);

    //CanvasFrame frame = new CanvasFrame("Face Detection", CanvasFrame.getDefaultGamma() / 2.2);

    //frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    //frame.setCanvasSize(600,600);
    //frame.setLocationRelativeTo(null);
    //frame.setVisible(true);

    //frame.showImage(converter.convert(image));

  }

}
