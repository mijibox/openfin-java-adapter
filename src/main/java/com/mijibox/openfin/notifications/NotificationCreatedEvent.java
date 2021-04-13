package com.mijibox.openfin.notifications;


/**
 * Event fired whenever a new notification has been created.
 * 
 * @author Anthony
 *
 */
public class NotificationCreatedEvent extends NotificationEvent {

	public NotificationCreatedEvent() {
		super(TYPE_CREATED);
	}

}
