package com.miscell.lucky;

import java.lang.reflect.Method;
import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dotools.fls.setting.onekeyset.OneKeySetManager;

public class MainActivity extends Activity implements OnClickListener {
    private static final Intent sSettingsIntent =
            new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private TextView mAccessibleLabel;
    private TextView mNotificationLabel;
    private TextView mLabelText;
    private Button mOneKeySetBtn;
    
    public static MainActivity sInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	sInstance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mOneKeySetBtn = (Button) findViewById(R.id.button_notification);
        mOneKeySetBtn.setOnClickListener(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float density = metrics.density;
        final int screenWidth = metrics.widthPixels;

        int width = (int) (screenWidth - (density * 12 + .5f) * 2);
        int height = (int) (366.f * width / 1080);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        ImageView imageView1 = (ImageView) findViewById(R.id.image_accessibility);
        ImageView imageView2 = (ImageView) findViewById(R.id.image_notification);

        mAccessibleLabel = (TextView) findViewById(R.id.label_accessible);
        mNotificationLabel = (TextView) findViewById(R.id.label_notification);
        mLabelText = (TextView) findViewById(R.id.label_text);

        imageView1.setLayoutParams(lp);
        imageView2.setLayoutParams(lp);

        if (Build.VERSION.SDK_INT >= 18) {
            imageView2.setVisibility(View.VISIBLE);
            mNotificationLabel.setVisibility(View.VISIBLE);
            findViewById(R.id.button_notification).setVisibility(View.VISIBLE);
        } else {
            imageView2.setVisibility(View.GONE);
            mNotificationLabel.setVisibility(View.GONE);
            findViewById(R.id.button_notification).setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        changeLabelStatus();
       mOneKeySetBtn.setText(OneKeySetManager.getInstance().isGuidesOk() ? "ok" : "set" );
    }

    private void changeLabelStatus() {
        boolean isAccessibilityEnabled = isAccessibleEnabled();
        mAccessibleLabel.setTextColor(isAccessibilityEnabled ? 0xFF009588 : Color.RED);
        mAccessibleLabel.setText(isAccessibleEnabled() ? "辅助功能已打开" : "辅助功能未打开");
        mLabelText.setText(isAccessibilityEnabled ? "好了~你可以去做其他事情了，我会自动给你抢红包的" : "请打开开关开始抢红包");

        if (Build.VERSION.SDK_INT >= 18) {
            boolean isNotificationEnabled = isNotificationEnabled();
            mNotificationLabel.setTextColor(isNotificationEnabled ? 0xFF009588 : Color.RED);
            mNotificationLabel.setText(isNotificationEnabled ? "接收通知已打开" : "接收通知未打开");

            if (isAccessibilityEnabled && isNotificationEnabled) {
                mLabelText.setText("好了~你可以去做其他事情了，我会自动给你抢红包的");
            } else {
                mLabelText.setText("请把两个开关都打开开始抢红包");
            }
        }
    }

    public void onNotificationEnableButtonClicked(View view) {
//    	MonitorService.flag = true;
      //  startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP));
//    	toFloatPage();
//    	Log.i("monitor", hasFloatWindowPermission(this) +"permissioin");
//    	toAutoStartPage();
//    	toUsage();
    	OneKeySetManager.getInstance().doStart();
    }
    
    public void usage(){
    	  startActivity(new Intent().setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$UsageAccessSettingsActivity")));
    }
    
    public void toUsage(View view){
    	usage();
//    	Intent mIntent = new Intent();
//    	mIntent.setAction(Intent.ACTION_MAIN);
//    	mIntent.addCategory(Intent.CATEGORY_HOME);
//    	startActivity(mIntent);
    }
    
    @SuppressLint("NewApi")
	public boolean hasPermission(Context ct){
		AppOpsManager appOps = (AppOpsManager) ct.getSystemService(Context.APP_OPS_SERVICE);
		int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), ct.getPackageName());
		return mode == AppOpsManager.MODE_ALLOWED;
	}
    
    public void toFloat(View view){
    	toFloatPage();
    }
    
    public void toNtf(View view){
    	  startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }
    
    public  boolean hasFloatWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return checkOp(getApplicationContext()) == 1;
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
    
    private void toMeiZuPage(){
    	  Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
          intent.setComponent(new ComponentName("com.meizu.safe","com.meizu.safe.security.AppSecActivity"));
          intent.putExtra("packageName", getPackageName());
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
    }
    
   private void toAutoStartPage(){
	   try{
		   String SCHEME = "package";
	       PackageInfo info = null;
	       PackageManager pm = getPackageManager();
	       info = pm.getPackageInfo(getPackageName(), 0);
	       Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
	       intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
	       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	       intent.putExtra("extra_pkgname", "com.vlocker.locker");
	       startActivity(intent);
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
    
   private void  toFloatPage(){
	   Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
       String SCHEME = "package";
       intent.setClassName("com.miui.securitycenter",
               "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       intent.putExtra("extra_pkgname", getPackageName());
       startActivity(intent);
   }

    public void onSettingsClicked(View view) {
        startActivity(sSettingsIntent);
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

    private boolean isNotificationEnabled() {
        ContentResolver contentResolver = getContentResolver();
        String enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");

        if (!TextUtils.isEmpty(enabledListeners)) {
            return enabledListeners.contains(getPackageName() + "/" + getPackageName() + ".NotificationService");
        } else {
            return false;
        }
    }

    private void showEnableAccessibilityDialog() {
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setTitle("重要!").setMessage("您需要打开\"有红包\"的辅助功能选项才能抢微信红包")
                .setPositiveButton("打开", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(sSettingsIntent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null);
        dialog.show();
    }
   private Handler mhandler = new Handler(){
	   public void handleMessage(android.os.Message msg) {
		   Log.i("android_test", "1111111111111111111111111111111");
		   removeWindow();
	   };
   };

private TextView tv;

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button_notification){
			if(OneKeySetManager.getInstance().isGuidesOk()){
				Toast.makeText(this, "一切ok", 0).show();
			}else{
				OneKeySetManager.getInstance().doStart();
				addWindow();
				mhandler.sendEmptyMessageDelayed(0, 5000);
			}
		}
	}
	
	void addWindow(){
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		 lp.type = WindowManager.LayoutParams.TYPE_PHONE;
         lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
         lp.flags |= LayoutParams.FLAG_DISMISS_KEYGUARD ;
         lp.flags |= LayoutParams.FLAG_FULLSCREEN ;
         lp.flags |= LayoutParams.FLAG_SHOW_WHEN_LOCKED ;
         lp.flags |= LayoutParams.FLAG_LAYOUT_NO_LIMITS ;
         lp.flags |= LayoutParams.FLAG_LAYOUT_IN_SCREEN;
         // lp.format = PixelFormat.TRANSLUCENT;
         lp.format = PixelFormat.RGBA_8888  ;
         lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST;
         lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
         lp.gravity = Gravity.TOP | Gravity.START;
         lp.height = WindowManager.LayoutParams.MATCH_PARENT;
         lp.width = WindowManager.LayoutParams.MATCH_PARENT;
         tv = new TextView(this);
         tv.setBackgroundColor(Color.RED);
         manager.addView(tv, lp);
	}
	
	void removeWindow(){
		if(tv != null && tv.getParent() != null){
			WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			manager.removeView(tv);
		}
	}
}
