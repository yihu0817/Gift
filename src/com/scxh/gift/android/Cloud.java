package com.scxh.gift.android;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Cloud {
	
	private Bitmap cloud;
	private float x, y;
	private int screenWidth, screenHeight;
	private RectF bound;
	private Paint mPaint;
	boolean moveLeft;
	
	public Cloud(Bitmap cloud, float x, float y, int screenWidth, int screenHeight){
		this.cloud = cloud;
		this.x = x;
		this.y = y;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		init();
	}
	
	public void init(){
		mPaint = new Paint();
		mPaint.setAlpha(255);
		
		//以左为基准
		bound = new RectF(x, 0, x+20+new Random().nextInt(20), 0);
		//判断，然后调整
		if(bound.right > screenWidth){
			bound.left = screenWidth - bound.width();
		}
		//再次调整……如果需要这次调整，那就需要控制大小了
		if(bound.left < 0){
			bound.left = 0;
		}
	}
	/**
	 * 在画布上绘制白云
	 * @param canvas
	 */
	public void drawCloud(Canvas canvas){
		canvas.drawBitmap(cloud, x, y, mPaint);
		
		moveCloud();
	}
	
	public void moveCloud(){
		if(moveLeft){
			if(x > bound.left){
				x -= 0.5;
			}else{
				moveLeft = false;
			}
		}else{
			if(x < bound.right){
				x += 0.5;
			}else{
				moveLeft = true;
			}
		}
	}

}
