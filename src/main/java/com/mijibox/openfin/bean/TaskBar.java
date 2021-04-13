package com.mijibox.openfin.bean;

public class TaskBar extends DipScaleRects {

	private String edge;
	private WindowBounds rect;

	public String getEdge() {
		return edge;
	}

	public void setEdge(String edge) {
		this.edge = edge;
	}

	public WindowBounds getRect() {
		return rect;
	}

	public void setRect(WindowBounds rect) {
		this.rect = rect;
	}

}
