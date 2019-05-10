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

    TagList<String> aliases = TagList.get(member);

    if (member == null) {   

      result = mDataResult;

    }
    else {

      for (int i = 0; i < mDataResult.dataCollections.size(); i++) {

        boolean pass = false;

        for (String alias : aliases) {

          if (member.contains(alias)) {

            pass = true;
            
            break;

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
