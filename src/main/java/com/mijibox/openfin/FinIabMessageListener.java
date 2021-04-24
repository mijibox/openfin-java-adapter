package com.mijibox.openfin;

import javax.json.JsonValue;

import com.mijibox.openfin.bean.Identity;

/**
 * The OpenFin InterApplicationBus Listener
 * @author Anthony
 *
 */
@FunctionalInterface
public interface FinIabMessageListener {
	/**
	 * The method to be invoked when a message has been received.
	 * @param identity The identity of the sender of the message.
	 * @param jsonValue The content of the message.
	 */
	public void onMessage(Identity identity, JsonValue jsonValue);
}