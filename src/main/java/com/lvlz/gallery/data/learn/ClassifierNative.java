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

    Mat image = readImage(imagePath);

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

      yProd = recognize(yProd);

      rectangle(image, face.face, new Scalar(255, 0, 0, 0), 2, LINE_8, 0);
      
      //rectangle(image, new Point(face.face.x() - 10, face.face.y() - 10 - labelSize.height()), new Point(face.face.x() + labelSize.width(), face.face.y() + baseLine[0]), new Scalar(0, 255, 0, 0), FILLED, LINE_8, 0);
      
      textPosition = new Point(face.face.x() + face.face.width() + 10, face.face.y());
      for (int i = 0; i < subjects.length; i++) {
        
        label = subjects[i] + " : " + yProd.data().getFloat((long) i);
        labelSize = getTextSize(label, FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);
      
        putText(image, label, textPosition, FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 255, 0));

	    textPosition = new Point(textPosition.x(), textPosition.y() + labelSize.height() + 10);

      }

    }

    return image;

  }

  private Mat recognize(Mat yProd) {

    Mat cors;
    Mat e, e1;
    Mat wei,wei2;

    cors = new Mat(yProd.cols(), yProd.rows(), CV_64F);
    matchTemplate(yProd, yProd, cors, CV_TM_CCOEFF_NORMED);

    e = new Mat(sumElems(cors));
    exp(e, e);
    e1 = new Mat(sumElems(e));
    wei = divide(e, e1).asMat();
    //System.out.println("Dims wei : " + wei.dims() + "\n\n");
    //gemm(yProd, wei.reshape(0, 1), 1, new Mat(), 0, yProd);
    //wei = wei.reshape(0, new int[] {1, 1});
    System.out.println("wei cols: " + wei.size().height());
    wei2 = new Mat(new int[] {1,1}, CV_64F);
    wei.copyTo(wei2);
    //wei.convertTo(wei, CV_64F, 255.0/65536.0, 0);
    System.out.println("wei2 type == CV_64F ? " + (wei2.type() == CV_64F ? "true" : "false"));
    yProd = yProd.mul(wei2).asMat();

    return yProd;

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
long currtime = System.currentTimeMillis();
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

    saveImage(currtime + "-im.jpeg", im);
    saveImage(currtime + "-im2.jpeg", im2);
    saveImage(currtime + "-im3.jpeg", im3);
    saveImage(currtime + "-im4.jpeg", im4);

    MatVector batches = new MatVector(new Mat(new int[] {4, 224, 224, 3}, CV_32F, Scalar.all(0)).ptr());

    /*batches.put(0, im);
    batches.put(1, im2);
    batches.put(2, im3);
    batches.put(3, im4);*/

    batches.put(im, im2, im3, im4);

    return blobFromImages(batches, 1.0, new Size(224, 224), new Scalar(93.594, 104.7624, 129.1863, 0), false, false, CV_32F);

  }

  /*
  private Mat cropImage(Mat image, Rect, rectCrop) {

    Mat croppedImage = new Mat(image, rectCrop);

    return croppedImage;

  }
  */

  /*
  private findFaceAndClassify(String filename) {

    Mat image = readImage(filename);

    List<FaceRect> faces = detectFace(image);

    String[] subjects = new String[] {"soul", "jiae", "jisoo", "mijoo", "kei", "jin", "sujeong", "yein"};

    Mat out = new Mat();
    Mat im, batches, yProd, cors, e, wei;

    image.copyTo(out);

    for (FaceRect face : faces) {

    	im = new Mat(out, face.face);
    	resize(im, im, new Size(224, 224));

	batches = imAugmentAndBatch(im);

	this.model.setInput(batches);
	yProd = this.model.forward();

	cors = new Mat(yprod);
	matchTemplate(yProd, yProd, cors, CV_TM_CCOEFF_NORMED);
	cors = Mat.eye(Mat.zeros(cors.rows(), cors.cols(), CV_32F));
    }

  }
  */
}
