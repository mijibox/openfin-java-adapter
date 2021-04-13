package com.mijibox.openfin.notifications;

/**
 * Listener that handles notification events.
 * @author Anthony
 *
 */
@FunctionalInterface
public interface NotificationEventListener {
	public void onEvent(NotificationEvent event);
}
