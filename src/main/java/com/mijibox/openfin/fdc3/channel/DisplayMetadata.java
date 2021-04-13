package com.mijibox.openfin.fdc3.channel;

import com.mijibox.openfin.bean.FinJsonBean;

public class DisplayMetadata extends FinJsonBean {

	private String color;
	private String glyph;
	private String name;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getGlyph() {
		return glyph;
	}

	public void setGlyph(String glyph) {
		this.glyph = glyph;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
