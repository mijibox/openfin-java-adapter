package com.mijibox.openfin.bean;

import java.util.List;

public class MonitorInfo extends FinJsonBean {
	private Double deviceScaleFactor;
	private Point dpi;
	private List<MonitorDetails> nonPrimaryMonitors;
	private MonitorDetails primaryMonitor;
	private String reason;
	private TaskBar taskBar;
	private DipRect virtualScreen;

	public Point getDpi() {
		return dpi;
	}

	public void setDpi(Point dpi) {
		this.dpi = dpi;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Double getDeviceScaleFactor() {
		return deviceScaleFactor;
	}

	public void setDeviceScaleFactor(Double deviceScaleFactor) {
		this.deviceScaleFactor = deviceScaleFactor;
	}

	public MonitorDetails getPrimaryMonitor() {
		return primaryMonitor;
	}

	public void setPrimaryMonitor(MonitorDetails primaryMonitor) {
		this.primaryMonitor = primaryMonitor;
	}

	public List<MonitorDetails> getNonPrimaryMonitors() {
		return nonPrimaryMonitors;
	}

	public void setNonPrimaryMonitors(List<MonitorDetails> nonPrimaryMonitors) {
		this.nonPrimaryMonitors = nonPrimaryMonitors;
	}

	public TaskBar getTaskBar() {
		return taskBar;
	}

	public void setTaskBar(TaskBar taskBar) {
		this.taskBar = taskBar;
	}

	public DipRect getVirtualScreen() {
		return virtualScreen;
	}

	public void setVirtualScreen(DipRect virtualScreen) {
		this.virtualScreen = virtualScreen;
	}
}
