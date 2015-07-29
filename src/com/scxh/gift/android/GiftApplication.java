package com.scxh.gift.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.scxh.gift.android.dao.NewGiftDTO;

public class GiftApplication extends Application {
	public static Long countTime = (long) -1.0f;
	public List<NewGiftDTO> mGiftList = new ArrayList<NewGiftDTO>(); // 宝箱动画
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
}
