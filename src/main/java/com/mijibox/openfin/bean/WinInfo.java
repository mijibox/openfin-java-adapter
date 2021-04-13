package com.mijibox.openfin.bean;

public class WinInfo extends FinJsonBean {

	private WinDetail[] childWindows;
	private WinDetail mainWindow;
	private String uuid;

	public WinDetail[] getChildWindows() {
		return childWindows;
	}

	void setChildWindows(WinDetail[] childWindows) {
		this.childWindows = childWindows;
	}

	public WinDetail getMainWindow() {
		return mainWindow;
	}

	void setMainWindow(WinDetail mainWindow) {
		this.mainWindow = mainWindow;
	}

	public String getUuid() {
		return uuid;
	}

	void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
