package com.lvlz.gallery.data.learn;

import com.lvlz.gallery.assets.Asset;
import com.lvlz.gallery.debugger.DebugControl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.bytedeco.javacpp.indexer.FloatIndexer;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.*;
import org.bytedeco.opencv.opencv_imgproc.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_dnn.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class ClassifierNative {

  private Net faceDetector;
  private Net model;

  private static ClassifierNative mInstance;

  private ClassifierNative() {

    this.faceDetector = readNetFromCaffe(Asset.getPath("deploy.prototxt.txt"), Asset.getPath("res10_300x300_ssd_iter_140000.caffemodel"));

    this.model = readNetFromTensorflow(Asset.getPath("model.pb"), Asset.getPath("model.pbtxt"));

  }

  public static ClassifierNative load() {

    if (mInstance == null) {

      mInstance = new ClassifierNative();

    }

    return mInstance;

  }

  public Mat detectAndDraw(String imagePath) {

    Mat image = readImage(imagePath);

    List<Rect> faces = detectFace(image);

    for (Rect face : faces) {

      rectangle(image, face, new Scalar(255, 0, 0, 0), 2, LINE_8, 0);

    }

    return image;

  }

  private int max(int... number) {

    int result = number[0];

    for (int i = 1; i < number.length; i++) {

      result = Math.max(number[i], result);

    }

    return result;

  }

  private Mat readImage(String imagePath) {

    int width, height, newWidth, newHeight;

    int shapeChange;
    byte[] imBytes;
    Mat image;

    int maxpix = 1024;

    try {
   
      if (imagePath.contains("http") || imagePath.contains("https")) {

        imBytes = IOUtils.toByteArray(new URL(imagePath));

      }
      else {
      
        imBytes = FileUtils.readFileToByteArray(new File(imagePath));

      }

    }
    catch (IOException e) {

      DebugControl.process(e);

      return null;

    }

    image = imdecode(new Mat(imBytes), IMREAD_UNCHANGED);
    
    if (image.type() == CV_16U) {

      convertScaleAbs(image, image, (255.0/65535.0), 0);

    }
    
    if (image.size(3) == 4) {

      cvtColor(image, image, COLOR_BGRA2BGR);

    }

    width = image.cols();
    height = image.rows();

    if (max(width, height)) > maxpix) {

      if (height > width) {

        shapeChange = maxpix / height;

      }
      else {

        shapeChange = maxpix / width;

      }

      newWidth = width * shapeChange;
      newHeight = height * shapeChange;

      resize(image, image, new Size(newWidth, newHeight), (double) (newWidth / width), (double) (newHeight / height), INTER_LINEAR);

    }

    copyMakeBorder(image, image, 30, 30, 30, 30, BORDER_CONSTANT, new Scalar(0));

    return image;

  }

  private List<Rect> detectFace(Mat image) {

    Mat imageCopy = image.clone();

    Mat blob;
    Mat detections;
    Mat ne;

    FloatIndexer srcIndexer;

    float confidence, f1, f2, f3, f4, tx, ty, bx, by;

    List<Rect> faces = new ArrayList<Rect>();
    
    int h = image.rows();
    int w = image.cols();

    resize(imageCopy, imageCopy, new Size(300, 300)); 
    blob = blobFromImage(imageCopy, 1.0, new Size(300, 300), new Scalar(104.0, 177.0, 123.0, 0), false, false, CV_32F);

    this.faceDetector.setInput(blob);
    detections = this.faceDetector.forward();

    ne = new Mat(new Size(detections.size(3), detections.size(2)), CV_32F, detections.ptr(0, 0));

    srcIndexer = ne.createIndexer();

    for (int i = 0; i < detections.size(3); i++) {

      confidence = srcIndexer.get(i, 2);
      f1 = srcIndexer.get(i, 3);
      f2 = srcIndexer.get(i, 4);
      f3 = srcIndexer.get(i, 5);
      f4 = srcIndexer.get(i, 6);

      if (confidence > .5) {

        tx = f1 * w;
        ty = f2 * h;
        bx = f3 * w;
        by = f4 * h;

        faces.add(new Rect((int) tx, (int) ty, (int) (bx - tx), (int) (by - ty)));

      }

    }

    return faces;

  }

  /*
  private findFaceAndClassify(String filename) {

    Mat image = readImage(filename);

    List<Rect> faces = detectFace(image);

    String[] subjects = new String[] {"soul", "jiae", "jisoo", "mijoo", "kei", "jin", "sujeong", "yein"};

    Mat out = new Mat();

    image.copyTo(out);

    for (Rect face : faces) {

    }

  }
  */
}
