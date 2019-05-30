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
import org.bytedeco.javacpp.indexer.DoubleRawIndexer;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_dnn.*;
import org.bytedeco.opencv.opencv_imgproc.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_dnn.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import static org.bytedeco.opencv.global.opencv_highgui.*;

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

    Mat im, batches, yProd;

    //DoubleRawIndexer  indexer;
    float[] rates;

    Mat image = readImage(imagePath);

    //image.convertTo(image, CV_32F, 255, 255);

    List<FaceRect> faces = detectFace(image);

    String[] subjects = new String[] {"Soul", "Jiae", "Jisoo", "Mijoo", "Kei", "Jin", "Sujeong", "Yein"};

    String label;
    Size labelSize;
   
    Point textPosition; 
    int[] baseLine = new int[1];

    for (FaceRect face : faces) {

      im = new Mat(image, face.face);
      resize(im, im, new Size(224, 224));

      batches = imAugmentAndBatch(im);

      this.model.setInput(batches);
      yProd = this.model.forward();

      rates = new float[yProd.cols()];

      //double[] classProb = new double[subjects.length];
      printNet(yProd, subjects);

      rateFace(yProd, rates);
      //yProd = recognize(yProd, yProd);
      //indexer = yProd.createIndexer();

      rectangle(image, face.face, new Scalar(255, 0, 0, 0), 2, LINE_8, 0);
      
      //rectangle(image, new Point(face.face.x() - 10, face.face.y() - 10 - labelSize.height()), new Point(face.face.x() + labelSize.width(), face.face.y() + baseLine[0]), new Scalar(0, 255, 0, 0), FILLED, LINE_8, 0);
      
      textPosition = new Point(face.face.x() + face.face.width() + 10, face.face.y());
      for (int i = 0; i < subjects.length; i++) {
        label = subjects[i] + " : " + rates[i];
        labelSize = getTextSize(label, FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);
      
        putText(image, label, textPosition, FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 255, 0));

	    textPosition = new Point(textPosition.x(), textPosition.y() + labelSize.height() + 10);

      }

    }

    return image;

  }

  private void rateFace(Mat yProd, float[] result) {

      FloatIndexer indexer = yProd.createIndexer();

      float temp;

      for (int j = 0; j < yProd.cols(); j++) {

          temp = 0f;

          for (int i = 0; i < yProd.rows(); i++) {

              if (temp == 0f) { 
               
                  temp = indexer.get(i, j);

              }
              else {

                  temp = Math.max(temp, indexer.get(i, j));

              }

          }

          result[j] = temp;

      }


  }

  private void printNet(Mat yProd, String[] subjects) {

      echo("========================================");
      echo("==           Print Y_PROD             ==");
      echo("========================================");

      echo("= cols : " + yProd.cols() + "         ==");
      echo("= rows : " + yProd.rows() + "         ==");
      echo("========================================");

      FloatIndexer indexer = yProd.createIndexer();

      for (int j = 0; j < yProd.cols(); j++) {

          echo("member : " + subjects[j]);
          echo(" ");

          for (int i = 0; i < yProd.rows(); i++) {

              echo("  col : " + j);
              echo("  row : " + i);
              echo("  val : " + indexer.get(i, j));
              echo(" ");

          }

          echo("================================");

      }

  }

  private Mat recognize(Mat yProd, double[] classProb, String[] subjects) {

    //Mat cors = new Mat(yProd.cols(), yProd.rows(), CV_8U);
    //matchTemplate(yProd, yProd, cors, CV_TM_CCOEFF_NORMED);
    
    Point classId = new Point();
    //double[] classProb = new double[8];
    Mat probMat = yProd;//.reshape(1, new int[] {-1, 1});
    minMaxLoc(probMat, null, classProb, null, classId, null);

    //echo("probMat cols : " + probMat.cols());
    //echo("probMat rows : " + probMat.rows());
    //echo("Best class   : #" + classId.x() + " => " + subjects[classId.x()]);

    return probMat;
  }


  private Mat recognize(Mat src, Mat yProd) {

    Mat cors;
    Mat cors0;
    Mat e;
    Mat wei;

    cors = new Mat(yProd.cols(), yProd.rows(), CV_8U);
    matchTemplate(src, yProd, cors, CV_TM_CCOEFF);

    echoMat("cors", cors);

    cors0 = multiply(Mat.eye(cors.size(), CV_8U), 1).asMat();

    cors.setTo(new Mat(cors0.size(), cors0.type(), Scalar.all(0)), cors0);

    e = new Mat(cors.size(), CV_32F, sumElems(cors));
    exp(e, e);
    wei = divide(e, new Mat(e.size(), CV_32F, sumElems(e))).asMat();

    echoMat("e", e);
    echoMat("yProd", yProd);
    echoMat("wei", wei);
        

    //yProd = multiply(yProd, wei).asMat();
    //yProd = yProd.mul(wei.reshape(1, wei.cols() * wei.rows())).asMat();
    gemm(yProd, wei.reshape(1, wei.cols() * wei.rows()), 0, new Mat(), 0, yProd);
    yProd = new Mat(yProd.size(), CV_32F, sumElems(yProd));

    echoMat("yProd", yProd);
    echoMat("wei", wei);

    return yProd;

  }

  private void echoMat(String var, Mat mat) {

      echo("============================");
      echo(var + "  type = " + mat.type());
      echo(var + "  cols = " + mat.cols());
      echo(var + "  rows = " + mat.rows());
      echo("============================");

  }

  private void echo(String str) {

      System.out.println(str);

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

    if (max(width, height) > maxpix) {

      if (height > width) {

        shapeChange = maxpix / height;

      }
      else {

        shapeChange = maxpix / width;

      }

      newWidth = width * shapeChange;
      newHeight = height * shapeChange;

      // Weird behaviour
      // Work on local, but not on heroku...
      //resize(image, image, new Size(newWidth, newHeight), (double) (newWidth / width), (double) (newHeight / height), INTER_LINEAR);

    }

    copyMakeBorder(image, image, 30, 30, 30, 30, BORDER_CONSTANT, new Scalar(0));

    return image;

  }

  private List<FaceRect> detectFace(Mat image) {

    Mat imageCopy = image.clone();

    Mat blob;
    Mat detections;
    Mat ne;

    FloatIndexer srcIndexer;

    float confidence, f1, f2, f3, f4, tx, ty, bx, by;

    List<FaceRect> faces = new ArrayList<FaceRect>();

    FaceRect face;
    
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

        face = new FaceRect(confidence, (new Rect((int) tx, (int) ty, (int) (bx - tx), (int) (by - ty))));

        faces.add(face);

      }

    }

    return faces;

  }

  private void saveImage(String name, Mat mat) {

      String path = "/home/mr/Projects/heroku-app/java/lvlz-gallery/mods/";

      imwrite(path + name, mat);

  }

  private Mat imAugmentAndBatch(Mat image) {
    
    Mat im = new Mat(image.cols(), image.rows(), CV_32F);
    image.copyTo(im);

    Mat m = getRotationMatrix2D(new Point2f(image.cols() / 2, image.rows() / 2), 10, 1); 
    Mat mM = getRotationMatrix2D(new Point2f(image.cols() / 2, image.rows() / 2), -10, 1);

    Mat im2 = new Mat();
    Mat im3 = new Mat();
    Mat im4 = new Mat();

    warpAffine(im, im2, m, im.size());
    warpAffine(im, im3, mM, im.size());
    flip(im, im4, 1);

    /*
    im = im.reshape(0, im.dims()+1, im.size());
    im2 = im2.reshape(0, im2.dims()+1, im2.size());
    im3 = im3.reshape(0, im3.dims()+1, im3.size());
    im4 = im4.reshape(0, im4.dims()+1, im4.size());
    */

    MatVector batches = new MatVector(new Mat(new int[] {4, 224, 224, 3}, CV_32F, Scalar.all(0)).ptr());

    /*batches.put(0, im);
    batches.put(1, im2);
    batches.put(2, im3);
    batches.put(3, im4);*/

    batches.put(im, im2, im3, im4);

    return blobFromImages(batches, 1.0, new Size(224, 224), new Scalar(93.594, 104.7624, 129.1863, 0), false, false, CV_32F);

  }

}
