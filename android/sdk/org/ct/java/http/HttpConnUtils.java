package org.ct.java.http;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class HttpConnUtils {

	private static final String TAG = "HttpConnUtil";

	private static final int TIMEOUT = 1000;
	private static final int TIMEOUT_CONNECT = 10 * TIMEOUT;
	private static final int TIMEOUT_READ = 10 * TIMEOUT;

	private static final int BUFFER_READ = 1024 * 8;
	private static final int BUFFER_DOWNLOAD = 1024;

	/**
	 * 检测网络状态
	 */
	public static boolean checkNetworkConnectionState(Context context) {
		boolean flag = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != connectivityManager) {
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			connectivityManager = null;
			if (null != networkInfo) {
				for (int i = 0, len = networkInfo.length; i < len; i++) {
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 获取本地的IP地址
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	/**
	 * get请求。 传递URL，并且得到服务器返回的数据 联网失败，返回的数据为空，直接提示网络连接异常就可以了
	 * 如果联网成功，但获取的数据是错误的话，直接从返回的数据中解析异常信息
	 */
	public static String getHttpContent(String urlString) {
		StringBuffer stringBuffer = new StringBuffer();
		HttpURLConnection conn = null;
		InputStreamReader input = null;
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT_CONNECT);
			conn.setReadTimeout(TIMEOUT_READ);
			// conn.setDoOutput(true);//防止误认为POST
			conn.setDoInput(true);
			// conn.setRequestMethod("GET");

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				input = new InputStreamReader(conn.getInputStream());
				BufferedReader buffer = new BufferedReader(input, BUFFER_READ);
				String inputLine = "";
				while (((inputLine = buffer.readLine()) != null)) {
					stringBuffer.append(inputLine);
				}
				buffer.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {

			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (conn != null) {
				conn.disconnect();
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * map add in urlString
	 * 
	 * @param urlString
	 * @param map
	 * @return
	 */
	public static String requestMap2HttpUrl(String urlString, Map<String, Object> map) {
		StringBuffer stringBuffer = new StringBuffer(urlString);
		if (map != null) {
			Iterator<String> it = map.keySet().iterator();
			boolean isFirst = true;
			while (it.hasNext()) {
				String str = it.next();
				if (isFirst) {
					stringBuffer.append("?" + str + "=" + map.get(str));
					isFirst = false;
				} else {
					stringBuffer.append("&" + str + "=" + map.get(str));
				}
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * get请求。 传递URL，并且得到服务器返回的数据 联网失败，返回的数据为空，直接提示网络连接异常就可以了
	 * 如果联网成功，但获取的数据是错误的话，直接从返回的数据中解析异常信息
	 */
	public static String getHttpContent(String urlString, Map<String, Object> map) {
		StringBuffer stringBuffer = new StringBuffer(urlString);
		if (map != null) {
			Iterator<String> it = map.keySet().iterator();
			boolean isFirst = true;
			while (it.hasNext()) {
				String str = it.next();
				if (isFirst) {
					stringBuffer.append("?" + str + "=" + map.get(str));
					isFirst = false;
				} else {
					stringBuffer.append("&" + str + "=" + map.get(str));
				}
			}
		}

		Log.d(TAG, stringBuffer.toString().trim());

		return getHttpContent(stringBuffer.toString());
	}

	/**
	 * post请求。 传递URL，并且得到服务器返回的数据
	 */
	public static String postHttpContent(String urlStr, Map<String, Object> map) {
		Log.d(TAG, "urlStr:" + urlStr);
		StringBuffer stringBuffer = new StringBuffer();
		HttpURLConnection conn = null;
		InputStreamReader input = null;
		OutputStream os = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT_CONNECT);
			conn.setReadTimeout(TIMEOUT_READ);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setRequestProperty("Charsert", "UTF-8");

			os = conn.getOutputStream();
			if (map != null) {
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String str = it.next();
					os.write(("&" + str + "=" + map.get(str)).getBytes());
				}
			}
			os.flush();

			int tmp = conn.getResponseCode();
			Log.d(TAG, "tmp:" + tmp);
			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				input = new InputStreamReader(conn.getInputStream());
				BufferedReader buffer = new BufferedReader(input, BUFFER_READ);
				String inputLine = "";
				while (((inputLine = buffer.readLine()) != null)) {
					stringBuffer.append(inputLine);
				}
				buffer.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {

			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (conn != null) {
				conn.disconnect();
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 * 
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public static String uploadFile(String url, InputStream inputStream, String fileName) {
		StringBuffer stringBuffer = new StringBuffer();
		DataOutputStream outStream = null;
		try {
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			URL uri = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(TIMEOUT_READ); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

			outStream = new DataOutputStream(conn.getOutputStream());
			// 发送文件数据

			StringBuilder sb1 = new StringBuilder();
			sb1.append(PREFIX);
			sb1.append(BOUNDARY);
			sb1.append(LINEND);
			sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINEND);
			sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
			sb1.append(LINEND);

			outStream.write(sb1.toString().getBytes());

			if (inputStream != null) {
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
					outStream.write(buffer, 0, len);
				}
				inputStream.close();

			}

			outStream.write(LINEND.getBytes());

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();

			InputStreamReader input = null;

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				input = new InputStreamReader(conn.getInputStream());
				BufferedReader buffer = new BufferedReader(input, 1024 * 8);
				String inputLine = "";
				while (((inputLine = buffer.readLine()) != null)) {
					stringBuffer.append(inputLine);
				}
				buffer.close();
			}

			System.out.println("图片上传文件 - " + stringBuffer.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != outStream) {
				try {
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return stringBuffer.toString();

	}

	public static String uploadAndPostHttpContent(String url, Map<String, Object> map, InputStream inputStream, String fileName) {
		StringBuffer stringBuffer = new StringBuffer();
		DataOutputStream outStream = null;
		try {
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			URL uri = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(TIMEOUT_READ); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

			outStream = new DataOutputStream(conn.getOutputStream());

			if (map != null) {
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String str = it.next();
					outStream.write(("&" + str + "=" + map.get(str)).getBytes());
				}
			}

			//
			// 发送文件数据

			StringBuilder sb1 = new StringBuilder();
			sb1.append(PREFIX);
			sb1.append(BOUNDARY);
			sb1.append(LINEND);
			sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINEND);
			sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
			sb1.append(LINEND);

			outStream.write(sb1.toString().getBytes());

			if (inputStream != null) {
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
					outStream.write(buffer, 0, len);
				}
				inputStream.close();

			}
			outStream.write(LINEND.getBytes());

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();

			InputStreamReader input = null;

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				input = new InputStreamReader(conn.getInputStream());
				BufferedReader buffer = new BufferedReader(input, 1024 * 8);
				String inputLine = "";
				while (((inputLine = buffer.readLine()) != null)) {
					stringBuffer.append(inputLine);
				}
				buffer.close();
			}

			System.out.println("图片上传文件 - " + stringBuffer.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != outStream) {
				try {
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return stringBuffer.toString();

	}

	/**
	 * 直接下载
	 */
	public static boolean downloadHttp(String intUrl, String outUrl) {
		// Log.d("download", "intUrl:" + intUrl + "outUrl:" + outUrl);
		HttpURLConnection conn = null;
		FileOutputStream fOut = null;
		try {
			File outFile = new File(outUrl);

			URL url = new URL(intUrl);

			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT_CONNECT);
			conn.setDoInput(true);

			if (outFile.exists()) {
				if (outFile.length() == conn.getContentLength()) {
					return true;
				}
			} else {
				File parentFile = outFile.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
			}

			// outFile.createNewFile();

			fOut = new FileOutputStream(outFile);

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				InputStream is = conn.getInputStream();
				byte buffer[] = new byte[BUFFER_DOWNLOAD];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fOut.write(buffer, 0, count);
				}
			}

			if (outFile.length() == conn.getContentLength()) {
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
			try {
				if (null != fOut)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 直接下载
	 */
	public static boolean downloadHttpNoHttp2(String intUrl, String outUrl) {
		// Log.d("download", "intUrl:" + intUrl + "outUrl:" + outUrl);
		HttpURLConnection conn = null;
		FileOutputStream fOut = null;
		try {
			File outFile = new File(outUrl);

			URL url = new URL(intUrl);

			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT_CONNECT);
			conn.setDoInput(true);

			if (outFile.exists()) {
				if (outFile.length() == conn.getContentLength()) {
					return true;
				}
			} else {
				File parentFile = outFile.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
			}

			// outFile.createNewFile();

			fOut = new FileOutputStream(outFile);

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				InputStream is = conn.getInputStream();
				byte buffer[] = new byte[BUFFER_DOWNLOAD];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fOut.write(buffer, 0, count);
				}
			}

			// if (outFile.length() == conn.getContentLength()) {
			return true;
			// }

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
			try {
				if (null != fOut)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 多进程多线程独占锁直接下载
	 */
	public synchronized static boolean downloadHttpFileLock(String inUrl, String outUrl) {
		Log.d("download", "intUrl:" + inUrl + "outUrl:" + outUrl);
		HttpURLConnection conn = null;
		RandomAccessFile fOut = null;
		try {
			File outFile = new File(outUrl);

			URL url = new URL(inUrl);

			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT_CONNECT);
			conn.setDoInput(true);

			if (outFile.exists()) {
				if (outFile.length() == conn.getContentLength()) {
					return true;
				}
			} else {
				File parentFile = outFile.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
			}

			fOut = new RandomAccessFile(outFile, "rw");
			FileLock lock = fOut.getChannel().lock();
			long length = conn.getContentLength();
			if (outFile.length() == length) {
				return true;
			}
			if (null != lock) {
				fOut.seek(0);
				if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
					InputStream is = conn.getInputStream();
					byte buffer[] = new byte[BUFFER_DOWNLOAD];
					int count = 0;
					while ((count = is.read(buffer)) > 0) {
						fOut.write(buffer, 0, count);
					}
				}
				lock.release();
			}

			if (outFile.length() == length) {
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
			try {
				if (null != fOut)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * 断点续传
	 */

	public static void downloadHttpBP(String inUrl, String outUrl) throws MalformedURLException, FileNotFoundException, IOException {
		File outputFile = new File(outUrl);
		downloadHttpBP(inUrl, outputFile, 0);
	}

	public static void downloadHttpBP(String inUrl, String outUrl, boolean wantContinue) throws MalformedURLException, FileNotFoundException,
			IOException {
		File outputFile = new File(outUrl);
		if (wantContinue) {
			downloadHttpBP(inUrl, outputFile, outputFile.length());
		} else {
			downloadHttpBP(inUrl, outputFile, 0);
		}

	}

	public static void downloadHttpBP(String inUrl, String outUrl, long pos) throws MalformedURLException, FileNotFoundException, IOException {
		File outputFile = new File(outUrl);
		downloadHttpBP(inUrl, outputFile, pos);
	}

	public static void downloadHttpBP(String inUrl, File outputFile, long pos) throws MalformedURLException, FileNotFoundException, IOException {
		URL url = new URL(inUrl);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(TIMEOUT_CONNECT);
		conn.setReadTimeout(TIMEOUT_READ);

		long length = conn.getContentLength();// 下载数据长度

		conn.setRequestProperty("Range", "bytes=" + pos + "-");
		DataInputStream downloadStream = new DataInputStream(conn.getInputStream());

		RandomAccessFile oFile = new RandomAccessFile(outputFile, "rw");

		int len = 0;
		try {
			byte[] buf = new byte[BUFFER_DOWNLOAD];
			oFile.seek(pos);
			oFile.setLength(pos);
			do {
				if (pos >= length) {
					oFile.setLength(length);
					pos = length;
				} else {
					oFile.write(buf, 0, len);
					pos = outputFile.length();
				}
			} while (1 != (len = downloadStream.read(buf)));
		} finally {
			if (null != oFile)
				oFile.close();
			if (null != downloadStream)
				downloadStream.close();
		}
	}

	public static String getHttpContentHttpClient(String url) {

		String result = null;
		HttpGet httpRequest = new HttpGet(url);// 创建HTTP Get连接
		try {

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
			HttpClient client = new DefaultHttpClient(httpParameters);

			HttpResponse response = client.execute(httpRequest);
			;// new DefaultHttpClient().execute(httpRequest);
			if (response.getStatusLine().getStatusCode() == 200) { // 当访问服务器有效时
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String getHttpContentFirefox(String urlString) {
		StringBuffer stringBuffer = new StringBuffer();
		HttpURLConnection conn = null;
		InputStreamReader input = null;
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT_CONNECT);
			conn.setReadTimeout(TIMEOUT_READ);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("accept", "text/html");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; rv:2.0b11) Gecko/20100101 Firefox/4.0b11");

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				input = new InputStreamReader(conn.getInputStream());
				BufferedReader buffer = new BufferedReader(input, BUFFER_READ);
				String inputLine = "";
				while (((inputLine = buffer.readLine()) != null)) {
					stringBuffer.append(inputLine);
				}
				buffer.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return stringBuffer.toString();
	}

}
