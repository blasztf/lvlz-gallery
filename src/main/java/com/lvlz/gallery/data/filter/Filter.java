package com.lvlz.gallery.data.filter;

import com.lvlz.gallery.data.DataResult;

public class Filter {

  public static Filter mInstance;

  private DataResult mDataResult;

  private static void lazyLoad() {

    if (mInstance == null) {

      mInstance = new Filter();

    }

  }

  public static Filter with(DataResult dataResult) {

    lazyLoad();

    mInstance.mDataResult = dataResult;

    return mInstance;

  }

  /**
   * Proto : guessUser
   * Desc  : Determine user based on their tweet.
   ***/

  //public DataResult guessUser() {
  //
  //}

  public DataResult find(String member) {

    DataResult result;
    boolean pass;
    Data data;

    TagList<String> aliases = TagList.get(member);

    if (member == null || member.toLowerCase().equals("lovelyz")) {   

      result = mDataResult;

    }
    else {

      for (int i = 0; i < mDataResult.dataCollections.size(); i++) {

        pass = false;
        data = mDataResult.dataCollections.get(i);

        for (String tag : data.tags) {

          for (String alias : aliases) {

            if (tag.contains(alias)) {

              pass = true;
            
              break 2;

            }

          }
        
        }

        if (!pass) {

          mDataResult.dataCollections.remove(i);

          --i;

        }

      }

      result =  mDataResult;

    }

    mDataResult = null;

    return result;

  }
    
}  
