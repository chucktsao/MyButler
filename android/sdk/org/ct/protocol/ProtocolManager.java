/**
 * @file BaseManager.java 
 * @package com.qin.ct.cartoonlike.protocol.manager
 * @description 
 * @date 2013-10-11
 * @version V1.0
 *
 * @author Chuck 
 * @email chucktsao@126.com
 */

package org.ct.protocol;

import org.ct.java.http.IHttpCallback;
import org.ct.java.thread.BaseThread;
import org.ct.java.thread.ProtocolThread;
import org.ct.java.thread.ThreadPoolUtil;


import android.content.Context;

public class ProtocolManager {

	static final String TAG = "AbstractManager";

	protected BaseThread mThread;
	protected int mRequestType;
	protected IHttpCallback mCallback;
	private String mUrl;

	public ProtocolManager(IHttpCallback callback, String url, int requestType) {
		this.mCallback = callback;
		this.mUrl = url;
		this.mRequestType = requestType;
	}

	public void sendRequest(Context context, Object request, Class<?> responseClass) {
		cancle();
		try {
			mThread = new ProtocolThread(context, mCallback, mUrl, mRequestType, request, responseClass.newInstance());
			ThreadPoolUtil.startProtocol(mThread);
			return;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		mCallback.onFailed(mRequestType, "response create failed");

	}

	protected final void cancle() {
		if (mThread != null) {
			mThread.cancle();
		}
	}

	public void destroy() {
		cancle();
	}
}
