package com.scxh.gift.android.util;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

public class AsyncImageLoader {
	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	public AsyncImageLoader() {
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
	}
	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}
	
	public Bitmap loadBitmap(final String imageUrl,final ImageCallbackForBitmap callback) {
		Logs.v("loadBitmap "+imageUrl);
		if (imageUrl == null) {
			return null;
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				callback.imageLoaded((Bitmap) msg.obj, imageUrl);
			}
		};
		
		Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
		if( bitmap != null){
			Logs.v("从内存取图片 "+imageUrl);
			return bitmap;
		}
		
		new Thread() {
			public void run() {
				Logs.v("从网络取图片 "+imageUrl);
				Bitmap bitmap = loadImageBitmapFromUrl(imageUrl);
				addBitmapToMemoryCache(imageUrl,bitmap);
				if (bitmap != null) {
					handler.sendMessage(handler.obtainMessage(0, bitmap));
				}
			}
		}.start();

		return null;
	}

	protected Bitmap loadImageBitmapFromUrl(String imageUrl) {
		try {
			URL rul = new URL(imageUrl);
			InputStream is = rul.openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public interface ImageCallbackForBitmap {
		public void imageLoaded(Bitmap bitmap, String imageUrl);
	}
}
