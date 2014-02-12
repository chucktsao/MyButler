/**
 * @file NetBitmapCache.java 
 * @package org.ct.android.bitmap
 * @description 
 * @date 2013-12-23
 * @version V1.0
 *
 * @author Chuck 
 * @email chucktsao@126.com
 */
package org.ct.android.bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ct.util.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class IconCache {
	static final String TAG = "NetBitmapCache";

	private IMemoryCache mMemoryCache;
	private BlockingQueue<Runnable> queue;
	private ThreadPoolExecutor executor;

	public IconCache() {
		init();
		queue = new LinkedBlockingQueue<Runnable>();
		executor = new ThreadPoolExecutor(1, 50, 180, TimeUnit.SECONDS, queue);
	}

	/**
	    * 初始化 图片缓存
	    * @param cacheParams
	    */
	private void init() {
		mMemoryCache = new SoftMemoryCacheImpl();
	}

	@SuppressLint("HandlerLeak")
	public Bitmap loadImage(final Context context, final String imageUrl, final ImageCallback imageCallback) {
		Bitmap img = getImageFormMemoryCache(imageUrl);

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap)message.obj, imageUrl);
			}
		};

		if (img == null) {
			img = getImageFromDiskCache(context, imageUrl);
			if (null != img && img.getWidth() > 0 && img.getHeight() > 0) {
				mMemoryCache.put(imageUrl, img);
			} else {
				img = null;
			}
		}

		executor.execute(new Runnable() {
			@Override
			public void run() {
				Bitmap img = getImageFromUrl(context, imageUrl);
				if (null != img && img.getWidth() > 0 && img.getHeight() > 0) {
					mMemoryCache.put(imageUrl, img);
				} else {
					img = null;
				}
				Message message = handler.obtainMessage(0, img);
				handler.sendMessage(message);
			}
		});
		return img;

	}

	private Bitmap getImageFormMemoryCache(final String imageUrl) {
		return mMemoryCache.get(imageUrl);
	}

	private Bitmap getImageFromDiskCache(final Context context, final String imageUrl) {
		String path = getImageDiskCachePath(context, imageUrl);
		return Utils.loadIcon(path);
	}

	private Bitmap getImageFromUrl(final Context context, final String imageUrl) {

		String imagePath = getImageDiskCachePath(context, imageUrl);
		File file = new File(imagePath);

		if (file.exists()) {
			file.delete();
		}
		Log.d(TAG, "loading file..");
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			fos = new FileOutputStream(file);

			HttpGet httpRequest = new HttpGet(imageUrl);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
			is = bufferedHttpEntity.getContent();
			int data = is.read();
			while (data != -1) {
				fos.write(data);
				data = is.read();

			}
			return Utils.loadIcon(imagePath);
		} catch (Exception e) {
			Log.e(TAG, e.toString() + "图片下载及保存时出现异常！");
		} finally {
			try {
				if (null != fos)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (null != is)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * SDK上图片缓存地址
	 * @param context
	 * @param imageUrl
	 * @return
	 */
	public static String getImageDiskCachePath(Context context, String imageUrl) {
		String imagePath;
		String fileName = "";
		if (imageUrl != null && imageUrl.length() != 0) {
			fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

		}
		imagePath = context.getCacheDir() + "/" + fileName;
		return imagePath;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageBitmap, String imageUrl);
	}

}
