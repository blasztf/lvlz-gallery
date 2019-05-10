package com.lvlz.gallery.data;

import java.util.Map;

import com.lvlz.gallery.debugger.DebugControl;

import com.google.gson.Gson;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class DataRetriever {

  private static DataRetriever mInstance;

  private long mPointer;

  private DataRetriever.Options mOptions;

  private CacheControl mCacheControl;

  private DataSource mDataSource;

  private DataRetriever() {
  
    mCacheControl = new CacheControl();

  }

  public static class Options {

    public String userAgent;

    public String referer;

  }

  private static class Response {

    public String content;

    public long pointer;

    public Response() {}

  }

  private static void lazyLoad() {

    if (mInstance == null) {

      mInstance = new DataRetriever();

    }

  }

  public static DataRetriever with(long pointer, DataRetriever.Options options) {

    DataRetriever.lazyLoad();

    mInstance.mPointer = pointer;

    mInstance.mDataSource = pointer == 0l ? DataSource.GLOBAL : DataSource.GLOBAL.setPointer(pointer);

    if (options != null) mInstance.mOptions = options;

    return mInstance;

  }

  public static DataRetriever with(long pointer) {
    
    Options opts = new Options();

    opts.userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0";

    opts.referer = "https://twitter.com/\r\n";

    return DataRetriever.with(pointer, opts);

  }

  private boolean shouldUseCache() {

    return !mCacheControl.expired() && mCacheControl.contains(mPointer);

  }

  private Response format(String response) {

    Map tweetData;

    Document document;

    Response objResponse = new Response();

    if (mDataSource.type == DataSource.TYPE_PRIMARY) {

      document = Jsoup.parse(response);

      objResponse.content = document.selectFirst("ol#stream-items-id").html();

      objResponse.pointer = Long.parseLong(document.selectFirst(".stream-container").attr("data-min-position"));

    }
    else {
      
      tweetData = new Gson().fromJson(response, Map.class);

      objResponse.content = (String) tweetData.get("items_html");

      objResponse.pointer = ((Double) tweetData.get("next_pointer")).longValue();

    }

    return objResponse;

  }

  private DataResult parse(Response response) {

    DataResult resultData = new DataResult();

    Document document = Jsoup.parse("<html><head></head><body><div><ul id=\"contents\">" + response.content + "</ul></div></body></html>");

    Elements items = document.select("#contents > li.stream-item");

    DataResult.Data data; 

    Element item;

    Elements subItem;

    for (int i = 0; i < items.size(); i++) {

        data = new DataResult.Data();

        item = items.get(i).selectFirst("div.tweet");

        data.uname = item.attr("data-screen-name");

        data.name = item.attr("data-name");

        item = items.get(i).selectFirst("p.tweet-text");

        data.tweet = item.text();
        
        subItem = item.select("a.twitter-hashtag");

        for (int j = 0; j < subItem.size(); j++) {

          data.tags.add(subItem.get(j).text().replace("#", ""));

        }

        subItem = items.get(i).select("div.AdaptiveMedia-photoContainer");

        for (int j = 0; j < subItem.size(); j++) {

          data.photos.add(subItem.get(j).attr("data-image-url"));

        }

        resultData.dataCollections.add(data);

    }

    return resultData;

  }

  public DataResult retrieve() {

    DataResult data = null;
    String response = null;
    Response formatResponse = null;

    if (shouldUseCache()) {

      data = mCacheControl.load(mPointer, DataResult.class);
      
    }
    else {

      try {

        response = Jsoup.connect(mDataSource.url).userAgent(mOptions.userAgent).header("referer", mOptions.referer).execute().body();

        formatResponse = format(response);

        data = parse(formatResponse);

        mCacheControl.cache(mPointer, data);

      }
      catch (IOException e) {

        DebugControl.process(e);

      }

    }

    return data;

  }

}
