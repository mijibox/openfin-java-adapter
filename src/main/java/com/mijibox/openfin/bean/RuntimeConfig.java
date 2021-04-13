package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbProperty;

public class RuntimeConfig extends FinJsonBean {
	
	private String licenseKey;
	@JsonbProperty(value = "devtools_port")
	private Integer devtoolsPort;
	private Runtime runtime;
	private Shortcut shortcut;
	private Boolean splashScreenForceDownload;
	private String splashScreenImage;
	private Boolean offlineAccess;
	private String overrideConfigUrl;
	@JsonbProperty(value = "startup_app")
	private ApplicationOptions startupApp;
	private Service[] services; 
	private Boolean nonPersistent;
	
	public Boolean getSplashScreenForceDownload() {
		return splashScreenForceDownload;
	}

	public void setSplashScreenForceDownload(Boolean splashScreenForceDownload) {
		this.splashScreenForceDownload = splashScreenForceDownload;
	}

	public String getSplashScreenImage() {
		return splashScreenImage;
	}

	public void setSplashScreenImage(String splashScreenImage) {
		this.splashScreenImage = splashScreenImage;
	}

	public Boolean getOfflineAccess() {
		return offlineAccess;
	}

	public void setOfflineAccess(Boolean offlineAccess) {
		this.offlineAccess = offlineAccess;
	}

	public String getOverrideConfigUrl() {
		return overrideConfigUrl;
	}

	public void setOverrideConfigUrl(String overrideConfigUrl) {
		this.overrideConfigUrl = overrideConfigUrl;
	}

	public ApplicationOptions getStartupApp() {
		return startupApp;
	}

	public void setStartupApp(ApplicationOptions startupApp) {
		this.startupApp = startupApp;
	}

	public RuntimeConfig() {
	}

	public String getLicenseKey() {
		return licenseKey;
	}

	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}

	public Integer getDevtoolsPort() {
		return devtoolsPort;
	}

	public void setDevtoolsPort(Integer devtoolsPort) {
		this.devtoolsPort = devtoolsPort;
	}

	public Runtime getRuntime() {
		if (runtime == null) {
			runtime = new Runtime();
		}
		return runtime;
	}

	public void setRuntime(Runtime runtime) {
		this.runtime = runtime;
	}

	public Shortcut getShortcut() {
		return shortcut;
	}

	public void setShortcut(Shortcut shortcut) {
		this.shortcut = shortcut;
	}
	
	public void setServices(Service... services) {
		this.services = services;
	}
	
	public Service[] getServices() {
		return this.services;
	}

	public Boolean getNonPersistent() {
		return nonPersistent;
	}

	public void setNonPersistent(Boolean nonPersistent) {
		this.nonPersistent = nonPersistent;
	}
}
