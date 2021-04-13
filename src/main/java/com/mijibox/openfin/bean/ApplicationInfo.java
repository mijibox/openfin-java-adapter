package com.mijibox.openfin.bean;

public class ApplicationInfo extends FinJsonBean {

	private ApplicationOptions initialOptions;
	private RuntimeConfig manifest;
	private Runtime runtime;
	private String launchMode;
	private String manifestUrl;
	private String parentUuid;

	public ApplicationOptions getInitialOptions() {
		return initialOptions;
	}

	public void setInitialOptions(ApplicationOptions initialOptions) {
		this.initialOptions = initialOptions;
	}

	public RuntimeConfig getManifest() {
		return manifest;
	}

	public void setManifest(RuntimeConfig manifest) {
		this.manifest = manifest;
	}

	public Runtime getRuntime() {
		return runtime;
	}

	public void setRuntime(Runtime runtime) {
		this.runtime = runtime;
	}

	public String getLaunchMode() {
		return launchMode;
	}

	public void setLaunchMode(String launchMode) {
		this.launchMode = launchMode;
	}

	public String getManifestUrl() {
		return manifestUrl;
	}

	public void setManifestUrl(String manifestUrl) {
		this.manifestUrl = manifestUrl;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	
}
