package com.mijibox.openfin.fdc3;

import com.mijibox.openfin.bean.FinJsonBean;

public class IntentResolution extends FinJsonBean {
	private String source;
	private String version;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
