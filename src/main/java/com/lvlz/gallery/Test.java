package com.lvlz.gallery;

import com.lvlz.gallery.data.learn.ClassifierNative;

//import org.bytedeco.javacv.CanvasFrame;
//import org.bytedeco.javacv.OpenCVFrameConverter;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;

public class Test {

  public Test(String[] args) {

   // OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
   String windowId = "Lovelyz Face Recognition";
   String imPath = args[0];
   
   Mat image = ClassifierNative.load().detectAndDraw(imPath);

   namedWindow(windowId, CV_WINDOW_AUTOSIZE);

   imshow(windowId, image);

   waitKey(0);

    //CanvasFrame frame = new CanvasFrame("Face Detection", CanvasFrame.getDefaultGamma() / 2.2);

    //frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    //frame.setCanvasSize(600,600);
    //frame.setLocationRelativeTo(null);
    //frame.setVisible(true);

    //frame.showImage(converter.convert(image));

  }

}
