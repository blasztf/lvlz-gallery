package com.lvlz.gallery.data.filter;

import java.util.Arrays;
import java.util.ArrayList;

public class TagList<T> extends ArrayList<T> {

  private TagList<TagList<String>> list;

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

    for (TagList<String> listTag : mInstance.list) {

      for (String iTag : listTag) {

        if (tag.equals(iTag)) {

          return listTag;
        
        }

      }
          
    }

    return new TagList<String>();

  }

  private void generate() {

    TagList<String> iList;
    String[] pList;

    if (list == null) {

      list = new TagList<TagList<String>>();

    }
    else {

      list.clear();

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

    list.add(iList);

	  pList = new String[] {
		  "jiae",
		  "yoojiae",
		  "yujiae",
	  	"유지애",
	  	"지애"
  	};

    iList = new TagList<String>(Arrays.asList(pList));

    list.add(iList);

	  pList = new String[] {
		  "jisoo",
		  "jisu",
		  "seojisoo",
		  "seojisu",
		  "서지수",
		  "지수"
  	};

    iList = new TagList<String>(Arrays.asList(pList));

    list.add(iList);

	  pList = new String[] {
		  "mijoo",
		  "miju",
		  "leemijoo",
		  "leemiju",
		  "이미주",
		  "미주"
	  };

    iList = new TagList<String>(Arrays.asList(pList));

    list.add(iList);

	  pList = new String[] {
		  "kei",
		  "kimjiyeon",
		  "케이",
		  "킴지연"
	  };

    iList = new TagList<String>(Arrays.asList(pList));

    list.add(iList);

	  pList = new String[] {
		  "jin",
		  "myungeun",
		  "진",
		  "명은",
		  "박명은"
	  };

    iList = new TagList<String>(Arrays.asList(pList));

    list.add(iList);

	  pList = new String[] {
		  "sujeong",
		  "sujung",
		  "ryusujeong",
		  "ryusujung",
		  "류수정",
		  "수정"
	  };

    iList = new TagList<String>(Arrays.asList(pList));

    list.add(iList);

	  pList = new String[] {
		  "yein",
		  "jeongyein",
		  "jungyein",
		  "정예인",
		  "예인"
  	};

    iList = new TagList<String>(Arrays.asList(pList));

    list.add(iList);

  }

}





