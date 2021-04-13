package com.mijibox.openfin.notifications;

import com.mijibox.openfin.bean.FinJsonBean;
import com.mijibox.openfin.bean.Identity;

/**
 * For notifications that have originated from an application running on the
 * same machine as the provider.
 * 
 * @author Anthony
 *
 */
public class NotificationSourceDesktop extends FinJsonBean implements NotificationSource {

	private Identity identity;
	private String type;

	public NotificationSourceDesktop() {
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
