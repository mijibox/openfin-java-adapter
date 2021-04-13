package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbProperty;

public class RvmInfo extends FinJsonBean {

	private String appLogDirectory;
	private String path;
	@JsonbProperty("start-time")
	private String startTime;
	private String version;
	@JsonbProperty("working-dir")
	private String workingDir;

	public String getAppLogDirectory() {
		return appLogDirectory;
	}

	public void setAppLogDirectory(String appLogDirectory) {
		this.appLogDirectory = appLogDirectory;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

}
