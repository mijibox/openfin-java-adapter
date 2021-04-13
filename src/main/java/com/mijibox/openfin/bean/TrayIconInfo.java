package com.mijibox.openfin.bean;

public class TrayIconInfo extends FinJsonBean {
	private Rectangle bounds;
	private MonitorInfo monitorInfo;
	private Integer x;
	private Integer y;
	public Rectangle getBounds() {
		return bounds;
	}
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	public MonitorInfo getMonitorInfo() {
		return monitorInfo;
	}
	public void setMonitorInfo(MonitorInfo monitorInfo) {
		this.monitorInfo = monitorInfo;
	}
	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	
	
}
