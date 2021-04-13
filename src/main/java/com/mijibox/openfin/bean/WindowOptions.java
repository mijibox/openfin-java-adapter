package com.mijibox.openfin.bean;

public class WindowOptions extends WindowUpdatableOptions {

	protected Boolean autoShow;
	protected Integer defaultTop;
	protected Integer defaultLeft;
	protected Integer defaultWidth;
	protected Integer defaultHeight;
	protected Boolean defaultCentered;
	protected String name;
	protected String url;
	protected Boolean waitForPageLoad;
	protected Boolean smallWindow;
	protected LayoutConfig layout;

	public WindowOptions() {
	}

	public WindowOptions(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getAutoShow() {
		return autoShow;
	}

	public void setAutoShow(Boolean autoShow) {
		this.autoShow = autoShow;
	}

	public Integer getDefaultTop() {
		return defaultTop;
	}

	public void setDefaultTop(Integer defaultTop) {
		this.defaultTop = defaultTop;
	}

	public Integer getDefaultLeft() {
		return defaultLeft;
	}

	public void setDefaultLeft(Integer defaultLeft) {
		this.defaultLeft = defaultLeft;
	}

	public Integer getDefaultWidth() {
		return defaultWidth;
	}

	public void setDefaultWidth(Integer defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	public Integer getDefaultHeight() {
		return defaultHeight;
	}

	public void setDefaultHeight(Integer defaultHeight) {
		this.defaultHeight = defaultHeight;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getWaitForPageLoad() {
		return waitForPageLoad;
	}

	public void setWaitForPageLoad(Boolean waitForPageLoad) {
		this.waitForPageLoad = waitForPageLoad;
	}

	public Boolean getSmallWindow() {
		return smallWindow;
	}

	public void setSmallWindow(Boolean smallWindow) {
		this.smallWindow = smallWindow;
	}

	public LayoutConfig getLayout() {
		return layout;
	}

	public void setLayout(LayoutConfig layout) {
		this.layout = layout;
	}

	public Boolean getDefaultCentered() {
		return defaultCentered;
	}

	public void setDefaultCentered(Boolean defaultCentered) {
		this.defaultCentered = defaultCentered;
	}

}
