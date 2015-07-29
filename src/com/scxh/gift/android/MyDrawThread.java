package com.scxh.gift.android;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MyDrawThread extends Thread{

	MyGiftSurfer gameView;
	SurfaceHolder holder;
	private static float degree = 0;
	
	long threadStart;
	long threadRun;
	
	public static boolean draw;
	
	public static int delay = 40;
	
	public MyDrawThread(MyGiftSurfer gameView){
		this.gameView = gameView;
		holder = gameView.getHolder();
	}
	
	public void run(){
		Canvas canvas = null;

		while(draw){
			threadStart = System.currentTimeMillis();

			gameView.moveGifts();
			gameView.checkGift();
			try{
				canvas = holder.lockCanvas(null);
//				canvas = holder.lockCanvas(new Rect(0, 0, gameView.screenWidth, gameView.screenHeight * 5 / 6));
				synchronized(holder){
					if(canvas != null)
					gameView.doDraw(canvas, degree);
				}
			}finally{
				if(canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			
			degree += 5;
			
			threadRun = System.currentTimeMillis() - threadStart;
//			Logs.i("runTime:", "thread run time----------:"+threadRun);	/////////
			if(threadRun<delay){
				try{
//					Logs.i("sleep", "thread sleep----------:"+(delay -threadRun));	/////////
					this.sleep(delay-threadRun);
				}catch(Exception e){}
			}
		}
	}
}
