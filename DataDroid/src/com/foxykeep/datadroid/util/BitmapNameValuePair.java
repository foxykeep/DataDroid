package com.foxykeep.datadroid.util;

import android.graphics.Bitmap;

public class BitmapNameValuePair {

	private final String mName;
	private final String mFileName;
	private final Bitmap mBitmap;

	public BitmapNameValuePair(String name, String fileName, Bitmap bitmap) {
		mName = name;
		mFileName = fileName;
		mBitmap = bitmap;
	}

	public String getName() {
		return mName;
	}

	public String getFileName() {
		return mFileName;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

}
