package com.mijibox.openfin.bean;

public class DipRect extends WindowBounds {
	private WindowBounds dipRect;
	private WindowBounds scaledRect;

	public WindowBounds getDipRect() {
		return dipRect;
	}

	public void setDipRect(WindowBounds dipRect) {
		this.dipRect = dipRect;
	}

	public WindowBounds getScaledRect() {
		return scaledRect;
	}

	public void setScaledRect(WindowBounds scaledRect) {
		this.scaledRect = scaledRect;
	}

}
