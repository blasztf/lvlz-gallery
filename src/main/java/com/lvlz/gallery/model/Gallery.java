package com.lvlz.gallery.model;

import com.lvlz.gallery.data.DataResult;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Gallery {

  private final List<DataResult.Data> data;
  private final long next_pointer;
  private final int code_err;
  public String log;

  public Gallery(DataResult result, String log) {

    this.data = result.dataCollections;
    this.next_pointer = result.nextPointer;
    this.code_err = result.codeErr;
    this.log = log;

  }

  public long getNextPointer() {

    return next_pointer;

  }

  public List<DataResult.Data> getData() {

    return data;

  }

  public int getCodeErr() {

    return code_err;

  }

}
