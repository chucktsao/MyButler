/**
 * @file DeviceUtils.java 
 * @package org.ct.android.utils
 * @description 
 * @date 2013-9-2
 * @version V1.0
 *
 * @author Chuck 
 * @email chucktsao@126.com
 */
package org.ct.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Utils extends JavaUtils{
	public static final String TAG = "Utils";
	
	public static void actionGalleryFromActivity(final int result, final Activity activity) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		activity.startActivityForResult(intent, result);
	}

	public static void actionGalleryFromFragment(final int result, final Fragment fragment) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		fragment.startActivityForResult(intent, result);
	}

	// 获取设备信息

	/**
	 * @description get imei form device
	 * @param context
	 *            :activity or app context;
	 * @return
	 */
	public static String getImei(Context context) {

		if (context == null) {
			throw new IllegalArgumentException("context cannot be null!");
		}

		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (phoneManager == null) {
			return null;
		}
		String imei = phoneManager.getDeviceId();
		if (imei == null || "".equals(imei)) {
			return null;
		}
		return imei;
	}

	/**
	 * @description get imsi form device
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {

		if (context == null) {
			throw new IllegalArgumentException("context cannot be null!");
		}

		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (phoneManager == null) {
			return null;
		}
		String imsi = phoneManager.getSubscriberId();
		if (imsi == null | "".equals(imsi)) {

			return null;
		}
		return imsi;
	}

	/**
	 * @description get phone number form device
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {

		if (context == null) {
			throw new IllegalArgumentException("context cannot be null!");
		}
		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (phoneManager == null) {
			return null;
		}
		String phoneNumber = phoneManager.getLine1Number();
		if (phoneNumber == null | "".equals(phoneNumber)) {

			return null;
		}
		return phoneNumber;
	}

	public static String convertSysVersion(String version) {
		String sdk = null;
		sdk = version;
		if (version.equals("4")) {
			sdk = "1.6";
		} else if (version.equals("7")) {
			sdk = "2.1";
		} else if (version.equals("8")) {
			sdk = "2.2";
		} else if (version.equals("9") || version.equals("10")) {
			sdk = "2.3";
		} else if (version.equals("11") || version.equals("12") || version.equals("13")) {
			sdk = "3.0";
		} else if (version.equals("14") || version.equals("15")) {
			sdk = "4.0";
		} else if (version.equals("16")) {
			sdk = "4.1";
		} else if (version.equals("17")) {
			sdk = "4.2";
		} else if (version.equals("18")) {
			sdk = "4.3";
		}
		return sdk;
	}

	/**
	 * 获取手机总内存
	 * 
	 * @return unit:MB
	 */
	public static int getDeviceTotalMemory() {
		String str1 = "/proc/meminfo";
		String str2;
		String[] arrayOfString;
		int initialMemory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			if (localBufferedReader != null) {
				str2 = localBufferedReader.readLine();
				if (str2 != null) {
					arrayOfString = str2.split("\\s+");
					for (String num : arrayOfString) {
						Log.i(str2, num + "\t");
					}
					initialMemory = Integer.valueOf(arrayOfString[1]).intValue();// KB
					localBufferedReader.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return initialMemory / 1024; // MB
	}

	/**
	 * 将"content://media/external/images/media" 转化为
	 * "/mnt/sdcard/DCIM/Camera/xxx.jpg" "
	 * 
	 * @param uri
	 * @param cr
	 * @return
	 */
	public static String getFilePathFormContentUriForImage(Uri uri, ContentResolver cr) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = cr.query(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		return img_path;
	}

	/**
	 * 将 "/mnt/sdcard/DCIM/Camera/xxx.jpg" 转化为
	 * "content://media/external/images/media"
	 * 
	 * @param path
	 * @param cr
	 * @return
	 */
	public static Uri getContentUriFormFilePathForImage(String path, ContentResolver cr) {
		Uri mUri = Uri.parse("content://media/external/images/media");
		Uri mImageUri = null;

		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
			if (path.equals(data)) {
				int ringtoneID = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
				mImageUri = Uri.withAppendedPath(mUri, "" + ringtoneID);
				break;
			}
			cursor.moveToNext();
		}
		return mImageUri;
	}

	/***
	 * 判断 URI 是否是 ContentUri : "content://media/external/"
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isContentUri(Uri uri) {
		return "content".equals(uri.getScheme());
	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		if (null != sdDir)
			return sdDir.getAbsolutePath();
		else
			return "";
	}

	// sd卡剩余内存

	// sd卡总空间

	// app 信息
	public static String getVersionName(Context context) {

		if (context == null) {
			throw new IllegalArgumentException("context cannot be null!");
		}

		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;

	}

	public static int getVersionCode(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("context cannot be null!");
		}

		int versionCode = -1;// error code
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 判断快捷方式是否创建
	 * 
	 * @param context
	 * @param name
	 *            快捷方式名称
	 * @return
	 */
	public static boolean hasShortcut(Context context, String name) {
		boolean isInstallShortcut = false;
		final ContentResolver cr = context.getContentResolver();
		final String AUTHORITY = "com.android.launcher.settings";
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
		Cursor cursor = null;
		try {
			cursor = cr.query(CONTENT_URI, new String[] { "title", "iconResource" }, "title=?", new String[] { name }, null);
			if (cursor != null && cursor.getCount() > 0) {
				isInstallShortcut = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return isInstallShortcut;
	}

	/**
	 * 添加快捷方式
	 */
	public static void addShortcut(Activity activity, String name, int resourceId) {

		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序

		/**************************** 此方法已失效 *************************/
		// ComponentName comp = new ComponentName(activity.getPackageName(), "."
		// + activity.getLocalClassName());
		// shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new
		// Intent(Intent.ACTION_MAIN).setComponent(comp));

		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(activity, resourceId);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		// 添加要做的事情
		Intent todo = new Intent(Intent.ACTION_MAIN);
		todo.setClassName(activity, activity.getClass().getName());
		todo.putExtra("test1", "test");
		// 点击快捷方式 进行 todo 操作
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, todo);

		activity.sendBroadcast(shortcut);

	}

	/**
	 * 删除快捷方式
	 */
	public static void delShortcut(Activity activity, String name) {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		String appClass = activity.getPackageName() + "." + activity.getLocalClassName();
		ComponentName comp = new ComponentName(activity.getPackageName(), appClass);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
		activity.sendBroadcast(shortcut);
	}

	// Android　图片内存　８Ｍ 单位 byte = 8 bit

	public static long TOTAL_BITMAP_MEMORY = 8 * 1024 * 1024;// unit byte

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public static long getBitmapOfMemorySize(Bitmap img) {
		if (img == null) {
			throw new IllegalArgumentException("img cannot be null!");
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return img.getByteCount();
		}
		Bitmap.Config config = img.getConfig();

		int unit = 4;

		switch (config) {
		case ALPHA_8: // 1 byte
			unit = 1;
			break;
		case RGB_565: // 2byte
			unit = 2;
			break;
		case ARGB_4444:// 2byte
			unit = 2;
			break;
		case ARGB_8888:// 2 byte
			unit = 4;
			break;
		}

		return img.getWidth() * img.getHeight() * unit;

	}

	// 从SD卡上获取加载图片，未防止图片过大，自动缩放。
	public static Bitmap loadBimap(String path) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		BitmapFactory.decodeFile(path, options);
		// 保证加载的图片大小不超过 1024*1024*4
		int size = (options.outWidth * options.outHeight) >> 20;
		if (size >= 4) {
			options.inSampleSize = 4;
		} else if (size >= 1) {
			options.inSampleSize = 2;
		}

		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		// Log.d(TAG,
		// "ByteCount:"+bitmap.getByteCount()+"options.inSampleSize:"+options.inSampleSize);
		return bitmap;
	}
	
	// 从SD卡上获取加载图片，未防止图片过大，自动缩放。
	public static Bitmap loadIcon(String path) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		BitmapFactory.decodeFile(path, options);
		// 保证加载的图片大小不超过 256*256*4
		int size = (options.outWidth * options.outHeight) >> 16;
		if (size >= 4) {
			options.inSampleSize = 4;
		} else if (size >= 1) {
			options.inSampleSize = 2;
		}

		options.inJustDecodeBounds = false;

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 获取可以使用的缓存目录
	 * 
	 * @param context
	 * @param uniqueName
	 *            目录名称
	 * @return
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? getExternalCacheDir(context).getPath()
				: context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取bitmap的字节大小
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int getBitmapSize(Bitmap bitmap) {
		return (bitmap.getRowBytes() * bitmap.getHeight());
	}

	/**
	 * 获取程序外部的缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static File getExternalCacheDir(Context context) {
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

	/**
	 * 获取文件路径空间大小
	 * 
	 * @param path
	 * @return
	 */
	public static long getUsableSpace(File path) {
		try {
			final StatFs stats = new StatFs(path.getPath());
			return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
		} catch (Exception e) {
			Log.e(TAG, "获取 sdcard 缓存大小 出错，请查看AndroidManifest.xml 是否添加了sdcard的访问权限");
			e.printStackTrace();
			return -1;
		}

	}

	public static byte[] getBytes(String in) {
		byte[] result = new byte[in.length() * 2];
		int output = 0;
		for (char ch : in.toCharArray()) {
			result[output++] = (byte) (ch & 0xFF);
			result[output++] = (byte) (ch >> 8);
		}
		return result;
	}

	public static boolean isSameKey(byte[] key, byte[] buffer) {
		int n = key.length;
		if (buffer.length < n) {
			return false;
		}
		for (int i = 0; i < n; ++i) {
			if (key[i] != buffer[i]) {
				return false;
			}
		}
		return true;
	}

	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}

	private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
	private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

	private static long[] sCrcTable = new long[256];

	static {
		// 参考 http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
		long part;
		for (int i = 0; i < 256; i++) {
			part = i;
			for (int j = 0; j < 8; j++) {
				long x = ((int) part & 1) != 0 ? POLY64REV : 0;
				part = (part >> 1) ^ x;
			}
			sCrcTable[i] = part;
		}
	}

	public static byte[] makeKey(String httpUrl) {
		return getBytes(httpUrl);
	}

	/**
	 * A function thats returns a 64-bit crc for string
	 * 
	 * @param in
	 *            input string
	 * @return a 64-bit crc value
	 */
	public static final long crc64Long(String in) {
		if (in == null || in.length() == 0) {
			return 0;
		}
		return crc64Long(getBytes(in));
	}

	public static final long crc64Long(byte[] buffer) {
		long crc = INITIALCRC;
		for (int k = 0, n = buffer.length; k < n; ++k) {
			crc = sCrcTable[(((int) crc) ^ buffer[k]) & 0xff] ^ (crc >> 8);
		}
		return crc;
	}
	// 从assets 文件夹中获取文件并读取数据
		public static String getJsonFromAssets(Context context, String fileName) {
			StringBuffer strBuffer = new StringBuffer();
			InputStream in = null;
			InputStreamReader isr = null;
			BufferedReader read = null;
//			strBuffer.append("{\"data\"");
			try {
				in = context.getResources().getAssets().open(fileName);
				if (null != in) {
					isr = new InputStreamReader(in, "UTF-8");
					if (null != isr)
						read = new BufferedReader(isr);
				}

				String str;
				while ((str = read.readLine()) != null) {
					str = str.trim();
					str = str.replaceAll(" ", "");
					strBuffer.append(str);
					// System.out.print(str);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != read) {
					try {
						read.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (null != isr) {
					try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (null != in) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
//			strBuffer.append("}");
			return strBuffer.toString();
		}
}
