/**
 * @file ILoadBitmapCallback.java 
 * @package org.ct.java.http
 * @description 
 * @date 2014-1-14
 * @version V1.0
 *
 * @author Chuck 
 * @email chucktsao@126.com
 */
package org.ct.java.http;

import android.graphics.Bitmap;

public interface ILoadBitmapCallback {
	static final String TAG = "ILoadBitmapCallback";

	public void onSuccess(int requestType, String url, Bitmap img);

	public void onFailed(int requestType, String message);
}
