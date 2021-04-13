package com.mijibox.openfin.notifications;


/**
 * Event fired whenever the notification has been closed.
 * 
 * This event is fired regardless of how the notification was closed - i.e.: via
 * a call to clear/clearAll, the notification expiring, or by a user clicking
 * either the notification itself, the notification's close button, or a button
 * on the notification.
 * 
 * @author Anthony
 *
 */
public class NotificationClosedEvent extends NotificationEvent {

	public NotificationClosedEvent() {
		super(TYPE_CLOSED);
	}

}
