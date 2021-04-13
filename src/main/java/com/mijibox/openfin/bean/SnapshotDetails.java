package com.mijibox.openfin.bean;

public class SnapshotDetails {
	private MonitorInfo monitorInfo;
	private String runtimeVersion;
	private String timestamp;

	public MonitorInfo getMonitorInfo() {
		return monitorInfo;
	}

	public void setMonitorInfo(MonitorInfo monitorInfo) {
		this.monitorInfo = monitorInfo;
	}

	public String getRuntimeVersion() {
		return runtimeVersion;
	}

	public void setRuntimeVersion(String runtimeVersion) {
		this.runtimeVersion = runtimeVersion;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
