package com.mijibox.openfin.notifications;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import com.mijibox.openfin.bean.FinJsonBean;

public class NotificationEvent extends FinJsonBean {
	
	public final static String TYPE_ACTION = "notification-action";
	public final static String TYPE_CREATED = "notification-created";
	public final static String TYPE_CLOSED = "notification-closed";

	private String type;
	@JsonbProperty("notification")
	private NotificationOptions notification;
	
	@JsonbCreator
	public NotificationEvent(@JsonbProperty("type")String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public NotificationOptions getNotification() {
		return notification;
	}

	public void setNotification(NotificationOptions notificationOpts) {
		this.notification = notificationOpts;
	}

}
