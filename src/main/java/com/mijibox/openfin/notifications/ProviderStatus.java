package com.mijibox.openfin.notifications;

import com.mijibox.openfin.bean.FinJsonBean;

public class ProviderStatus extends FinJsonBean {

	private Boolean connected;
	private String version;

	public Boolean getConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}
	
	public boolean isConnected() {
		return this.connected == null ? false : this.connected.booleanValue();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
