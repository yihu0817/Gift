package com.scxh.gift.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scxh.gift.android.dao.NewGiftDTO;
import com.scxh.gift.android.dao.User;
import com.scxh.gift.android.util.AsyncImageLoader;
import com.scxh.gift.android.util.AsyncImageLoader.ImageCallbackForBitmap;
import com.scxh.gift.android.util.Constances;

public class GiftActivity extends Activity {

	public LinearLayout popLayout;
	ImageView popLayoutImage;
	Button cancelButton, trueButton;
	Button closeButton;

	ImageView mPromptImageView;

	private MyGiftSurfer mySurfer;
	SurfaceHolder holder;
	// 网络数据读取
	GiftApplication mApplication;
	// 领取宝箱的用户ID
	private User mUser;
	// 领取的宝箱的ID
	public static int id;

	// 网络数据操作
	private DiscardGiftTask mDiscardGiftTask;
	private ObtainGiftTask mObtainGiftTask;
	AsyncImageLoader mAsyncImageLoader = new AsyncImageLoader();
	private Toast mToast = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_gift_layout);
		
		mApplication = (GiftApplication) this.getApplicationContext();
		int count = 4;
		for(int i =0; i< count; i++ ){
			NewGiftDTO NewGiftDTO = new NewGiftDTO();
			NewGiftDTO.setGiftRecordId("100"+i);
			NewGiftDTO.setPicUrl(Constances.imageThumbUrls[i%9]);
			
			mApplication.mGiftList.add(NewGiftDTO);
		}
		
		init();
	}

	public void init() {
		cancelButton = (Button) findViewById(R.id.dialog_button_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				cancelButton.setClickable(false);
				trueButton.setClickable(false);
				// popLayout.setVisibility(View.GONE);
				useAnimationsIn(0.0f, 0.0f);
				mySurfer.drawBalloons = true;

				// 丢弃宝箱
				List<String> discardId = new ArrayList<String>();
				discardId.add(mApplication.mGiftList.get(0).getGiftRecordId());
				mySurfer.mCanGet = true;

				mDiscardGiftTask = (DiscardGiftTask) new DiscardGiftTask().execute(discardId);

			}
		});

		trueButton = (Button) findViewById(R.id.dialog_button_true);
		trueButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelButton.setClickable(false);
				trueButton.setClickable(false);
				useAnimationsIn(1.0f, 1.0f);

				// 领取宝箱
				mObtainGiftTask = (ObtainGiftTask) new ObtainGiftTask().execute();
				mySurfer.mCanGet = true;
			}
		});

		popLayout = (LinearLayout) findViewById(R.id.popLayout);
		popLayout.setVisibility(View.GONE);
		popLayoutImage = (ImageView) popLayout.findViewById(R.id.dialog_title_image);

		closeButton = (Button) findViewById(R.id.closeButton);
		closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 先解决继续Loading的问题
				GiftApplication.countTime = System.currentTimeMillis();

				GiftActivity.this.finish();

				// 丢弃宝箱
				List<String> discardIds = new ArrayList<String>();
				for (int i = 0; i < mApplication.mGiftList.size(); i++) {
					discardIds.add(mApplication.mGiftList.get(i)
							.getGiftRecordId());
				}
				mApplication.mGiftList.removeAll(mApplication.mGiftList);
				mDiscardGiftTask = (DiscardGiftTask) new DiscardGiftTask()
						.execute(discardIds);

				mySurfer.mCanGet = true;
				mApplication.mGiftList.clear();

			}
		});

		mPromptImageView = (ImageView) findViewById(R.id.prompt);
		mPromptImageView.setClickable(false);
		mPromptImageView.setFocusable(false);

		mySurfer = (MyGiftSurfer) findViewById(R.id.myView);
		mySurfer.setCount(mApplication.mGiftList.size());
		if (mApplication.mGiftList.size() == 0) {
			finish();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mySurfer.setCount(mApplication.mGiftList.size());
	}

	public void cancelAsyncTask(AsyncTask mAsyncTask) {
		if (mAsyncTask != null) {
			mAsyncTask.cancel(true);
			mAsyncTask = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelAsyncTask(mDiscardGiftTask);
		cancelAsyncTask(mObtainGiftTask);
	}

	class clearListener implements AnimationListener {

		private LinearLayout layout;

		public clearListener(LinearLayout layout) {
			this.layout = layout;
		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			layout.clearAnimation();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			closeButton.performClick();
		}
		return super.onKeyDown(keyCode, event);
	}

	private class DiscardGiftTask extends AsyncTask<List<String>, Void, String> {
		@Override
		protected String doInBackground(List<String>... params) {
			List<String> discardIds = params[0];
			try {
//				mApplication.getApiManager().discardGift(discardIds);
			} catch (Exception me) {
				me.printStackTrace();
				return null;
			}
			return "success";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				// Logs.e("look~~~","丢弃宝箱失败！"); ///////////
			}
			if (mApplication.mGiftList.size() != 0)
				mApplication.mGiftList.remove(0);
		}
	}

	// 领取宝箱
	private class ObtainGiftTask extends AsyncTask<String, Void, Integer> {
		int success = 0;
		int fail = 1;

		@Override
		protected Integer doInBackground(String... params) {
			try {
				return success;
			} catch (Exception e) {
				e.printStackTrace();
				return fail;
			}

		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result != fail) {
				// 操作本地宝箱数据
				if (mApplication.mGiftList.size() != 0)
					mApplication.mGiftList.remove(0);
				mPromptImageView.setVisibility(View.VISIBLE);
				showPromptAnimations();

				// 宝箱存入数据库
				
			} else {
				showTextToast(getResources().getString(R.string.gift_get_fail));
			}
		}
	}

	public void useAnimationsIn(float x, float y) {

		Animation animation;
		animation = new ScaleAnimation(1.0f, 0, 1.0f, 0,
				Animation.RELATIVE_TO_PARENT, x, Animation.RELATIVE_TO_PARENT,
				y);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(animation);
		set.setDuration(500);
		set.setFillAfter(true);
		popLayout.startAnimation(set);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				popLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				popLayout.setVisibility(View.GONE);
				popLayout.clearAnimation();

				cancelButton.setClickable(true);
				trueButton.setClickable(true);
			}
		});

	}

	public void useAnimationsOut(float x, float y) {
		Bitmap quanBitmap = mAsyncImageLoader.loadBitmap(mApplication.mGiftList.get(0).getPicUrl(), new ImageCallbackForBitmap() {
			
			@Override
			public void imageLoaded(Bitmap bitmap, String imageUrl) {
				popLayoutImage.setImageBitmap(bitmap);
			}
		});
		if(quanBitmap != null){
			popLayoutImage.setImageBitmap(quanBitmap);
		}else{
			popLayoutImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.m4));
		}
		
		
		popLayout.setVisibility(View.VISIBLE);

		Animation animation;
		animation = new ScaleAnimation(0, 1.0f, 0, 1.0f,
				Animation.RELATIVE_TO_PARENT, x, Animation.RELATIVE_TO_PARENT,
				y);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(animation);
		set.setDuration(500);
		set.setFillAfter(true);
		popLayout.startAnimation(set);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				popLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				popLayout.clearAnimation();
			}
		});

	}

	public void showPromptAnimations() {

		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		TranslateAnimation trans = new TranslateAnimation(0, 0, 50, 0);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(alpha);
		set.addAnimation(trans);
		set.setDuration(500);
		set.setFillAfter(true);
		mPromptImageView.startAnimation(set);
		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mPromptImageView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mPromptImageView.clearAnimation();
				hidePromptAnimations();
			}
		});

	}

	public void hidePromptAnimations() {

		AlphaAnimation alpha = new AlphaAnimation(1, 0);
		TranslateAnimation trans = new TranslateAnimation(0, 0, 0, -50);

		AnimationSet set = new AnimationSet(true);
		set.addAnimation(alpha);
		set.addAnimation(trans);
		set.setDuration(500);
		set.setFillAfter(true);
		mPromptImageView.startAnimation(set);
		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mPromptImageView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mPromptImageView.clearAnimation();
				mPromptImageView.setVisibility(View.GONE);
			}
		});

	}

	public void showTextToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

}
