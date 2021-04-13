package com.mijibox.openfin.bean;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class ContextMenuSettings extends FinJsonBean {
	private Boolean enable;
	private Boolean devtools;
	private Boolean reload;

	@JsonbCreator
	public ContextMenuSettings(@JsonbProperty("enable")boolean enable) {
		this.enable = enable;
	}

	public Boolean getDevtools() {
		return devtools;
	}

	public void setDevtools(Boolean devtools) {
		this.devtools = devtools;
	}

	public Boolean getReload() {
		return reload;
	}

	public void setReload(Boolean reload) {
		this.reload = reload;
	}

	public Boolean getEnable() {
		return enable;
	}
	
	
}
