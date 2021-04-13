package com.mijibox.openfin.notifications;

import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbTypeDeserializer;

/**
 * Event fired when an action is raised for a notification due to a specified
 * trigger.
 * 
 * @author Anthony
 *
 */
public class NotificationActionEvent extends NotificationEvent {

	private String trigger;
	@JsonbTypeDeserializer(NotificationSourceDeserializer.class)
	private NotificationSource source;
	private JsonValue result;

	public NotificationActionEvent() {
		super(TYPE_ACTION);
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public NotificationSource getSource() {
		return source;
	}

	public void setSource(NotificationSource source) {
		this.source = source;
	}

	public JsonValue getResult() {
		return result;
	}

	public void setResult(JsonValue result) {
		this.result = result;
	}

	/**
	 * Indicates what triggered this action.
	 * 
	 * Trigger can be one of the following:
	 * 
	 * <p>
	 * "close": The notification was closed, either by user interaction,
	 * programmatically by an application, or by the notification expiring.
	 * </p>
	 * 
	 * <p>
	 * "control": The user interacted with one of the controls within the
	 * notification. This currently means a button click, but other control types
	 * will be added in future releases.
	 * </p>
	 * 
	 * <p>
	 * "expire": The notification expired.
	 * </p>
	 * 
	 * <p>
	 * "programmatic": The action was triggered programmatically by an application.
	 * Not currently supported - will be implemented in a future release.
	 * </p>
	 * 
	 * <p>
	 * "select": The user clicked the body of the notification itself. Any clicks of
	 * the notification that don't hit a control or the close button will fire an
	 * event with the 'select' action trigger.
	 * </p>
	 * 
	 * @return The trigger of this action.
	 */

	/**
	 * Notifications can be created by both desktop applications and as push
	 * notifications from a notification feed.
	 * 
	 * This method allows the application handling the action to identify where
	 * this notification originated.
	 * 
	 * @return The source of the notification.
	 */

	/**
	 * Get application-defined metadata that this event is passing back to the application.
	 * @return Application-defined metadata.
	 */


}
