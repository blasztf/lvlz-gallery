package com.lvlz.gallery.data;

public class DataSource {

  public static final DataSource GLOBAL = new DataSource("Lovelyz_Global");

  public static final DataSource INDONESIA = new DataSource("LovelinusINA");

  public static final DataSource SINGAPORE = new DataSource("SGLovelyz");

  private static final String PRIMARY = "https://twitter.com/{name}";

  private static final String SECONDARY = "https://twitter.com/i/profiles/show/{name}/timeline/tweets?include_available_features=1&include_entities=1&max_position={max_position}&reset_error_state=false"; 

  public static final int TYPE_PRIMARY = 1;

  public static final int TYPE_SECONDARY = 2;

  public String url;

  public int type;

  private String mName;

  private DataSource() {}

  private DataSource(String name) {

    mName = name;

    setPointer(0);

  }

  public DataSource setPointer(long pointer) {

    if (pointer == 0) {

      url = PRIMARY.replace("{name}", mName.toLowerCase());

      type = TYPE_PRIMARY;

    }
    else {

      url = SECONDARY.replace("{name}", mName).replace("{max_position}", pointer + "");

      type = TYPE_SECONDARY;

    }

    return this;

  }

}  
