package com.dotools.fls.setting.onekeyset;

import android.app.Application;


public class OneKeySetApplication extends Application{
	private static OneKeySetApplication sApplication;
	@Override
	public void onCreate() {
		super.onCreate();
		OneKeySetManager.init();
		sApplication = this;
	}
	
	public static Application  getAppContext(){
		return sApplication;
	}
}
