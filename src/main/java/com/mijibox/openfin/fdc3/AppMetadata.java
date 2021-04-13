package com.mijibox.openfin.fdc3;

import com.mijibox.openfin.bean.FinJsonBean;

/**
 * App metadata is Desktop Agent specific - but should always support a name
 * property.
 * 
 * @author Anthony
 *
 */
public class AppMetadata extends FinJsonBean {
	private String appId;
	private String name;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
