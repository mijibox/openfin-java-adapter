package com.mijibox.openfin.notifications;

import com.mijibox.openfin.bean.FinJsonBean;

/**
 * Details of the application that will handle any actions coming from a
 * particular push notification.
 * 
 * @author Anthony
 *
 */
public class FeedApplication extends FinJsonBean {

	private String manifestUrl;
	private String uuid;

	public String getManifestUrl() {
		return manifestUrl;
	}

	public void setManifestUrl(String manifestUrl) {
		this.manifestUrl = manifestUrl;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
