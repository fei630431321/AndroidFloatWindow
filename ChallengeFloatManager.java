package com.ifreetalk.ftalk.util;

import com.ifreetalk.ftalk.R;
import com.ifreetalk.ftalk.app.ftalkApp;
import com.ifreetalk.ftalk.basestruct.ChallengeStatusInfo;
import com.ifreetalk.ftalk.datacenter.ChallengeManager;
import com.ifreetalk.ftalk.datacenter.HttpPB.ImageLoader;
import com.ifreetalk.ftalk.emotinactionmgr.DownloadMgr;
import com.ifreetalk.ftalk.util.TimeUtil;
import com.ifreetalk.ftalk.views.widgets.ChallengeOutAnimationView;
import com.ifreetalk.ftalk.views.widgets.FloatWindowSmallView;

import android.content.Context;
import android.content.pm.PackageManager;

import com.ifreetalk.ftalk.datacenter.Config;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

// lipengfei
// 2015-10-24
public class ChallengeFloatManager {
	/**
	 * ChallengeDialog 动画
	 */
	private  ChallengeOutAnimationView mChallengeOutAnimation;
	
	/**
	 * 挑战悬浮窗View的实例
	 */
	private  FloatWindowSmallView mChallengeSmallWindow;

	/**
	 * 挑战悬浮窗View的参数
	 */
	private LayoutParams mChallengeFloatWindowParams;

	/**
	 * 挑战列表悬浮窗View的参数
	 */
	private LayoutParams mChallengeAnimationWindowParams;

	/**
	 * 用于控制在屏幕上添加或移除悬浮窗
	 */
	private WindowManager mWindowManager;

	/**
	 * 单例对象
	 */
	private static ChallengeFloatManager mChallengeFloatManager = null;
	
	/**
	 * 屏幕宽度
	 */
	private int screenWidth ;
	
	/**
	 * 屏幕高度
	 */
	private int screenHeight;
	
	/**
	 * 获取单例
	 * @return
	 */
	public static ChallengeFloatManager getInstance(){
		if(mChallengeFloatManager == null){
			mChallengeFloatManager = new ChallengeFloatManager();
		}
		return mChallengeFloatManager;
	}
	
	/**
	 * 是否存在SYSTEM_ALERT_WINDOW 权限
	 * @return
	 */
	private boolean ishavepermission(){
		PackageManager pm = ftalkApp._context.getPackageManager();
		if(pm !=null){
			return PackageManager.PERMISSION_GRANTED ==   
					pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", "com.ifreetalk.ftalk"); 
		}
		return false;
	}
	
	/**
	 * 获取屏幕大小
	 */
	private void getWindowSize(){
		if(screenWidth ==0 || screenHeight== 0){
			WindowManager windowManager = getWindowManager(ftalkApp._context);
			screenWidth = windowManager.getDefaultDisplay().getWidth();
			screenHeight = windowManager.getDefaultDisplay().getHeight();
		}
	}
	
