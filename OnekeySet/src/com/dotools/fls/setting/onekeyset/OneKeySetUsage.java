package com.dotools.fls.setting.onekeyset;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.accessibility.AccessibilityNodeInfo;

public class OneKeySetUsage extends OneKeySetBase{

	@Override
	public void doSet(AccessibilityNodeInfo node) {
		if(!hasPermission(OneKeySetApplication.getAppContext())){
			Log.i("monitor", "do usage");
			Log.i("monitor", "mStep "+mStep);
			doScroll(node);
			if(mStep == 0){
				traverseNode(node, "有红包");
			}
		}else{
			if(!mHasBack){
				mHasBack = true;
				performGlobalBack();
				OneKeySetManager.getInstance().doNext();
			}
		}
	}

	@Override
	public void toSetPage(Context ct) {
		Intent intent = new Intent().setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$UsageAccessSettingsActivity"));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ct.startActivity(intent);
	}

	@Override
	public void performAction(AccessibilityNodeInfo node, String s) {
		if(node !=  null && !TextUtils.isEmpty(s)){
			if("有红包".equals(s) && mStep == 0){
				Log.i("monitor", "find有红包");
				AccessibilityNodeInfo targetNode = node.getParent();
				if(targetNode != null){
					targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					mStep++;
				}
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	@SuppressLint("NewApi")
	public boolean hasPermission(Context ct){
		AppOpsManager appOps = (AppOpsManager) ct.getSystemService(Context.APP_OPS_SERVICE);
		int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), ct.getPackageName());
		return mode == AppOpsManager.MODE_ALLOWED;
	}

}
