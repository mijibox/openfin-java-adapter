package com.mijibox.openfin.fdc3;

import com.mijibox.openfin.bean.FinJsonBean;

/**
 * The class used to describe an Intent within the platform.
 * @author Anthony
 *
 */
public class IntentMetadata extends FinJsonBean {
	
	private String name;
	private String displayName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
