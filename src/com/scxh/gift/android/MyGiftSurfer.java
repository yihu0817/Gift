package com.scxh.gift.android;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

public class MyGiftSurfer extends SurfaceView implements SurfaceHolder.Callback{
	
	static boolean initSurface = false;

	GiftActivity mActivity;
	int screenWidth, screenHeight;
	Paint mPaint;
	MyDrawThread mDrawThread;
	
	int orgCount = 0;
	public int count = 0;
	Bitmap[] mBalloonBitmaps;
	Bitmap[] mBoxBitmaps;
	Gift[] mShowGifts;	//三
	List<Gift> mAllGiftList;
	
	public static int currentCount = 0;	//当前显示all count
	public int currentIndex = 0;	//当前显示number
	
	public boolean drawBalloons = true;
	
	public static boolean mCanGet = true;
	
	Bitmap[] mCloudBitmaps;
	Cloud[] mClouds;
	
	Bitmap mBackBitmap;
	
	public MyGiftSurfer(Context context) {
		super(context);
		getHolder().addCallback(MyGiftSurfer.this);
		this.mActivity = (GiftActivity) context;
		mCanGet = true;
	}

	public MyGiftSurfer(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(MyGiftSurfer.this);
		this.mActivity = (GiftActivity) context;
		mCanGet = true;
	}

	public MyGiftSurfer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		getHolder().addCallback(MyGiftSurfer.this);
		this.mActivity = (GiftActivity) context;
		mCanGet = true;
	}
	
	public void setCount(int orgCount){
		this.count = orgCount;
		this.orgCount = orgCount;
	}
	
	public void initBitmap(){
		mBackBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gift_new_bg);
