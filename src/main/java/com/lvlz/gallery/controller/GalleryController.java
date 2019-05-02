package com.lvlz.gallery.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import com.lvlz.gallery.model.Gallery;

@RestController
public class GalleryController {

  @RequestMapping("/")
  public String index() {

    return "Selamat masuk ke index!";

  }

  @RequestMapping(method=RequestMethod.GET, path="/member/{name}")
  public Gallery member(@PathVariable String name) {
    
    return findImages(name);

  }

  public Gallery findImages(String name) {

    String[] images = findListImageByName(name);
    long nextPointer = 12345678L;

    return new Gallery(nextPointer, images);

  }

  private String[] findListImageByName(String name) {

    String[] data = new String[10];
    
    for (int i = 0; i < 10; i++) {

      data[i] = name + " " + i;

    }

    return data;

  }

}
