package com.scxh.gift.android.dao;

public class NewGiftMessageDTO {
	
	private String resultCode;
	private String resultInfo;
	private NewGiftsDTO info;
	
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultInfo() {
		return resultInfo;
	}
	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}
	public NewGiftsDTO getInfo() {
		return info;
	}
	public void setInfo(NewGiftsDTO info) {
		this.info = info;
	}

}
