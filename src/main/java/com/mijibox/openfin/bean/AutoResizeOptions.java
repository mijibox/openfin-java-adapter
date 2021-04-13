package com.mijibox.openfin.bean;

public class AutoResizeOptions extends FinJsonBean {

	private Boolean width;
	private Boolean height;
	private Boolean horizontal;
	private Boolean vertical;
	
	public AutoResizeOptions() {
		
	}
	
	public AutoResizeOptions(boolean width, boolean height, boolean horizontal, boolean vertical) {
		this.width = width;
		this.height = height;
		this.horizontal = horizontal;
		this.vertical = vertical;
	}

	public Boolean getWidth() {
		return width;
	}

	public void setWidth(Boolean width) {
		this.width = width;
	}

	public Boolean getHeight() {
		return height;
	}

	public void setHeight(Boolean height) {
		this.height = height;
	}

	public Boolean getHorizontal() {
		return horizontal;
	}

	public void setHorizontal(Boolean horizontal) {
		this.horizontal = horizontal;
	}

	public Boolean getVertical() {
		return vertical;
	}

	public void setVertical(Boolean vertical) {
		this.vertical = vertical;
	}

}
