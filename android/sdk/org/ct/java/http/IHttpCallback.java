package org.ct.java.http;

public interface IHttpCallback {

	public static final int RESULT_SUCCEED = 200;
	public static final int RESULT_FAILED = 400;
	public static final int RESULT_NET_FAILED = 401;

	public void onSuccess(int requestType, Object response);

	public void onFailed(int requestType, String message);
}
