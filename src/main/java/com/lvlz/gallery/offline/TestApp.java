package com.lvlz.gallery.offline;

import java.io.File;

import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;

import com.lvlz.gallery.data.learn.ClassifierNative;

public class TestApp extends Application {

  public TestApp(String[] args) {

    launch(args);

  }

  public static void testImage(String imPath, String detectorText) {

    int detector = (detectorText.equals("haar") ? ClassifierNative.DETECTOR_HAAR : ClassifierNative.DETECTOR_DNN);

    String windowId = "Lovelyz Face Recognition - " + imPath;
    Mat image = ClassifierNative.load(detector).detectAndDraw(imPath);

    namedWindow(windowId, CV_WINDOW_AUTOSIZE);

    imshow(windowId, image);

    waitKey(0);

    destroyAllWindows();

  }

  @Override
  public void start(Stage primaryStage) {

    primaryStage.setTitle("Lovelyz Classifier");
    Button btn = new Button();
    btn.setText("Open image...");
    btn.setOnAction(new EventHandler<ActionEvent>() {

      FileChooser fileChooser;

      @Override
      public void handle(ActionEvent event) {

        fileChooser = new FileChooser();
        //fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null && selectedFile.isFile()) {

          testImage(selectedFile.getAbsolutePath(), "");

        }

      }

    });

    StackPane root = new StackPane();
    root.getChildren().add(btn);
    primaryStage.setScene(new Scene(root, 300, 300));
    primaryStage.show();

  }

}
