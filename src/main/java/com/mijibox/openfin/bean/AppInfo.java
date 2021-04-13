package com.mijibox.openfin.bean;

public class AppInfo extends FinJsonBean {

	private Boolean isPlatform;
	private Boolean isRunning;
	private String uuid;
	private String parentUuid;

	public Boolean getIsPlatform() {
		return isPlatform;
	}

	public void setIsPlatform(Boolean isPlatform) {
		this.isPlatform = isPlatform;
	}

	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

}
