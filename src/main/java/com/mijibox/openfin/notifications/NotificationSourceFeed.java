package com.mijibox.openfin.notifications;

import com.mijibox.openfin.bean.FinJsonBean;

/**
 * For notifications that have come from a cloud-hosted notification feed.
 * 
 * @author Anthony
 *
 */
public class NotificationSourceFeed extends FinJsonBean implements NotificationSource {

	private FeedApplication application;
	private String id;
	private String type;

	public FeedApplication getApplication() {
		return application;
	}

	public void setApplication(FeedApplication application) {
		this.application = application;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
