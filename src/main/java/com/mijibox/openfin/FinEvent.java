package com.mijibox.openfin;

import javax.json.JsonObject;

public class FinEvent {
	private JsonObject eventObject;
	
	public FinEvent(JsonObject eventObject) {
		this.eventObject = eventObject;
	}

	public JsonObject getEventObject() {
		return eventObject;
	}
}