//		mBackBitmap = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.gift_new_bg));
		mBalloonBitmaps = new Bitmap[]{
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_0),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_1),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_2),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_3),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_4),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_5),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_6),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_7),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_8),
			BitmapFactory.decodeResource(getResources(), R.drawable.balloon_9)
		};
		mBoxBitmaps = new Bitmap[]{
			BitmapFactory.decodeResource(getResources(), R.drawable.box_0),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_1),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_2),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_3),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_4),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_5),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_6),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_7),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_8),
			BitmapFactory.decodeResource(getResources(), R.drawable.box_9)
		};
		mCloudBitmaps = new Bitmap[]{
				BitmapFactory.decodeResource(getResources(), R.drawable.cloud_1),
				BitmapFactory.decodeResource(getResources(), R.drawable.cloud_2),
				BitmapFactory.decodeResource(getResources(), R.drawable.cloud_3)
		};
	}
	
	public void init(int count){
		currentIndex = 0;	//这个导致加载出错
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		
		//改用list
		mAllGiftList = new ArrayList<Gift>();
		for(int i = 0; i < count; i ++){
			Gift gift = new Gift(mBalloonBitmaps[i], mBoxBitmaps[i], screenWidth, screenHeight);
			mAllGiftList.add(gift);
		}
		//初始化需要显示在屏幕上的宝箱
		initShowGift();
		
		//初始化云……
		initCloud();
		
		mDrawThread = new MyDrawThread(MyGiftSurfer.this);
		mDrawThread.draw = true;
		mDrawThread.start();
		
	}
	public void initShowGift(){
		currentCount = 0;	//显示起始标志清0
		
//		if(count > 3){
//			mShowGifts = new Gift[3];
//		}else{
//			mShowGifts = new Gift[count];
//		}
		mShowGifts = new Gift[count];
		
		for(int i = 0; i < mShowGifts.length; i ++){
			
			mShowGifts[i] = mAllGiftList.get(currentIndex + i);
			
		}
		count -= mShowGifts.length;
		
		if(count == 0){
			//循环完一圈了……
			count = orgCount;
		}
		
		//暂重定位坐标
		for(int i = 0; i < mShowGifts.length; i ++){
			switch(i){
			case 0:
				mShowGifts[i].balloonCoord.x = screenWidth / 2 + screenWidth / 6;
				mShowGifts[i].balloonCoord.y = screenHeight / 3;
				mShowGifts[i].moveSpeedX = 0.8f + new Random().nextFloat();
				mShowGifts[i].moveSpeedY = 2.0f - new Random().nextFloat();
				break;
			case 1:
				mShowGifts[i].balloonCoord.x = screenWidth / 2 - screenWidth / 6;
				mShowGifts[i].balloonCoord.y = screenHeight / 11;
				mShowGifts[i].moveSpeedX = 0.8f + new Random().nextFloat();
				mShowGifts[i].moveSpeedY = 1.5f - new Random().nextFloat();
				break;
			case 2:
				mShowGifts[i].balloonCoord.x = mShowGifts[i-1].balloonCoord.x + screenWidth / 4;
				mShowGifts[i].balloonCoord.y = screenHeight / 13;
				mShowGifts[i].moveSpeedX = 0.4f + new Random().nextFloat();
				mShowGifts[i].moveSpeedY = 2.0f - new Random().nextFloat();
				break;
			}
		}
	}
	
	public void initCloud(){
		mClouds = new Cloud[3];
		mClouds[0] = new Cloud(mCloudBitmaps[0], screenWidth/4, screenHeight/4, screenWidth, screenHeight);
		mClouds[1] = new Cloud(mCloudBitmaps[1], screenWidth-mCloudBitmaps[1].getWidth(), screenHeight/2, screenWidth, screenHeight);
		mClouds[2] = new Cloud(mCloudBitmaps[2], 0, screenHeight*3/5, screenWidth, screenHeight);
	}
	

	public void doDraw(Canvas canvas, float degree){
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		
		drawBack(canvas);
		
		drawClouds(canvas);
		
		if(drawBalloons)
			drawGifts(canvas, degree);
	}
	
	private void drawBack(Canvas canvas){
//		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		
		canvas.drawBitmap(mBackBitmap, null, new Rect(0, 0, screenWidth, screenHeight), mPaint);
		
//		canvas.drawColor(Color.WHITE);
	}
	
	private void drawClouds(Canvas canvas){
		for(int i = 0; i < mClouds.length; i ++){
			mClouds[i].drawCloud(canvas);
		}
	}
	
	private void drawGifts(Canvas canvas, float degree){
		if((degree%70==0) && (currentCount<mShowGifts.length)){
			currentCount ++;
		}
		for(int i = 0; i < currentCount; i ++){
			mShowGifts[i].drawGift(canvas);
		}
		
	}
	
	public void moveGifts(){
		for(int i = 0; i < currentCount; i ++){
			mShowGifts[i].moveGift();
		}
	}
	
	//检查是否有被打开的宝箱
	public void checkGift(){
		int temp = 0;
		for(int i = 0; i < mShowGifts.length; i ++){
			Gift gift = mShowGifts[i];
			//判断优惠券弹出事件
			if(gift.giftState == Gift.DESTROY && gift.isLiving){
				handler.post(callback);

				//此宝箱已完成任务
				gift.isLiving = false;
			}
			//判断是否更换气球事件
			if(gift.checkGiftOutBounds()){
				temp ++;
			}
		}
		if(temp == mShowGifts.length){
			//更换显示的气球
			currentIndex += mShowGifts.length;
			if(currentIndex == orgCount){
				currentIndex = 0;	//循环完一圈，清0重来
				for(int i = 0; i < mAllGiftList.size(); i ++){
					if(!mAllGiftList.get(i).isLiving){
						mAllGiftList.remove(i);
					}
				}
				for(int i = 0; i < mAllGiftList.size(); i ++){
					mAllGiftList.get(i).resetAllCoord();
				}
				orgCount = mAllGiftList.size();
				count = orgCount;
			}
			initShowGift();
		}
	}
	//弹出特惠券
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.arg1){
			case 7:
				GiftApplication myApplication = (GiftApplication) mActivity.getApplicationContext();
				if(mActivity != null && myApplication.mGiftList.size() != 0){
					mActivity.useAnimationsOut(0.5f, 0.5f);
					handler.removeCallbacks(callback);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
    Runnable callback = new Runnable(){
		public void run(){
			Message m = handler.obtainMessage();
			m.arg1 = 7;
			m.sendToTarget();
		}
	};

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;

		initBitmap();
		//别问这里为什么封的这么复杂……提示：顺序是关键……
		if(!initSurface){
			synchronized (holder) {
				initSurface = true;
				init(count);
			}
		}

	}
	public void surfaceCreated(SurfaceHolder holder) {
		
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mDrawThread != null){
			initSurface = false;
			mDrawThread.draw = false;
//			drawThread.stop();
		}
	}
	

	public void surfaceStarted(int screenWidth, int screenHeight){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(mCanGet){
				
				float touchX = event.getX();
				float touchY = event.getY();
				boolean stop = false;
				if(mShowGifts != null)
				if(mShowGifts.length > 0){
					for(int i = mShowGifts.length-1; i >= 0 && !stop; i --){
						if(mShowGifts[i].balloonRect.contains(touchX, touchY) 
								|| mShowGifts[i].boxRect.contains(touchX, touchY)){
							if(mActivity.popLayout.getVisibility() == View.GONE)
								mCanGet = false;
							stop = true;
							if(mActivity != null){
								mActivity.id = currentIndex + i;
								mShowGifts[i].giftState = Gift.ENDING;
							}
						}
					}
				}
			}else{
				mActivity.showTextToast(getResources().getString(R.string.gift_disposing));
			}
			
			//重写触控事件
			break;
		case MotionEvent.ACTION_UP:
//			touchX = touchY = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
//		return super.onTouchEvent(event);
		return true;
	}
	


    private final class SwapViews implements Runnable {
    	
    	LinearLayout popLayout;
        private final boolean horizontal;

        public SwapViews(LinearLayout popLayout, boolean horizontal) {
        	this.popLayout = popLayout;
            this.horizontal = horizontal;
            
        }

        public void run() {
            final float centerX = popLayout.getWidth() / 2.0f;
            final float centerY = popLayout.getHeight() / 2.0f;
            MyAnimation myAnimation;
            
            myAnimation = new MyAnimation(90, 0, centerX, centerY, 310.0f, false, horizontal);
            
            myAnimation.setDuration(1000);
            myAnimation.setFillAfter(true);
            myAnimation.setInterpolator(new DecelerateInterpolator());
            myAnimation.setAnimationListener(new DisplayNextView2(popLayout));

            popLayout.startAnimation(myAnimation);
        }
    }

    private final class DisplayNextView2 implements Animation.AnimationListener {
    	LinearLayout popLayout;

        private DisplayNextView2(LinearLayout popLayout) {
        	this.popLayout = popLayout;
        }

        public void onAnimationStart(Animation animation) {
        	popLayout.invalidate();
        }

        public void onAnimationEnd(Animation animation) {
        	popLayout.clearAnimation();
        }

		public void onAnimationRepeat(Animation animation) {
		}
    }

}