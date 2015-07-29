package com.scxh.gift.android.dao;

import java.io.Serializable;

import android.graphics.Bitmap;

public class User implements Serializable {
	private int id;
	private String name;
	private String passWord;
	private Bitmap headIcon;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public Bitmap getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(Bitmap headIcon) {
		this.headIcon = headIcon;
	}
}
