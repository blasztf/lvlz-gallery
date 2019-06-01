package com.lvlz.gallery.data.learn;

import org.bytedeco.opencv.opencv_core.*;

import org.bytedeco.javacpp.indexer.*;

import static org.bytedeco.opencv.global.opencv_core.*;

public class CvHelper {

	public static void print(String text) {

		System.out.print(text);

	}

	public static void println(String text) {

		System.out.println(text);

	}

	public static void fillDiagonal(Mat mat, double d) {

		mat.setTo(new Mat(Scalar.all(d)), Mat.eye(mat.size(), CV_8U).alpha(1).asMat());

	}

	public static void printRect(Rect r) {

		println("Rectangle");
		println(" x            => " + r.x());
		println(" y            => " + r.y());
		println(" width        => " + r.width());
		println(" height       => " + r.height());
		println(" area         => " + r.area());
		println(" top-left     => " + r.tl().y() + "," + r.tl().x());
		println(" bottom-right => " + r.br().y() + "," + r.br().x());
	}

	public static void printMat(Mat mat, String title) {

		Indexer indexer = mat.createIndexer();

	  String el;

	  println(title);

	  println("cols : " + mat.cols());
	  println("rows : " + mat.rows());
	  println("[");

	  for (int i = 0; i < mat.rows(); i++) {

      el = " [";
      for (int j = 0; j < mat.cols(); j++) {

        if (indexer instanceof DoubleIndexer) {
        	el += ((DoubleIndexer) indexer).get(i, j);
        }
        else if (indexer instanceof FloatIndexer) {
          el += ((FloatIndexer) indexer).get(i, j);
        }
        else if (indexer instanceof UByteIndexer) {
          el += ((UByteIndexer) indexer).get(i, j);
        }
          el += " ";
      }
      el += "] ";

      println(el);

		}

    println("]\n");

	}

}