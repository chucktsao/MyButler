/**
 * @file ProtocolThread.java 
 * @package com.android.ct.comic.thread
 * @create 2013-4-17
 * @author Chuck 
 * @email chucktsao@126.com
 * @description 
 */
package org.ct.java.thread;

import java.util.Map;

import org.ct.java.http.HttpConnUtils;
import org.ct.java.http.IHttpCallback;
import org.ct.java.http.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public final class ProtocolThread extends BaseThread {

	private static final String TAG = "ProtocolThread";

	private static final boolean USEPOST = false;

	private Context mContext;

	private Object mRequest = null;

	private Object mResponse;

	private String mUrl;

	private IHttpCallback mCallback;
	private int mRequestType;

	public ProtocolThread(Context context, IHttpCallback callback, String url, int requestType, Object request, Object response) {
		this.mContext = context;
		this.mRequest = request;
		this.mResponse = response;
		this.mUrl = url;
		this.mCallback = callback;
		this.mRequestType = requestType;
	}

	public void run() {

		if (!HttpConnUtils.checkNetworkConnectionState(mContext)) {
			// 没有网络连接
			mCallback.onFailed(mRequestType, "没有网络连接");
			return;
		}
		if (isCancle()) {
			return;// 取消
		}

		String res = null;
		if (USEPOST) {
			Map<String, Object> requestMap = JsonUtils.getRequestMap(mRequest);

			res = HttpConnUtils.postHttpContent(mUrl, requestMap);
		} else {
			Map<String, Object> requestMap = JsonUtils.getRequestMap(mRequest);

			res = HttpConnUtils.getHttpContent(mUrl, requestMap);
		}

		if (null == res) {
			mCallback.onFailed(mRequestType, "response is null");
			return;
		}
		Log.d(TAG, "back res" + res);

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(res);
		} catch (JSONException e) {
			mCallback.onFailed(mRequestType, "JSONException");
			e.printStackTrace();
			return;
		}

		try {
			JsonUtils.decodeReponse(jsonObject, mResponse);
		} catch (IllegalArgumentException e) {
			mCallback.onFailed(mRequestType, "IllegalArgumentException");
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			mCallback.onFailed(mRequestType, "IllegalAccessException");
			e.printStackTrace();
			return;
		}

		if (!isCancle() && null != mCallback) {
			mCallback.onSuccess(mRequestType, mResponse);
		}

	}
}
