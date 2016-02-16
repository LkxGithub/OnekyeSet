package com.dotools.fls.setting.onekeyset;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.miscell.lucky.MonitorService;

public class OneKeySetFloat extends OneKeySetBase{

	@Override
	public void doSet(AccessibilityNodeInfo node) {  
		if(!hasPermission(OneKeySetApplication.getAppContext())){
			doScroll(node);
			if(mStep == 0){
				traverseNode(node, "显示悬浮窗");
			}else if(mStep == 1){
				traverseNode(node, "允许");
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
		 Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
	       String SCHEME = "package";
	       intent.setClassName("com.miui.securitycenter",
	               "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
	       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	       intent.putExtra("extra_pkgname",OneKeySetApplication.getAppContext().getPackageName());
	       OneKeySetApplication.getAppContext().startActivity(intent);
	}

	@Override
	public void performAction(AccessibilityNodeInfo node, String s) {
		AccessibilityNodeInfo targetNode;
		if(s.equals("显示悬浮窗")){
			Log.i("monitor", "find 显示悬浮窗");
			targetNode = node.getParent();
			if(targetNode != null && mStep == 0){
				Log.i("monitor",targetNode.getPackageName().toString() + "mStep "+mStep);
				targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				mStep++;
			}
		}else if(s.equals("允许")){
			
			targetNode = node;
			if(targetNode != null && mStep == 1){
				targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				mStep++;
			}
		}
	}
	 
	   @Override
	   public  boolean hasPermission(Context context) {
	            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	                return checkOp(OneKeySetApplication.getAppContext()) == 1;
	            }
	            ApplicationInfo applicationInfo;
	            try {
	                applicationInfo = context.getPackageManager().getApplicationInfo(
	                        context.getPackageName(), 0);
	                return ((applicationInfo.flags & 1 << 27) == 134217728);
	            } catch (NameNotFoundException e) {
	                e.printStackTrace();
	                return false;
	            }
	    }

	    public  int checkOp(Context context) {
	        try {
	            Object localObject = context.getSystemService("appops");
	            Class localClass = localObject.getClass();
	            Class[] arrayOfClass = new Class[3];
	            arrayOfClass[0] = Integer.TYPE;
	            arrayOfClass[1] = Integer.TYPE;
	            arrayOfClass[2] = String.class;
	            Method localMethod = localClass.getMethod("checkOp", arrayOfClass);
	            Object[] arrayOfObject = new Object[3];
	            arrayOfObject[0] = Integer.valueOf(24);
	            arrayOfObject[1] = Integer.valueOf(Binder.getCallingUid());
	            arrayOfObject[2] = context.getPackageName();
	            int j = ((Integer) localMethod.invoke(localObject, arrayOfObject)).intValue();
	            if (j == 0)
	                return 1;
	            return 0;
	        } catch (Throwable localThrowable2) {
	            return -1;
	        }
	    }

}
