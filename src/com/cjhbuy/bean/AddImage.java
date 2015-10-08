package com.cjhbuy.bean;

import java.io.File;

import android.graphics.Bitmap;

import com.cjhbuy.utils.ImageUtil;

public class AddImage {

	private int id;
	private String fileName;
	private File file;
	private Bitmap bitmap;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public AddImage(int id, Bitmap bitmap) {
		super();
		this.id = id;
		this.bitmap = bitmap;
	}
	public AddImage(int id, String fileName,Bitmap bitmap) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.bitmap = bitmap;
		if(bitmap != null){
			this.file = ImageUtil.bitmap2file(bitmap);
		}
	}

	public AddImage() {
		super();
	}
	
}
