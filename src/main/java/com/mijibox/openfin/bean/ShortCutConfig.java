package com.mijibox.openfin.bean;

public class ShortCutConfig extends FinJsonBean {

	private Boolean desktop;
	private Boolean startMenu;
	private Boolean systemStartup;

	public Boolean getDesktop() {
		return desktop;
	}

	public void setDesktop(Boolean desktop) {
		this.desktop = desktop;
	}

	public Boolean getStartMenu() {
		return startMenu;
	}

	public void setStartMenu(Boolean startMenu) {
		this.startMenu = startMenu;
	}

	public Boolean getSystemStartup() {
		return systemStartup;
	}

	public void setSystemStartup(Boolean systemStartup) {
		this.systemStartup = systemStartup;
	}

}