	/**
	 * 创建挑战浮动窗口
	 * @param context
	 * @param userId
	 */
	public void createChallengeAnimation(long userID){
		if(userID <=0 ){
			return ;
		}
		if(mChallengeOutAnimation == null){
			try{
				WindowManager windowManager = getWindowManager(ftalkApp._context);
				getWindowSize();
				mChallengeOutAnimation = new ChallengeOutAnimationView(ftalkApp._context);
				if (mChallengeAnimationWindowParams == null) {
					// 当手机没有获取到系统SYSTEM_ALERT_WINDOW 权限时，Layoutparams.type 为 TYPE_TOAST
					mChallengeAnimationWindowParams = new LayoutParams();
					if(ishavepermission()){
						mChallengeAnimationWindowParams.type = LayoutParams.TYPE_PHONE;
					}else{
						mChallengeAnimationWindowParams.type = LayoutParams.TYPE_TOAST;
					}
					mChallengeAnimationWindowParams.format = PixelFormat.RGBA_8888;
					mChallengeAnimationWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
							| LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
					mChallengeAnimationWindowParams.width = screenWidth;
					mChallengeAnimationWindowParams.height = screenHeight;
				}
				windowManager.addView(mChallengeOutAnimation, mChallengeAnimationWindowParams);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		int[] a = new int[2];
		if(mChallengeSmallWindow != null){
			mChallengeSmallWindow.getLocationOnScreen(a);
			a[1] = a[1] - 2*mChallengeSmallWindow.getStatusBarHeight();
		}else{
			a[0] = 0;
			a[1] = screenHeight*23/128;
		}
		mChallengeOutAnimation.setData(userID,a[0],a[1]);
	}
	/**
	 * remove createChallengeDialog
	 */
	public void removeChallengeAnimation(){
		if(mChallengeOutAnimation != null){
			WindowManager windowManager = getWindowManager(ftalkApp._context);
			windowManager.removeView(mChallengeOutAnimation);
			mChallengeOutAnimation = null;
		}
	}
	/**
	 * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
	 * 
	 * @param context  必须为应用程序的Context.
	 *            
	 */
	private void createChallengeFloatWindow(){
		if (mChallengeSmallWindow == null) {
			try{
				WindowManager windowManager = getWindowManager(ftalkApp._context);
				mChallengeSmallWindow = new FloatWindowSmallView(ftalkApp._context);
				if (mChallengeFloatWindowParams == null) {
					// 当手机没有获取到系统SYSTEM_ALERT_WINDOW 权限时，Layoutparams.type 为 TYPE_TOAST
					mChallengeFloatWindowParams = new LayoutParams();
					getWindowSize();
					if(ishavepermission()){
						mChallengeFloatWindowParams.type = LayoutParams.TYPE_PHONE;
					}else{
						mChallengeFloatWindowParams.type = LayoutParams.TYPE_TOAST;
					}
					mChallengeFloatWindowParams.format = PixelFormat.RGBA_8888;
					mChallengeFloatWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
							| LayoutParams.FLAG_NOT_FOCUSABLE;
					mChallengeFloatWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
					mChallengeFloatWindowParams.width = FloatWindowSmallView.viewWidth;
					mChallengeFloatWindowParams.height = FloatWindowSmallView.viewHeight;
					mChallengeFloatWindowParams.x = 0;
					mChallengeFloatWindowParams.y = screenHeight*23/128 + mChallengeSmallWindow.getStatusBarHeight();
				}
				mChallengeSmallWindow.setParams(mChallengeFloatWindowParams);
				windowManager.addView(mChallengeSmallWindow, mChallengeFloatWindowParams);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 将挑战悬浮窗从屏幕上移除。
	 * 
	 * @param context 必须为应用程序的Context.
	 *            
	 */
	public void removeChallengeFloatWindow() {
		if (mChallengeSmallWindow != null) {
			WindowManager windowManager = getWindowManager(ftalkApp._context);
			windowManager.removeView(mChallengeSmallWindow);
			mChallengeSmallWindow = null;
			mChallengeFloatWindowParams = null;
		}
	}
	public void hideChallengeFloatWindow(){
		if(mChallengeSmallWindow != null){
			if(mChallengeSmallWindow.getVisibility() == View.VISIBLE){
				mChallengeSmallWindow.setVisibility(View.GONE);
			}
		}
	}
	public void showChallengeFloatWindow(){
		createChallengeFloatWindow();
		if(mChallengeSmallWindow != null){
			if(mChallengeSmallWindow.getVisibility() != View.VISIBLE){
				mChallengeSmallWindow.setVisibility(View.VISIBLE);
			}
		}
	}
	
	/**
	 * 更新挑战悬浮窗的TextView上的数据。
	 * @param context 可传入应用程序上下文。
	 *            
	 */
	public void updateChallengeFloatView() {
		ChallengeStatusInfo info = ChallengeManager.getInstance().getChallengeFightStatusInfoTop();
		updateChallengeFloatView(info);
	}
	private void updateChallengeFloatView(ChallengeStatusInfo info) {
		if(info == null){
			return;
		}
		if (mChallengeSmallWindow != null && mChallengeSmallWindow.getVisibility() == View.VISIBLE) {
			long userId = info.getSourceId() == Config.getInstance().getUserID() ? info.getTargetId() : info.getSourceId();
			TextView havetime = (TextView) mChallengeSmallWindow.findViewById(R.id.havetime);
			ImageView user_img =(ImageView) mChallengeSmallWindow.findViewById(R.id.user_img);
			TextView challengeNums = (TextView) mChallengeSmallWindow.findViewById(R.id.challenge_nums);
			setUserImage(userId,user_img);
			havetime.setText(TimeUtil.secToTime(info.getFightTime()));
			challengeNums.setText(String.valueOf(ChallengeManager.getInstance().getChallengeFightStatusInfoSize()));
		}
	}
	
	/**
	 * 设置图像
	 * @param baseInfo   用户基本信息
	 * @param view		   用户图像textView
	 * @param context	  上下文
	 */
	private void setUserImage(long userId,ImageView view) {
		// TODO Auto-generated method stub
		if(userId <= 0 || view == null){
			return;
		}
		//用户头像设置
		if(view.getTag() != null && view.getTag() instanceof Long){
			long user = (Long) view.getTag();
			if(user == userId){//已经设置过了
				return;
			}
		}
		view.setTag(userId);
		String strDownLoadImage = DownloadMgr.CreateDownLoadUserImage(userId);
		ImageLoader.load(strDownLoadImage, view, R.drawable.city_master_l, -1, ftalkApp._context);
	}
	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context 必须为应用程序的Context.
	 *            
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	private WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}
}
