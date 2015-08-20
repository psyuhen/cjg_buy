/**
 * 
 */
package com.cjhbuy.bean;

/**
 * 分类信息
 * @author ps
 *
 */
public class ClassifyInfo {
	private int classify_id;
    private String name;
    private String classify_type;
    private String desc;
    private int classify_num;
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
	public String getClassify_type() {
		return classify_type;
	}
	public void setClassify_type(String classify_type) {
		this.classify_type = classify_type;
	}
	public int getClassify_num() {
		return classify_num;
	}
	public void setClassify_num(int classify_num) {
		this.classify_num = classify_num;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
