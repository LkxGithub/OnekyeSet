package com.dotools.fls.setting.onekeyset;
import com.miscell.lucky.MonitorService;
import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

public class OneKeySetNtf extends OneKeySetBase{ 
	@Override
	public void doSet(AccessibilityNodeInfo node) {
		Log.i("monitor", "doset ntf");
		Log.i("monitor",hasPermission(OneKeySetApplication.getAppContext()) + "");
		if(!hasPermission(OneKeySetApplication.getAppContext()) ){
 			doScroll(node);
 			Log.i("monitor", mStep+""); 
			if(mStep == 0){
				traverseNode(node, "有红包");
			}else if(mStep == 1){
				traverseNode(node, "确认");
			}
		}else{
			if(!mHasBack){
				performGlobalBack();
				mHasBack = true;
				OneKeySetManager.getInstance().doNext();
			}
		}
		
	}

	@Override
	public void performAction(AccessibilityNodeInfo node,String s) {
		Log.i("android_test", (node == null)+"perform action  node is  nulll nodetext" +node.getText().toString() + "mStep "+mStep);
		AccessibilityNodeInfo targetNode = null;
		if(s.contains("有红包")){
			Log.i("android_test", "constains 有红包");
			targetNode = node.getParent(); 
			if(targetNode != null && mStep == 0){
				Log.i("android_test", "有红包　perform click");
				mStep++;
				targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		}else if(s.contains("确认")){
			Log.i("android_test", "find 确认　　"+mStep);
			targetNode = node;
			if(targetNode != null && mStep == 1){
				mStep++;
				targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		}
	}

	@Override
	public void toSetPage(Context ct) {
		openGetNotificationPermissionPage(ct);
		start = false;
	}

    private void openGetNotificationPermissionPage(Context ct) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                Intent intent = new Intent();
                intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ct.startActivity(intent);
            } catch (Exception e) {
               openAccessibility2(ct);
            }
            
        } else {
            try{
                Intent notfication_Intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                notfication_Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ct.startActivity(notfication_Intent); 
            }catch(Exception e){
                openNtfListener2(ct);
            }
           
        }
        
    }
    
    private void openAccessibility2(Context ct){
        try {
            ComponentName cm;
            Intent intent = new Intent("/");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cm = new ComponentName("com.android.settings","com.android.settings.Settings$AccessibilitySettingsActivity");
            intent.setComponent(cm);
            ct.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void openNtfListener2(Context ct){
        try {
            ComponentName cm;
            Intent intent = new Intent("/");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cm = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
            intent.setComponent(cm);
            ct.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public  boolean hasPermission(Context ct) {
		  String enabled_notification_listeners = Settings.Secure.getString(ct.getContentResolver(),"enabled_notification_listeners");
        if (enabled_notification_listeners != null
                && enabled_notification_listeners.contains(ct.getPackageName())) {
            return true;
        }
        return false;
  }
}
