package com.miscell.lucky;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.dotools.fls.setting.onekeyset.OneKeySetManager;
import com.dotools.fls.setting.onekeyset.OneKeySetNtf;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.KeyguardManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

/**
 * Created by chenjishi on 15/2/12.
 */
public class MonitorService extends AccessibilityService {
    private ArrayList<AccessibilityNodeInfo> mNodeInfoList = new ArrayList<AccessibilityNodeInfo>();

    private boolean mLuckyClicked;
    private boolean mContainsLucky;
    private boolean mContainsOpenLucky;
    private static MonitorService sInstance ;
    
    public static MonitorService getService(){
    	return sInstance;
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	Log.i("monitor","create");
    	sInstance = this;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.i("monitor","destroy");
    }
    
    @Override
    protected void onServiceConnected() {
    	super.onServiceConnected();
    	AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        info.loadDescription(getPackageManager());
        setServiceInfo(info);
    	Log.i("monitor","connected");
    }
	
	
	private boolean isAccessibleEnabled() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals(getPackageName() + "/.MonitorService")) {
                return true;
            }
        }
        return false;
    }
	
	private void openNtf() {
	        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	
	private boolean isNotificationEnabled() {
		  String enabled_notification_listeners = Settings.Secure.getString(getContentResolver(),
                  "enabled_notification_listeners");
          if (enabled_notification_listeners != null
                  && enabled_notification_listeners.contains("com.dotools.flashlockscreen")) {
              return true;
          }
          return false;
    }
	
	private void traverseNode(AccessibilityNodeInfo node) {
		if (null == node)
			return;
		final int count = node.getChildCount();
		if (count > 0) {
			Log.i("monitor", node.getPackageName().toString());
			for (int i = 0; i < count; i++) {
				AccessibilityNodeInfo childNode = node.getChild(i);
				traverseNode(childNode);
			}
		} else {
			CharSequence text = node.getText();
			if (null != text && text.length() > 0) {
				String str = text.toString();
				if (str.contains("悬浮窗")) {
					node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//					Log.i("monitor", "hasFind视频通话");
//					Log.i("monitor",node.getClassName().toString());
//					Log.i("monitor",node.getParent().getClassName().toString());
//					Log.i("monitor", node.getParent().getChildCount()+"");
//					Log.i("monitor", node.getParent().getChild(0).getClassName() + "0 "+ node.getParent().getChild(1).getClassName()+ "1");
//					node.getParent().getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//					node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//					node.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD	);
//					if(node.getParent().getParent() != null){
//						Log.i("monitor", node.getParent().getParent().getClassName().toString());
//					}
				//	Log.i("monitortest", "hasfind  lock");
//						node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
				///	node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD); 
				}
//				if(str.contains("确认") && !isNotificationEnabled() || str.contains("确定")){
//					Log.i("monitortest", "hasfind  conform");
//					node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//				}
//				if(isNotificationEnabled() && !flag){
//					flag = true;
//					performGlobalAction(GLOBAL_ACTION_BACK);
//				}
			}
		}
	}
	
	
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        if(getEventStr(event) != null){
        	Log.i("monitor", getEventStr(event) + "                             "+event.getPackageName());
        }
        
//        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
//        	AccessibilityNodeInfo node = getRootInActiveWindow();
        	AccessibilityNodeInfo node = event.getSource();
//        	Log.i("monitor", OneKeySetManager.getInstance().isCurrentSetting()+"current setting");
        	if(node !=null && OneKeySetManager.getInstance().isCurrentSetting()){
        	OneKeySetManager.getInstance().doSet(node);
//        	if(node != null){
//        		traverseNode(node);
//        	}
      }
//        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
//            unlockScreen();
//            mLuckyClicked = false;
//
//            /**
//             * for API >= 18, we use NotificationListenerService to detect the notifications
//             * below API_18 we use AccessibilityService to detect
//             */
//
//            if (Build.VERSION.SDK_INT < 18) {
//                Notification notification = (Notification) event.getParcelableData();
//                List<String> textList = getText(notification);
//                if (null != textList && textList.size() > 0) {
//                    for (String text : textList) {
//                        if (!TextUtils.isEmpty(text) && text.contains("[微信红包]")) {
//                            final PendingIntent pendingIntent = notification.contentIntent;
//                            try {
//                                pendingIntent.send();
//                            } catch (PendingIntent.CanceledException e) {
//                            }
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//
//        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            AccessibilityNodeInfo nodeInfo = event.getSource();
//
//            if (null != nodeInfo) {
//                mNodeInfoList.clear();
//                traverseNode(nodeInfo);
//                if (mContainsLucky && !mLuckyClicked) {
//                    int size = mNodeInfoList.size();
//                    if (size > 0) {
//                        /** step1: get the last hongbao cell to fire click action */
//                        AccessibilityNodeInfo cellNode = mNodeInfoList.get(size - 1);
//                        cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        mContainsLucky = false;
//                        mLuckyClicked = true;
//                    }
//                }
//                if (mContainsOpenLucky) {
//                    int size = mNodeInfoList.size();
//                    if (size > 0) {
//                        /** step2: when hongbao clicked we need to open it, so fire click action */
//                        AccessibilityNodeInfo cellNode = mNodeInfoList.get(size - 1);
//                        cellNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        mContainsOpenLucky = false;
//                    }
//                }
//            }
//        }
    }
    
    private void unlockScreen() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
        keyguardLock.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");

        wakeLock.acquire();
    }

