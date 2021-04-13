package com.mijibox.openfin.bean;


import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;

public class CapturePageOptions extends FinJsonBean {

	public final static String IMAGE_FORMAT_BMP = "bmp";
	public final static String IMAGE_FORMAT_JPG = "jpg";
	public final static String IMAGE_FORMAT_PNG = "png";

	private String format;
	private Rectangle area;
	private Integer quality;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Rectangle getArea() {
		return area;
	}

	public void setArea(Rectangle area) {
		this.area = area;
	}

	public Integer getQuality() {
		return quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

}
