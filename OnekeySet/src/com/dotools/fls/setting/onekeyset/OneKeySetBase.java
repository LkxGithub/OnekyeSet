package com.dotools.fls.setting.onekeyset;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.miscell.lucky.MonitorService;

public abstract class OneKeySetBase {
	public  int mStep;
	public  boolean mHasBack;
	public boolean start;
	public Handler mHandler = new Handler();
	public AccessibilityNodeInfo mReceiveInfo;
	
	public abstract void doSet(AccessibilityNodeInfo node);
	public abstract void toSetPage(Context ct);
	public abstract  void performAction(AccessibilityNodeInfo node,String s) ;
	public abstract boolean hasPermission(Context ct);
	
	public void resetStatus(){
		mStep = 0;
		mHasBack = false;
	}
	
	public  void performGlobalBack(){
		MonitorService service = MonitorService.getService();
		Log.i("monitor", "service "+(service == null));
		if(service != null){
			boolean result = service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    		Log.i("monitor", result+" global");
		}
	}
	
	public  void doScroll(AccessibilityNodeInfo node) {
		if(node != null && mStep == 0){
			for(int i = 0; i < node.getChildCount(); i++){
				AccessibilityNodeInfo child = node.getChild(i);
				Log.i("monitor", "child " +child.getClassName().toString());
				if(child.getClassName().toString().equals("android.widget.ListView")){
					child.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
				}
			}
		}
	}
	
	public  void traverseNode(AccessibilityNodeInfo node,String s) {
		if (null == node)
			return ;
		final int count = node.getChildCount();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				AccessibilityNodeInfo childNode = node.getChild(i);
				traverseNode(childNode,s);
			}
		} else {
			CharSequence text = node.getText();
			if (null != text && text.length() > 0) {
				if (text.toString().contains(s)) {
					Log.i("android_test", "hasfind" +s);
					mReceiveInfo = node;
					performAction(mReceiveInfo,s);
				}
			}
		}
	}
}
