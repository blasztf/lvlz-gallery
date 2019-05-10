package com.lvlz.gallery.data;

import java.util.List;
import java.util.ArrayList;

public class DataResult implements CacheImpl {

	public List<Data> dataCollections;
	
	public long nextPointer;
	
	public int codeErr;

	public DataResult() {
	
		dataCollections = new ArrayList<Data>();
		
	}

	public static class Data implements CacheImpl {

		public String uname;

		public String name;

		public String tweet;

		public List<String> tags;

		public List<String> photos;

		public Data() {
    
			tags = new ArrayList<String>();

			photos = new ArrayList<String>();

		}

	}
	
}
