package com.mijibox.openfin;

import javax.json.JsonObject;

/**
 * OpenFin Runtime Event
 * @author Anthony
 */
public class FinEvent {
	private JsonObject eventObject;
	
	FinEvent(JsonObject eventObject) {
		this.eventObject = eventObject;
	}

	/**
	 * Gets the received event object in JSON.
	 * @return the event object.
	 */
	public JsonObject getEventObject() {
		return eventObject;
	}
}
