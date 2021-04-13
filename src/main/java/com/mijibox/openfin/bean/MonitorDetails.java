package com.mijibox.openfin.bean;

public class MonitorDetails extends FinJsonBean {

	private DipScaleRects available;
	private WindowBounds availableRect;
	private String deviceId;
	private Boolean displayDeviceActive;
	private Double deviceScaleFactor;
	private WindowBounds monitorRect;
	private String name;
	private Point dpi;
	private DipScaleRects monitor;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Boolean getDisplayDeviceActive() {
		return displayDeviceActive;
	}

	public void setDisplayDeviceActive(Boolean displayDeviceActive) {
		this.displayDeviceActive = displayDeviceActive;
	}

	public Double getDeviceScaleFactor() {
		return deviceScaleFactor;
	}

	public void setDeviceScaleFactor(Double deviceScaleFactor) {
		this.deviceScaleFactor = deviceScaleFactor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point getDpi() {
		return dpi;
	}

	public void setDpi(Point dpi) {
		this.dpi = dpi;
	}

	public DipScaleRects getAvailable() {
		return available;
	}

	public void setAvailable(DipScaleRects available) {
		this.available = available;
	}

	public WindowBounds getAvailableRect() {
		return availableRect;
	}

	public void setAvailableRect(WindowBounds availableRect) {
		this.availableRect = availableRect;
	}

	public WindowBounds getMonitorRect() {
		return monitorRect;
	}

	public void setMonitorRect(WindowBounds monitorRect) {
		this.monitorRect = monitorRect;
	}

	public DipScaleRects getMonitor() {
		return monitor;
	}

	public void setMonitor(DipScaleRects monitor) {
		this.monitor = monitor;
	}

}
