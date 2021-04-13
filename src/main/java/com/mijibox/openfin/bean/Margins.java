package com.mijibox.openfin.bean;

public class Margins extends FinJsonBean {
	public final static String TYPE_DEFAULT = "default";
	public final static String TYPE_NONE = "none";
	public final static String TYPE_PRINTABLE_AREA = "printableArea";
	public final static String TYPE_CUSTOM = "custom";

	private String marginType;
	private Double top;
	private Double bottom;
	private Double left;
	private Double right;

	public String getMarginType() {
		return marginType;
	}

	public void setMarginType(String marginType) {
		this.marginType = marginType;
	}

	public Double getTop() {
		return top;
	}

	public void setTop(Double top) {
		this.top = top;
	}

	public Double getBottom() {
		return bottom;
	}

	public void setBottom(Double bottom) {
		this.bottom = bottom;
	}

	public Double getLeft() {
		return left;
	}

	public void setLeft(Double left) {
		this.left = left;
	}

	public Double getRight() {
		return right;
	}

	public void setRight(Double right) {
		this.right = right;
	}

}
