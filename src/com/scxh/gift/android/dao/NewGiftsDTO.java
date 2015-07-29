package com.scxh.gift.android.dao;

import java.util.List;

public class NewGiftsDTO extends BasicDTO {

	public List<NewGiftDTO> info;

	public List<NewGiftDTO> getInfo() {
		return info;
	}

	public void setInfo(List<NewGiftDTO> info) {
		this.info = info;
	}

}
