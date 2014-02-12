/**
 * @file ResManager.java 
 * @package org.ct.plugin
 * @description 
 * @date 2013-12-13
 * @version V1.0
 *
 * @author Chuck 
 * @email chucktsao@126.com
 */
package org.ct.plugin;



import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class ResManager {
	static final String TAG = "ResManager";

	private String mPackageName = null;
	private Resources mRes = null;
	
	private static ResManager sInstance;

	private ResManager(Context context) {
		mPackageName = context.getPackageName();
    	mRes = context.getApplicationContext().getResources();
	}

	public static ResManager getInstance(Context context) {
		if(null==sInstance){
			sInstance = new ResManager(context);
		}
		return sInstance;
	}
//	
//	 /**
//     * 设置应用Context
//     * @param context
//     */
//    public void setAppContext(Context context) {
//    	mPackageName = context.getPackageName();
//    	mRes = context.getResources();
//    }
    

	public int getResId(String name, String type) {
		int resid = 0;
		if (mRes != null) {
			resid = mRes.getIdentifier(name, type, mPackageName);
		}
		if (resid == 0) {
			Log.e(TAG, "res not found:/" + type + "/" + name);
		}
		return resid;
	}
}
