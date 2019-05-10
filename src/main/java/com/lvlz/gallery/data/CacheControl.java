package com.lvlz.gallery.data;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.lvlz.gallery.debugger.DebugControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.io.FileUtils;

public class CacheControl {

  private static final String CACHE_DIR = ".cache" + File.pathSeparator;

  private static final String CACHE_PREFIX = "data-";
  private static final String CACHE_SUFFIX = ".lyz";

  private static final String CACHE_TIME = "time.lyz";

  private static final long CACHE_EXPIRE = 12 * 60 * 1000;

  private File mCacheBase;
  private File mStoreFile;

  private Kryo mKryo;

  public CacheControl() {

    this(System.getProperty("user.dir") + File.pathSeparator + CACHE_DIR);

  }

  public CacheControl(String path) {

    mCacheBase = new File(path);

    mKryo = new Kryo();

    if (!mCacheBase.exists() || !mCacheBase.isDirectory()) {

      mCacheBase.mkdirs();

    }

    mKryo.register(CacheImpl.class);

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

  public void cache(long pointer, CacheImpl obj) {

    RandomAccessFile raf = null;
    Output output = null;

    try {

      if (expired()) {

        mStoreFile = new File(mCacheBase, getName(pointer));

        FileUtils.cleanDirectory(mCacheBase);

        FileUtils.writeStringToFile(new File(mCacheBase, CACHE_TIME), System.currentTimeMillis() + "");

      }
      
      //obj.encodeCache(mStoreFile);
      
      raf = new RandomAccessFile(mStoreFile, "rw");
      output = new Output(new FileOutputStream(raf.getFD()));
      mKryo.writeObject(output, obj);
      output.close();

    }
    catch (IOException e) {

      //if (raf != null) raf.close();

      if (output != null) output.close();

      DebugControl.process(e);

    }

  }

  public <T extends CacheImpl> T load(long pointer, Class<T> clazz) {

    File mStoreFile = null;
    Input input = null;
    RandomAccessFile raf = null;
    T returnedObject = null;

    if (contains(pointer)) {

      mStoreFile = new File(mCacheBase, getName(pointer));
      
      try {
        
        if (!mStoreFile.exists() || !mStoreFile.isFile()) {
        
          throw new IOException(mStoreFile.getAbsolutePath() + " not exists!");

        }
        else {

          raf = new RandomAccessFile(mStoreFile, "rw");

          input = new Input(new FileInputStream(raf.getFD()));

          returnedObject = mKryo.readObject(input, clazz);

          input.close();

        }

      }
      catch (IOException e) {

        //if (raf != null) raf.close();

        if (input != null) input.close();

        mStoreFile = null;

        DebugControl.process(e);

      }

      return returnedObject;

    }
    else {

      return null;

    }

  }

}