//    private void traverseNode(AccessibilityNodeInfo node) {
//        if (null == node) return;
//
//        final int count = node.getChildCount();
//        if (count > 0) {
//            for (int i = 0; i < count; i++) {
//                AccessibilityNodeInfo childNode = node.getChild(i);
//                traverseNode(childNode);
//            }
//        } else {
//            CharSequence text = node.getText();
//            if (null != text && text.length() > 0) {
//                String str = text.toString();
//                if (str.contains("领取红包")) {
//                    mContainsLucky = true;
//                    AccessibilityNodeInfo cellNode = node.getParent().getParent().getParent();
//                    if (null != cellNode) mNodeInfoList.add(cellNode);
//                }
//
//                if (str.contains("拆红包")) {
//                    mContainsOpenLucky = true;
//                    mNodeInfoList.add(node);
//                }
//            }
//        }
//    }

    public List<String> getText(Notification notification) {
        if (null == notification) return null;

        RemoteViews views = notification.bigContentView;
        if (views == null) views = notification.contentView;
        if (views == null) return null;

        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        List<String> text = new ArrayList<String>();
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);

            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);

                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) continue;

                // View ID
                parcel.readInt();

                String methodName = parcel.readString();
                if (null == methodName) {
                    continue;
                } else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();

                    // Store the actual string
                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    text.add(t);
                }
                parcel.recycle();
            }
        } catch (Exception e) {
        }

        return text;
    }
    
    private String getEventStr(AccessibilityEvent event){
    	switch (event.getEventType()) {
		case AccessibilityEvent.TYPE_VIEW_CLICKED:
			return "TYPE_VIEW_CLICKED";
		case AccessibilityEvent.TYPE_VIEW_FOCUSED:
			return "TYPE_VIEW_FOCUSED";
		case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
			return "TYPE_VIEW_LONG_CLICKED";
		case AccessibilityEvent.TYPE_VIEW_SELECTED:
			return "TYPE_VIEW_SELECTED";
		case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
			return"TYPE_VIEW_TEXT_CHANGED";
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			return "TYPE_WINDOW_STATE_CHANGED";
		case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
			return "TYPE_NOTIFICATION_STATE_CHANGED";
		case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
			return "TYPE_TOUCH_EXPLORATION_GESTURE_END";
		case AccessibilityEvent.TYPE_ANNOUNCEMENT:
			return "TYPE_ANNOUNCEMENT";
		case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
			return "TYPE_TOUCH_EXPLORATION_GESTURE_START";
		case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
			return "TYPE_VIEW_HOVER_ENTER";
		case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
			return"TYPE_VIEW_HOVER_EXIT";
		case AccessibilityEvent.TYPE_VIEW_SCROLLED:
			return "TYPE_VIEW_SCROLLED";
		case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
			return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			return "TYPE_WINDOW_CONTENT_CHANGED";
		}
    	return null;
    }

    @Override
    public void onInterrupt() {

    }
}
