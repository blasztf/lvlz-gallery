package com.lvlz.gallery.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.lvlz.gallery.data.DataRetriever;
import com.lvlz.gallery.data.filter.Filter;
import com.lvlz.gallery.data.DataResult;
import com.lvlz.gallery.model.Gallery;

@RestController
public class GalleryController {

  @RequestMapping("/")
  public String index() {

    return "Selamat masuk ke index!";

  }

  @RequestMapping(method=RequestMethod.GET, path="/member/{name}")
  public DataResult member(@PathVariable String name, @RequestParam(value="next_pointer", defaultValue="0") long nextPointer) {
    
    DataResult result = DataReteriever.with(nextPointer).retrieve();

    result = Filter.with(result).find(name);

    return result

  }

}
