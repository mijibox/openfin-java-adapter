package com.mijibox.openfin.bean;

public class PrintOptions extends FinJsonBean {

	public final static String DUPLEX_MODE_SIMPLEX = "simplex";
	public final static String DUPLEX_MODE_SHORT_EDGE = "shortEdge";
	public final static String DUPLEX_MODE_LONG_EDGE = "longEdge";
	
	private Boolean silent;
	private Boolean printBackground;
	private String deviceName;
	private Boolean color;
	private Margins margins;
	private Boolean landscape;
	private Double scaleFactor;
	private Integer pagesPerSheet;
	private Boolean collate;
	private Integer copies;
	private String pageRanges;
	private String duplexMode;
	private Dpi dpi;

	public Boolean getSilent() {
		return silent;
	}

	public void setSilent(Boolean silent) {
		this.silent = silent;
	}

	public Boolean getPrintBackground() {
		return printBackground;
	}

	public void setPrintBackground(Boolean printBackground) {
		this.printBackground = printBackground;
	}

	public Boolean getColor() {
		return color;
	}

	public void setColor(Boolean color) {
		this.color = color;
	}

	public Margins getMargins() {
		return margins;
	}

	public void setMargins(Margins margins) {
		this.margins = margins;
	}

	public Boolean getLandscape() {
		return landscape;
	}

	public void setLandscape(Boolean landscape) {
		this.landscape = landscape;
	}

	public Double getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(Double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public Integer getPagesPerSheet() {
		return pagesPerSheet;
	}

	public void setPagesPerSheet(Integer pagesPerSheet) {
		this.pagesPerSheet = pagesPerSheet;
	}

	public Boolean getCollate() {
		return collate;
	}

	public void setCollate(Boolean collate) {
		this.collate = collate;
	}

	public Integer getCopies() {
		return copies;
	}

	public void setCopies(Integer copies) {
		this.copies = copies;
	}

	public String getPageRanges() {
		return pageRanges;
	}

	public void setPageRanges(String pageRanges) {
		this.pageRanges = pageRanges;
	}

	public String getDuplexMode() {
		return duplexMode;
	}

	public void setDuplexMode(String duplexMode) {
		this.duplexMode = duplexMode;
	}

	public Dpi getDpi() {
		return dpi;
	}

	public void setDpi(Dpi dpi) {
		this.dpi = dpi;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

}
