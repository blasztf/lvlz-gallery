package com.lvlz.gallery.main;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CacheControl {

  private static final String CACHE_DIR = ".cache" + File.pathSeparator;

  private static final String CACHE_PREFIX = "data-";
  private static final String CACHE_SUFFIX = ".lyz";

  private static final String CACHE_TIME = "time.lyz";

  private static final long CACHE_EXPIRE = 12 * 60 * 1000;

  private File mCacheBase;
  private File mStoreFile;

  public CacheControl() {

    this(System.getProperty("user.dir") + File.pathSeparator + CACHE_DIR);

  }

  public CacheControl(String path) {

    mCacheBase = new File(path);

    if (!mCacheBase.exists() || !mCacheBase.isDirectory()) {

      mCacheBase.mkdirs();

    }

  }

  public static interface CacheImpl {

    public String encodeCache();

    public <T extends CacheImpl> T decodeCache(String cache);

  }

  private String getName(long pointer) {

    return CACHE_PREFIX + pointer + CACHE_SUFFIX;

  }

  public boolean contains(long pointer) {
    
    boolean result = false;

    try {
       
      result = FileUtils.directoryContains(mCacheBase, new File(getName(pointer)));

    }
    catch (IOException e) {

      result = false;

      DebugControl.process(e); 

    }
    

    return result;

  }

  public boolean expired() {

    long currTime = System.currentTimeMillis();
    long lastTime = 0;

    File mStoreFile = new File(mCacheBase.getAbsolutePath(), CACHE_TIME);

    if (mStoreFile.exists() && mStoreFile.isFile()) {

      try {

        lastTime = Long.parseLong(FileUtils.readFileToString(mStoreFile, "UTF-8"));

      }
      catch (IOException e) {

        lastTime = currTime;

        DebugControl.process(e);

      }

      if ((currTime - lastTime) < CACHE_EXPIRE) {

        return false;

      }

    }

    return true;

  }

  public void cache(long pointer, CacheControl.CacheImpl obj) {

    try {

      if (expired()) {

        FileUtils.cleanDirectory(mCacheBase);

        FileUtils.writeStringToFile(new File(mCacheBase, CACHE_TIME), System.currentTimeMillis() + "");

      }
      
      FileUtils.writeStringToFile(new File(mCacheBase, getName(pointer)), obj.encodeCache());

    }
    catch (IOException e) {

      DebugControl.process(e);

    }

  }

  public <T extends CacheControl.CacheImpl> T load(long pointer, T obj) {

    if (contains(pointer)) {

      File mStoreFile = new File(mCacheBase, getName(pointer));

      String cache = null;
      
      try {
        
        cache = FileUtils.readFileToString(mStoreFile, "UTF-8");

      }
      catch (IOException e) {

        cache = null;

        DebugControl.process(e);

      }

      return cache == null ? cache : obj.decodeCache(cache);

    }
    else {

      return null;

    }

  }

}
