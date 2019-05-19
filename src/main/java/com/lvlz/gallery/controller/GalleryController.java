package com.lvlz.gallery.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;

import org.bytedeco.opencv.opencv_core.Mat;

//import org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import com.lvlz.gallery.debugger.DebugControl;

import com.lvlz.gallery.data.DataRetriever;
import com.lvlz.gallery.data.filter.Filter;
import com.lvlz.gallery.data.DataResult;

import com.lvlz.gallery.data.learn.ClassifierNative;

import com.lvlz.gallery.model.Gallery;

import java.io.ByteArrayInputStream;
import javax.servlet.http.HttpServletResponse;

import org.bytedeco.javacpp.BytePointer;
import java.io.IOException;
import java.io.File;

@RestController
public class GalleryController {

  @RequestMapping("/")
  public String index() {

    return "Lovelyz, when you reply to my tweet in MenPa...?";

  }

  @RequestMapping(method=RequestMethod.GET, path="/member/{name}")
  public Gallery member(@PathVariable String name, @RequestParam(value="next_pointer", defaultValue="0") long nextPointer) {
    
    DataResult result = getResult(name, nextPointer);

    return new Gallery(result, "Name : " + name);

  }

  @RequestMapping(method=RequestMethod.GET, path="/member/{name}/learn/face")
  public void learn(HttpServletResponse response, @PathVariable String name, @RequestParam(value="next_pointer", defaultValue="0") long nextPointer) {

    DataResult result = getResult(name, nextPointer);

    int rndData = (int) (Math.random() * result.dataCollections.size());

    DataResult.Data data = result.dataCollections.get(rndData);

    int rndPhoto = (int) (Math.random() * data.photos.size());

    Mat image = ClassifierNative.load().detectAndDraw(data.photos.get(rndPhoto));

    //BytePointer pointer = new BytePointer();
    //byte[] buffer;

    File baseDir = new File(System.getProperty("user.dir") + File.separator + "results");

    if (!baseDir.exists() || !baseDir.isDirectory()) {

      baseDir.mkdirs();

    }

    String fileName = System.currentTimeMillis() + ".jpeg";

    try {

      imwrite(baseDir.getCanonicalPath() + File.separator + fileName, image);

      //imencode(".jpeg", image, pointer);

      //buffer = new byte[pointer.sizeof()];
      
      //pointer.get(buffer);

      response.setContentType(MediaType.IMAGE_JPEG_VALUE);
      
      IOUtils.copy(FileUtils.openInputStream(new File(baseDir, fileName)), response.getOutputStream());

    }
    catch (IOException e) {

      DebugControl.process(e);

    }

  }

  private DataResult getResult(String name, long nextPointer) {

    DataResult result = DataRetriever.with(nextPointer).retrieve();

    result = Filter.with(result).find(name);

    return result;

  }

}
