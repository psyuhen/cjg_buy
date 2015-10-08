/**
 * 
 */
package com.cjhbuy.bean;

import java.io.Serializable;

/**
 * @author ps
 *
 */
public class Gallery  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1362096766120387201L;
	private int gallery_id;
	private int merch_id;
	private int classify_id;
	private String name;
	private String file_name;
	private String path;
	private String create_time;
	public int getGallery_id() {
		return gallery_id;
	}
	public void setGallery_id(int gallery_id) {
		this.gallery_id = gallery_id;
	}
	public int getMerch_id() {
		return merch_id;
	}
	public void setMerch_id(int merch_id) {
		this.merch_id = merch_id;
	}
	public int getClassify_id() {
		return classify_id;
	}
	public void setClassify_id(int classify_id) {
		this.classify_id = classify_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
}
