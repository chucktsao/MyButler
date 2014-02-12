/**
 * @file CommonUtils.java 
 * @package org.ct.android.utils
 * @description 
 * @date 2013-9-2
 * @version V1.0
 *
 * @author Chuck 
 * @email chucktsao@126.com
 */
package org.ct.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class CommonUtils {
	public static final String TAG = "CommonUtils";

	public static String getTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
		return sdf.format(date);
	}

	public static String long2Time(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return sdf.format(date);
	}

	public static String getDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
		return sdf.format(date);
	}

	public static String decimal2CN(long num) {
		StringBuilder strBuild = new StringBuilder();
		int value = 0;
		int count = 0;
		do {
			value = (int) (num % 10);

			if (count % 4 == 1) {
				strBuild.append("拾");
			} else if (count % 4 == 2) {
				strBuild.append("百");
			} else if (count % 4 == 3) {
				strBuild.append("千");
			} else if (count == 4) {
				strBuild.append("万");
			} else if (count == 8) {
				strBuild.append("亿");
			}

			strBuild.append(num2CN(value));
			count++;

		} while ((num /= 10) > 0);
		strBuild.reverse();// 倒序
		return strBuild.toString();
	}

	public static String num2CN(int num) {
		String tmp = "";
		switch (num) {
		case 0:
			tmp = "零";
			break;
		case 1:
			tmp = "一";
			break;
		case 2:
			tmp = "二";
			break;
		case 3:
			tmp = "三";
			break;
		case 4:
			tmp = "四";
			break;
		case 5:
			tmp = "五";
			break;
		case 6:
			tmp = "六";
			break;
		case 7:
			tmp = "七";
			break;
		case 8:
			tmp = "八";
			break;
		case 9:
			tmp = "九";
			break;
		}

		return tmp;
	}

	/**
	 * 阿拉伯数字转为中文
	 */
	/**
	 * null的时候代表不存在后缀名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExtensionName(String filename) {
		String extensions = null;
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				extensions = filename.substring(dot + 1);
				if (extensions.contains("/")) {
					extensions = null;
				}
			}
		}
		return extensions;
	}

	/**
	 * 获取备份名字
	 * 
	 * @param filename
	 * @return
	 */
	public static String getDuplicateName(String filename) {
		if (filename == null)
			return null;
		String extensions = getExtensionName(filename);
		if (extensions == null) {
			return filename + "(%1$s)";
		} else {
			int index = filename.lastIndexOf(extensions);
			index--;
			return filename.substring(0, index) + "_%1$s" + "." + extensions;
		}
	}

	/**
	 * 删除整个文件夹
	 * 
	 * @param folderPath
	 * @param type
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFileWithoutType(folderPath, null); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除后缀为type的文件
	 * 
	 * @param path
	 * @param type
	 *            type!=null的时候全部不删除
	 * @return
	 */
	public static boolean delAllFileWithTypeInPath(String path, String type) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}

		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}

			if (temp.isFile()) {
				if (type != null && type.equals(getExtensionName(temp.getName()))) {
					temp.delete();
				}
			}
		}

		return flag;
	}

	/**
	 * 删除后缀不为type的文件和全部文件夹
	 * 
	 * @param path
	 * @param type
	 *            : type==null的时候全部删除
	 * @return
	 */
	public static boolean delAllFileWithoutType(String path, String type) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}

			if (temp.isFile()) {
				if (type == null || !type.equals(getExtensionName(temp.getName()))) {
					temp.delete();
				}
			}
			if (temp.isDirectory()) {
				delFolder(path + "/" + tempList[i]);// 先删除文件夹里面的文件
			}
		}
		flag = true;
		return flag;
	}

	/**
	 * 邮箱正则式
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 手机号码正则式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isCellphone(String str) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	// 将对象转化为2进制数组
	public synchronized static byte[] object2byte(Context context, Object object) {
		if (object == null) {
			return null;
		}
		byte[] to2b = null;
		ByteArrayOutputStream out = null;
		ObjectOutputStream os = null;
		try {
			out = new ByteArrayOutputStream();
			os = new ObjectOutputStream(out);
			os.writeObject(object);
			to2b = out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return to2b;
	}

	// 将 2进制数组转化为对象
	@SuppressWarnings("unchecked")
	public synchronized static <T extends Object> T byte2object(Context context, byte[] b, Class<T> clazz) {
		if (b == null || b.length < 1) {
			return null;
		}

		byte tmp[] = b;
		T toObj = null;

		ByteArrayInputStream in = new ByteArrayInputStream(tmp);
		ObjectInputStream is;
		try {
			is = new ObjectInputStream(in);
			toObj = (T) is.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return toObj;

	}

	public static String list2String(ArrayList<String> list) {
		StringBuffer buffer = new StringBuffer();

		for (String str : list) {
			buffer.append(str + ";");
		}

		return buffer.toString();
	}

	public static ArrayList<String> string2List(String str) {
		ArrayList<String> list = new ArrayList<String>();

		if (null != str) {
			int index = str.indexOf(";");

			while (index >= 0) {
				list.add(str.substring(0, index));
				str = str.substring(index + 1);
				index = str.indexOf(";");
			}
		}

		return list;
	}

	public static ArrayList<String> serverString2List(String str) {
		ArrayList<String> list = new ArrayList<String>();

		if (null != str) {
			int index = str.indexOf(",");

			while (index >= 0) {
				list.add(str.substring(0, index));
				str = str.substring(index + 1);
				index = str.indexOf(",");
			}
			list.add(str);
		}

		return list;
	}

	public static final long dayTime = 24 * 60 * 60 * 1000;
	public static final long hourTime = 60 * 60 * 1000;
	public static final long minuteTime = 60 * 1000;

	public static String getIntervalTime(long lastTime) {
		long intervalTime = System.currentTimeMillis() - lastTime;
		if (lastTime == 0) {
			return "未知";
		}
		if (intervalTime > dayTime) {
			return (intervalTime / dayTime) + "天前";
		} else if (intervalTime > hourTime) {
			return (intervalTime / hourTime) + "小时前";
		} else if (intervalTime > minuteTime) {
			return (intervalTime / minuteTime) + "分钟前";
		} else {
			return "1分钟内";
		}
	}

}
