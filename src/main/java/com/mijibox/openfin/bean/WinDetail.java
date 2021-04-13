package com.mijibox.openfin.bean;

public class WinDetail extends FinJsonBean {
	private Integer bottom;
	private Integer height;
	private Integer left;
	private Integer right;
	private Integer top;
	private Integer width;
	private Boolean isShowing;
	private String name;
	private String state;

	public Integer getBottom() {
		return bottom;
	}

	public void setBottom(Integer bottom) {
		this.bottom = bottom;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getLeft() {
		return left;
	}

	public void setLeft(Integer left) {
		this.left = left;
	}

	public Integer getRight() {
		return right;
	}

	public void setRight(Integer right) {
		this.right = right;
	}

	public Integer getTop() {
		return top;
	}

	public void setTop(Integer top) {
		this.top = top;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Boolean getIsShowing() {
		return isShowing;
	}

	public void setIsShowing(Boolean isShowing) {
		this.isShowing = isShowing;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
