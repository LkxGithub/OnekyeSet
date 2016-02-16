package com.dotools.fls.setting.onekeyset;

import java.util.ArrayList;

import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.miscell.lucky.MonitorService;

public class OneKeySetManager {
	private OneKeySetManager(){}
	private static OneKeySetManager sInstance;
	public static OneKeySetManager getInstance(){
		if(sInstance == null){
			init();
		}
		return sInstance;
	}
	
	public static void init(){
		sInstance = new OneKeySetManager();
		sInstance.initGuides();
	}
	
	public ArrayList<OneKeySetBase> mInitGuides = new ArrayList<OneKeySetBase>();
	public ArrayList<OneKeySetBase> mCurrentGuides = new ArrayList<OneKeySetBase>();
	
	public OneKeySetNtf ntf ;
	public OneKeySetFloat set_float ;
	public OneKeySetUsage set_usage ;
	
	public void initGuides(){
		ntf = new OneKeySetNtf();
		set_float = new OneKeySetFloat();
		if(Build.VERSION.SDK_INT >= 21){
			set_usage = new OneKeySetUsage();
			mInitGuides.add(set_usage);
		}
		mInitGuides.add(ntf);
		//mInitGuides.add(set_float);
		mCurrentGuides.addAll(mInitGuides);
	}
	
	public void refreshGuides(){
		if(!isCurrentSetting()){
			mCurrentGuides.clear();
			for(int i = 0; i < mInitGuides.size(); i++){
				OneKeySetBase item = mInitGuides.get(i);
				if(!item.hasPermission(OneKeySetApplication.getAppContext())){
					mCurrentGuides.add(item);
				}
			}
		}
	}
	
	public boolean  isGuidesOk(){
		refreshGuides();
		return mCurrentGuides.size() == 0;
	} 
	
	private int mCurentIndex =-1;
	public void doNext(){
		mCurentIndex++;
		if(mCurentIndex < mCurrentGuides.size()){
			mCurrentGuides.get(mCurentIndex).toSetPage(OneKeySetApplication.getAppContext());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			setCurrentSetting(false);
		}
	}
	
	public void resetStatus(){
		setCurrentSetting(true);
		mCurentIndex = -1;
		for(int i =0 ; i < mCurrentGuides.size(); i++){
			mCurrentGuides.get(i).resetStatus();
		}
	}
	public void doStart(){
		if(!isGuidesOk()){
			resetStatus();
			doNext();
		}
	}
	
	private boolean isCurrentSetting;
	public boolean isCurrentSetting(){
		return isCurrentSetting;
	}
	public void setCurrentSetting(boolean currentSetting){
		isCurrentSetting = currentSetting;
	}
	
	public void doSet(AccessibilityNodeInfo node){
		Log.i("monitor", "mCurrentIndex "+mCurentIndex);
		if(mCurentIndex < mCurrentGuides.size()){
			mCurrentGuides.get(mCurentIndex).doSet(node);
		}
	}
}
