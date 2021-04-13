package com.mijibox.openfin.bean;

public class LayoutSettings extends FinJsonBean {

	private Boolean constrainDragToHeaders;
	private Boolean hasHeaders;
	private Boolean popoutWholeStack;
	private Boolean preventDragIn;
	private Boolean preventDragOut;
	private Boolean reorderEnabled;
	private Boolean showCloseIcon;
	private Boolean showMaximiseIcon;
	private Boolean showPopoutIcon;

	public Boolean getConstrainDragToHeaders() {
		return constrainDragToHeaders;
	}

	public void setConstrainDragToHeaders(Boolean constrainDragToHeaders) {
		this.constrainDragToHeaders = constrainDragToHeaders;
	}

	public Boolean getHasHeaders() {
		return hasHeaders;
	}

	public void setHasHeaders(Boolean hasHeaders) {
		this.hasHeaders = hasHeaders;
	}

	public Boolean getPopoutWholeStack() {
		return popoutWholeStack;
	}

	public void setPopoutWholeStack(Boolean popoutWholeStack) {
		this.popoutWholeStack = popoutWholeStack;
	}

	public Boolean getPreventDragIn() {
		return preventDragIn;
	}

	public void setPreventDragIn(Boolean preventDragIn) {
		this.preventDragIn = preventDragIn;
	}

	public Boolean getPreventDragOut() {
		return preventDragOut;
	}

	public void setPreventDragOut(Boolean preventDragOut) {
		this.preventDragOut = preventDragOut;
	}

	public Boolean getReorderEnabled() {
		return reorderEnabled;
	}

	public void setReorderEnabled(Boolean reorderEnabled) {
		this.reorderEnabled = reorderEnabled;
	}

	public Boolean getShowCloseIcon() {
		return showCloseIcon;
	}

	public void setShowCloseIcon(Boolean showCloseIcon) {
		this.showCloseIcon = showCloseIcon;
	}

	public Boolean getShowMaximiseIcon() {
		return showMaximiseIcon;
	}

	public void setShowMaximiseIcon(Boolean showMaximiseIcon) {
		this.showMaximiseIcon = showMaximiseIcon;
	}

	public Boolean getShowPopoutIcon() {
		return showPopoutIcon;
	}

	public void setShowPopoutIcon(Boolean showPopoutIcon) {
		this.showPopoutIcon = showPopoutIcon;
	}

}
