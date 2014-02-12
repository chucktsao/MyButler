package org.ct.android.app;

/**
 * @file CtActivity.java 
 * @package org.ct.android
 * @create 2013-9-2
 * @author Chuck 
 * @email chucktsao@126.com
 * @description 
 */

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import org.ct.java.annotation.ViewInject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

public abstract class AbstractActivity extends Activity implements OnClickListener{
	
	protected abstract void onCreate(Bundle savedInstanceState);
	
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

	abstract protected void initView();

	protected InnerHandler mHandler = null;

	public static class InnerHandler extends Handler {
		WeakReference<Activity> mReference;

		public InnerHandler(AbstractActivity activity) {
			mReference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			AbstractActivity activity = (AbstractActivity) mReference.get();
			if (null != activity) {
				activity.onInnerHandlerMessage(msg);
			}
		};
	}

	abstract protected void onInnerHandlerMessage(Message msg);
	
	//系统弹出模块
	
	protected void showToast(String str,int duration){
		Toast.makeText(this, str, duration).show();	
	}
	
	
	protected void showToast(String str,int duration,boolean isAppLife){
		if(isAppLife){
			Toast.makeText(this.getApplicationContext(), str, duration).show();	
		}else{
			Toast.makeText(this, str, duration).show();	
		}
	}
	


	
	private Dialog mOkCancelDialog;// 确定取消对话框

	private Dialog mAlertDialog;	// 警告对话框
	

	/**
	 * 取消弹出的对话框 dialog
	 * 
	 * @param dialog
	 */
	public void dismissDialog(Dialog dialog) {
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
	public void showDoubleButtonDialog(String msg, String title, String ok,
			String cancel, DialogInterface.OnClickListener posListener,
			DialogInterface.OnClickListener negListener,
			DialogInterface.OnCancelListener canListener,
			DialogInterface.OnDismissListener disListener) {
		dismissDialog(mOkCancelDialog);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton(ok, posListener);
		builder.setNegativeButton(cancel, negListener);
		mOkCancelDialog = builder.create();
		mOkCancelDialog.setCanceledOnTouchOutside(false);
		mOkCancelDialog.setOnCancelListener(canListener);
		mOkCancelDialog.setOnDismissListener(disListener);
		mOkCancelDialog.show();
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
	public void showAlertDialog(String msg, String title,
			DialogInterface.OnClickListener posListener,
			DialogInterface.OnCancelListener canListener,
			DialogInterface.OnDismissListener disListener) {
		dismissDialog(mAlertDialog);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(title);
		builder.setPositiveButton(android.R.string.ok, posListener);
		mAlertDialog = builder.create();
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.setOnCancelListener(canListener);
		mAlertDialog.setOnDismissListener(disListener);
		mAlertDialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	public void sendMessage(int what,Object obj){
		if(null!=mHandler){
			Message msg = mHandler.obtainMessage(what);
			msg.obj = obj;
			msg.sendToTarget();
		}
	}

}
