/**
 * @file MainFragmentActivity.java 
 * @package com.chaore.doctor.activity
 * @description 
 * @date 2013-11-23
 * @version V1.0
 *
 * @author Chuck 
 * @email chucktsao@126.com
 */
package org.ct.android.app;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import org.ct.java.annotation.ViewInject;




import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public abstract class AbstratFragmentActivity extends FragmentActivity
		implements OnClickListener {

	static final String TAG = "MainFragmentActivity";
	public static final String EXTRA_FRAGMENT = "fragment";

	protected InnerHandler mHandler = null;
	private AbstratFragment mFragment;// 当前的Fragment

	protected abstract void onCreate(Bundle savedInstanceState);

	protected abstract void initView();

	protected abstract int getFragmentResId();

	public void onCreate(Bundle savedInstanceState, int resId) {
		super.onCreate(savedInstanceState);
		mHandler = new InnerHandler(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(resId);
		findView();
		initView();
	}

	private final void findView() {
		Field[] fields = getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				ViewInject viewInject = field.getAnnotation(ViewInject.class);
				if (viewInject != null) {
					try {
						field.setAccessible(true);
						field.set(this, findViewById(viewInject.id()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	public static class InnerHandler extends Handler {
		WeakReference<Activity> mReference;

		public InnerHandler(AbstratFragmentActivity activity) {
			mReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			AbstratFragmentActivity activity = (AbstratFragmentActivity) mReference
					.get();
			if (null != activity) {
				activity.onInnerHandlerMessage(msg);
			}
		};
	}

	protected void onInnerHandlerMessage(Message msg) {
		if (null != mFragment) {
			mFragment.onInnerHandlerMessage(msg);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		if (null != mFragment) {
			mFragment.onClick(v);
		}

	}

	public void replaceFragment(Class<?> c) {
		replaceFragment(c, null);
	}

	public void replaceFragment(Class<?> c, Bundle bundle) {
		if (mFragment == null || mFragment.getClass() != c) {
			try {
				mFragment = (AbstratFragment) c.newInstance();
				mFragment.setHandler(mHandler);
				FragmentActivity activity = this;
				FragmentManager fragmentManager = activity
						.getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(getFragmentResId(), mFragment);
				if (null != bundle)
					mFragment.setArguments(bundle);
				fragmentTransaction.commit();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}

	}

	public void removeFragment() {
		if (mFragment != null) {
			FragmentActivity activity = this;
			FragmentManager fragmentManager = activity
					.getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.remove(mFragment);
			fragmentTransaction.commit();
			mFragment = null;
		}
	}

}
