package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbProperty;

public class PlatformOptions extends ApplicationOptions {
	
	private DefaultWindowOptions defaultWindowOptions;
	private ViewOptions defaultViewOptions;
	
	public PlatformOptions(String uuid) {
		super(uuid);
	}
	
	@JsonbProperty("isPlatformController")
	public boolean isPlatformController() {
		return true;
	}

	public DefaultWindowOptions getDefaultWindowOptions() {
		return defaultWindowOptions;
	}

	public void setDefaultWindowOptions(DefaultWindowOptions defaultWindowOptions) {
		this.defaultWindowOptions = defaultWindowOptions;
	}

	public ViewOptions getDefaultViewOptions() {
		return defaultViewOptions;
	}

	public void setDefaultViewOptions(ViewOptions defaultViewOptions) {
		this.defaultViewOptions = defaultViewOptions;
	}
	
}
