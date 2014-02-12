package org.ct.java.thread;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;


import org.ct.java.http.HttpConnUtils;
import org.ct.java.http.IHttpCallback;
import org.ct.java.http.JsonUtils;
import org.ct.util.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;

public class UploadThread extends BaseThread {

	private Context context;
	private String serverPath;
	private String filePath;
	private IHttpCallback mCallback;
	
	private int mRequestType;
	
	
	private Object mResponse;
	
	
	public UploadThread(Context context,String serverPath,String filePath,int requestType, Object response,IHttpCallback callBack){
		this.context = context;
		this.serverPath = serverPath;
		this.filePath = filePath;
		this.mRequestType =requestType;
		this.mCallback = callBack;
		this.mResponse = response;
		
	}


	public void run() {
		
		if (!HttpConnUtils.checkNetworkConnectionState(context)){
			if (mCallback != null) {
				mCallback.onFailed(mRequestType,"无网络");
			}
			return;
		}
		
		String end = CommonUtils.getExtensionName(filePath);
		if(null==end||"".equals(end)){
			mCallback.onFailed(mRequestType,"上传文件有误");
			return;	
		}
		
		String fileName = "source."+end;
		
		InputStream inStream= null;
		try {
			inStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String res= null;
		try {
			res=HttpConnUtils.uploadFile(serverPath, inStream,fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		
		if(null==res){
			mCallback.onFailed(mRequestType,"服务器连接异常！");
			return;
		}
		
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(res);
		} catch (JSONException e) {
			mCallback.onFailed(mRequestType,null);
			e.printStackTrace();
			return;
		}

		try {
			JsonUtils.decodeReponse(jsonObject, mResponse);
		} catch (IllegalArgumentException e) {
			mCallback.onFailed(mRequestType,"服务器连接异常！");
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			mCallback.onFailed(mRequestType,"服务器连接异常！");
			e.printStackTrace();
			return;
		}

		if (!isCancle() && null != mCallback) {
			mCallback.onSuccess(mRequestType,mResponse);
		}
	}
}
