package com.scxh.gift.android;

import java.util.Random;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Gift {
	public int balloonAlpha, boxAlpha;
	public Bitmap balloon;
	public Bitmap box;
	public float balloonWidth, balloonHeight, boxWidth, boxHeight;
	public int screenWidth, screenHeight;
	
	//坐标
	Coord balloonCoord, boxCoord;
	//大小
	public RectF balloonRect, boxRect;
	//速度
	public float moveSpeedX = 0.8f, moveSpeedY = 2.0f;
	//之间的调整
	private float gapX = 2f, gapY = 15f;
	
	public float scale;	//这个是标准缩放比例
	public float showScale;	//这个是实际的缩放比例
	
	//状态
	public int giftState;
	public static final int BEGINING = 0;
	public static final int LIVEING = 1;
	public static final int READY_END = 2;
	public static final int ENDING = 3;
	public static final int OUT = 4;
	public static final int DESTROY = 5;
	public static final int RESILIENCE = 6;
	public boolean isLiving;
	private int resilienceCount = 0, resilienceMax = 10;
	public int loop;	//精确点击宝箱之后……^
	
	public Paint mPaint;
	
	public Gift(Bitmap balloon, Bitmap box, int screenWidth, int screenHeight){
		this.balloon = balloon;
		this.box = box;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		init();
	}
	
	public void init(){
		this.isLiving = true;
		this.giftState = BEGINING;
		this.balloonAlpha = this.boxAlpha = 0;
		
		mPaint = new Paint();
		mPaint.setAlpha(255);
		mPaint.setAntiAlias(true);
		
		//缩放
		scale = (float) ((screenHeight * 1.00 / 4) / balloon.getHeight());
		scale -= scale * new Random().nextInt(3) * 1.00 / 10;
		showScale = 0;
		//尺寸
		balloonWidth = balloon.getWidth() * showScale;
		balloonHeight = balloon.getHeight() * showScale;
		boxWidth = box.getWidth() * showScale;
		boxHeight = box.getHeight() * showScale;
		gapY = (float) (box.getHeight()*1.0 / 3);
		gapX = (float) (box.getWidth() / 12);
		
		balloonCoord = new Coord();	//暂，取一个范围内的随机数
		int randomX = new Random().nextInt(screenWidth/2);
		int randomY = new Random().nextInt(screenHeight/3);
		balloonCoord.x = screenWidth-balloonWidth-randomX;
		balloonCoord.y = 0+randomY;
		boxCoord = new Coord();	//宝箱
		boxCoord.x = (float) (balloonCoord.x + balloonWidth/2 - boxWidth/2 + gapX);
		boxCoord.y = balloonCoord.y + balloonHeight - gapY;	//gapY

		this.balloonRect = new RectF(balloonCoord.x, balloonCoord.y, balloonCoord.x+balloonWidth, balloonCoord.y+balloonHeight);
		this.boxRect = new RectF(boxCoord.x, boxCoord.y, boxCoord.x+boxWidth, boxCoord.y+boxHeight);
		
//		showScale = 0;
		resetCoord(showScale);
	}
	
	//状态回滚，缩放尺寸复位，透明度复位，所有坐标重置
	public void resetAllCoord(){
		if(giftState == OUT)
			giftState = BEGINING;
		
		showScale = 0;
		balloonAlpha = 0;
		boxAlpha = 0;
		
		balloonCoord = new Coord();
		int randomX = new Random().nextInt(screenWidth/2);
		int randomY = new Random().nextInt(screenHeight/3);
		balloonCoord.x = screenWidth-balloonWidth-randomX;
		balloonCoord.y = 0+randomY;
		boxCoord = new Coord();	//宝箱
		boxCoord.x = (float) (balloonCoord.x + balloonWidth/2 - boxWidth/2 + gapX);
		boxCoord.y = balloonCoord.y + balloonHeight - gapY;	//gapY

		this.balloonRect = new RectF(balloonCoord.x, balloonCoord.y, balloonCoord.x+balloonWidth, balloonCoord.y+balloonHeight);
		this.boxRect = new RectF(boxCoord.x, boxCoord.y, boxCoord.x+boxWidth, boxCoord.y+boxHeight);
	}
	
	//重新定位显示坐标
	public void resetCoord(float showScale){
		//宝箱坐标
		balloonWidth = balloon.getWidth() * showScale;
		balloonHeight = balloon.getHeight() * showScale;
		boxWidth = box.getWidth() * showScale;
		boxHeight = box.getHeight() * showScale;
		
		//宝箱移动
		boxCoord.x = (float) (balloonCoord.x + balloonWidth/2 - boxWidth/2 + gapX*showScale);
		boxCoord.y = balloonCoord.y + balloonHeight - gapY*showScale;	//gapY

		this.balloonRect = new RectF(balloonCoord.x, balloonCoord.y, balloonCoord.x+balloonWidth, balloonCoord.y+balloonHeight);
		this.boxRect = new RectF(boxCoord.x, boxCoord.y, boxCoord.x+boxWidth, boxCoord.y+boxHeight);
	}
	
	public void drawGift(Canvas canvas){
		if(isLiving){
		
			mPaint.setAlpha(balloonAlpha);
			canvas.drawBitmap(balloon, null, balloonRect, mPaint);
			
			mPaint.setAlpha(boxAlpha);
			canvas.drawBitmap(box, null, boxRect, mPaint);
			
//			moveGift();
		}
	}
	
	public void moveGift(){
		switch(giftState){
		case BEGINING:
			if(balloonAlpha < 255){
				if(balloonCoord.x > 0-balloonWidth){
					balloonCoord.x -= moveSpeedX;
				}
				if(balloonCoord.y < screenHeight+balloonHeight){
					balloonCoord.y += moveSpeedY;
				}
				
				balloonAlpha += 5;
				boxAlpha += 5;
				
				showScale += (float)scale*1.00/51;
				resetCoord(showScale);
				
			}else{
				this.giftState = LIVEING;
			}
			break;
		case LIVEING:
			if(balloonCoord.x > 0-balloonWidth){
				balloonCoord.x -= moveSpeedX;
				boxCoord.x -= moveSpeedX;
			}else
				giftState = OUT;
			if(balloonCoord.y < screenHeight){
				balloonCoord.y += moveSpeedY;
				boxCoord.y += moveSpeedY;
			}else
				giftState = OUT;
			break;
		case READY_END:	//精确宝箱点击后的动画……^
			loop = (int) ((screenHeight - box.getHeight() - boxCoord.y) / 5);
			break;
		case ENDING:
			if(balloonAlpha > 0){
				balloonCoord.y -= 3;
				balloonAlpha -= 5;
			}
			boxAlpha = 255;
			if(boxCoord.y < screenHeight - boxHeight){
				boxCoord.y += 9;

				if(showScale < 1)
					showScale += 0.05;
				boxWidth = box.getWidth() * showScale;
				boxHeight = box.getHeight() * showScale;
			}
			else{
//				this.giftState = DESTROY;
				this.giftState = RESILIENCE;
			}
			break;
		case RESILIENCE:
			if(balloonAlpha > 0){
				balloonCoord.y -= 3;
				balloonAlpha -= 5;
			}
			if(resilienceCount < resilienceMax){
				boxCoord.y -= (10-resilienceCount)%resilienceMax*1.0/2;
				resilienceCount ++;
			}else{
				if(boxCoord.y < screenHeight - boxHeight){
					boxCoord.y -= (10-resilienceCount)%resilienceMax*1.0/2;
					resilienceCount ++;
					
					boxWidth = box.getWidth();
					boxHeight = box.getHeight();
				}else{
					this.giftState = DESTROY;
					resilienceCount = 0;
				}
			}
			break;
		case OUT:
		case DESTROY:
			balloonRect = new RectF(0, 0, 0, 0);
			boxRect = new RectF(0, 0, 0, 0);
			balloonAlpha = 0;
			boxAlpha = 0;
			break;
		default:
			break;
		}

		this.balloonRect = new RectF(balloonCoord.x, balloonCoord.y, balloonCoord.x+balloonWidth, balloonCoord.y+balloonHeight);
		this.boxRect = new RectF(boxCoord.x, boxCoord.y, boxCoord.x+boxWidth, boxCoord.y+boxHeight);
	}
	
	//礼物的点击
	public void getGift(){
		this.giftState = ENDING;
	}
	
	//礼物的判定
	public boolean checkGiftOutBounds(){
		if(giftState == Gift.DESTROY || giftState == Gift.OUT){
			return true;
		}
		return false;
	}

	//礼物的坐标封装
	class Coord{
		float x;
		float y;
	}
}
