/**
 * @file BaseFragment.java 
 * @package com.qin.ct.cartoonlike.fragment
 * @description 
 * @date 2013-9-23
 * @version V1.0
 *
 * @author Chuck 
 * @email chucktsao@126.com
 */
package org.ct.android.app;

import java.lang.reflect.Field;

import org.ct.java.annotation.ViewInject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class AbstratFragment extends Fragment implements
		OnClickListener {

	public static final String TAG = "AbstratFragment";

	/** Standard activity result: operation canceled. */
	public static final int RESULT_CANCELED = 0;
	/** Standard activity result: operation succeeded. */
	public static final int RESULT_OK = -1;
	/** Start of user-defined activity results. */
	public static final int RESULT_FIRST_USER = 1;

	private int mLayoutId = 0;

	protected Handler mHandler;

	public AbstratFragment() {
		mLayoutId = getContentView();
	}

	protected abstract int getContentView();

	protected abstract void initView(View v);

	public abstract void onInnerHandlerMessage(Message msg);

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.d(TAG, "onCreateView");
		View v = inflater.inflate(mLayoutId, container, false);
		findView(v);
		initView(v);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.d(TAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "onDetach");
		super.onDetach();
	}

	private final void findView(View v) {
		Field[] fields = getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				ViewInject viewInject = field.getAnnotation(ViewInject.class);
				if (viewInject != null) {
					try {
						field.setAccessible(true);
						field.set(this, v.findViewById(viewInject.id()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/* 消息 */
	public void showToast(String str, int duration) {
		FragmentActivity activity = this.getActivity();
		if (null == activity)
			return;
		Toast.makeText(activity.getApplicationContext(), str, duration).show();
	}

	public void showToast(String str, int duration, boolean isAppLife) {
		FragmentActivity activity = this.getActivity();
		if (null == activity)
			return;
		if (isAppLife) {
			Toast.makeText(activity.getApplicationContext(), str, duration)
					.show();
		} else {
			Toast.makeText(activity, str, duration).show();
		}
	}

	/* 弹出框 */
	private Dialog mYesNoDialog;// 确定取消对话框
	private Dialog mAlertDialog; // 警告对话框

	/**
	 * 取消弹出的对话框 dialog
	 * 
	 * @param dialog
	 */
	protected void dismissDialog(Dialog dialog) {
		if (dialog != null) {
			dialog.setOnDismissListener(null);
			dialog.dismiss();
		}
	}

	/**
	 * 确认取消对话框
	 * 
	 * @param msg
	 *            显示的消框
	 * @param posListener
	 *            确认监听
	 * @param negListener
	 *            取消监听
	 * @param canListener
	 *            关闭对话框监框
	 */
	protected void showSelectDialog(String content, String title, String yes,
			String no, DialogInterface.OnClickListener yesListener,
			DialogInterface.OnClickListener noListener,
			DialogInterface.OnCancelListener cancelListener,
			DialogInterface.OnDismissListener dismissListener) {
		dismissDialog(mYesNoDialog);
		FragmentActivity activity = this.getActivity();
		if (null == activity)
			return;
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(content);
		builder.setTitle(title);
		builder.setPositiveButton(yes, yesListener);
		builder.setNegativeButton(no, noListener);
		mYesNoDialog = builder.create();
		mYesNoDialog.setCanceledOnTouchOutside(false);
		mYesNoDialog.setOnCancelListener(cancelListener);
		mYesNoDialog.setOnDismissListener(dismissListener);
		mYesNoDialog.show();
	}

	/**
	 * 显示警告对话框
	 * 
	 * @param msg
	 *            显示的消框
	 * @param title
	 *            标题
	 * @param posListener
	 *            确定监听
	 * @param canListener
	 *            关闭监听
	 * @param disListener
	 *            解散监听
	 */
	protected void showAlertDialog(String content, String title,
			DialogInterface.OnClickListener sureListener,
			DialogInterface.OnCancelListener cancelListener,
			DialogInterface.OnDismissListener dismissListener) {
		dismissDialog(mAlertDialog);
		FragmentActivity activity = this.getActivity();
		if (null == activity)
			return;
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(content);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setPositiveButton(android.R.string.ok, sureListener);
		mAlertDialog = builder.create();
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.setOnCancelListener(cancelListener);
		mAlertDialog.setOnDismissListener(dismissListener);
		mAlertDialog.show();
	}

	public void sendMessage(int what, Object obj) {
		if (null != mHandler) {
			Message msg = new Message();
			msg.what = what;
			msg.obj = obj;
			mHandler.sendMessage(msg);
		}
	}

	
}
