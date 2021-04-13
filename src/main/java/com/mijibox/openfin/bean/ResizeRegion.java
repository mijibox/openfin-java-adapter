package com.mijibox.openfin.bean;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbTypeAdapter(ResizeRegionAdapter.class)
public class ResizeRegion extends FinJsonBean {
	private Integer bottomRightCorner;
	private Integer size;
	private Map<Side, Boolean> resizableSideMap;
	
	public enum Side {
		Top,
		Bottom,
		Left,
		Right
	}
	
	public ResizeRegion() {
		this.resizableSideMap = new HashMap<>();
	}

	public Integer getBottomRightCorner() {
		return bottomRightCorner;
	}

	public void setBottomRightCorner(Integer bottomRightCorner) {
		this.bottomRightCorner = bottomRightCorner;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	public void setResizableSide(Side side, Boolean enabled) {
		this.resizableSideMap.put(side,  enabled);
	}
	
	public Boolean getResizableSide(Side side) {
		return this.resizableSideMap.get(side);
	}
}
