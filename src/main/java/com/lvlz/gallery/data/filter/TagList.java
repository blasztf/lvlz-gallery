package com.lvlz.gallery.data.filter;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.Override;

public class TagList<T> extends ArrayList<T> {

  private TagList<String> mList = new TagList<String>();

  public static TagList mInstance;

  private static void lazyLoad() {

    if (mInstance == null) {

      mInstance = new TagList();

      mInstance.generate();

    }

  }


  /**
   *
   * Always return object of TagList so it can be check by isEmpty().
   *
   **/
  public static TagList<String> get(String tag) {

    lazyLoad();

    tag = tag.toLowerCase();

    for (int i = 0; i < mInstance.mList.size(); i++) {

      for (int j = 0; j < mInstance.mList.get(i).size(); j++) {

        if (tag.equals(mInstance.mList.get(i).get(j))) {

          return mInstance.mList.get(i);
        
        }

      }
          
    }

    return new TagList<String>();

  }

  //@Override
  public TagList() {

    super();

  }

  //@Override
  public TagList(Collection<? extends T> c) {
    
    super(c);

  }

  private void generate() {

    TagList<String> iList;
    String[] pList;

    if (mList == null) {

      mList = new TagList<TagList<String>>();

    }
    else {

      mList.clear();

    }

    pList = new String[] {
      "babysoul",
	    "leesujeong",
	    "leesujung",
	    "베이비소울",
	    "소울",
	    "이수정"
    };

    iList = new TagList<String>(Arrays.asList(pList));

    mList.add(iList);

	  pList = new String[] {
		  "jiae",
		  "yoojiae",
		  "yujiae",
	  	"유지애",
	  	"지애"
  	};

    iList = new TagList<String>(Arrays.asList(pList));

    mList.add(iList);

	  pList = new String[] {
		  "jisoo",
		  "jisu",
		  "seojisoo",
		  "seojisu",
		  "서지수",
		  "지수"
  	};

    iList = new TagList<String>(Arrays.asList(pList));

    mList.add(iList);

	  pList = new String[] {
		  "mijoo",
		  "miju",
		  "leemijoo",
		  "leemiju",
		  "이미주",
		  "미주"
	  };

    iList = new TagList<String>(Arrays.asList(pList));

    mList.add(iList);

	  pList = new String[] {
		  "kei",
		  "kimjiyeon",
		  "케이",
		  "킴지연"
	  };

    iList = new TagList<String>(Arrays.asList(pList));

    mList.add(iList);

	  pList = new String[] {
		  "jin",
		  "myungeun",
		  "진",
		  "명은",
		  "박명은"
	  };

    iList = new TagList<String>(Arrays.asList(pList));

    mList.add(iList);

	  pList = new String[] {
		  "sujeong",
		  "sujung",
		  "ryusujeong",
		  "ryusujung",
		  "류수정",
		  "수정"
	  };

    iList = new TagList<String>(Arrays.asList(pList));

    mList.add(iList);

	  pList = new String[] {
		  "yein",
		  "jeongyein",
		  "jungyein",
		  "정예인",
		  "예인"
  	};

    iList = new TagList<String>(Arrays.asList(pList));

    mList.add(iList);

  }

}





