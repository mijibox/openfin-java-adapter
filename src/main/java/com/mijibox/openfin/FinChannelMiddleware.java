package com.mijibox.openfin;

import javax.json.JsonValue;

import com.mijibox.openfin.bean.Identity;

@FunctionalInterface
public interface FinChannelMiddleware {
	/**
	 * Channel action to be invoked.
	 * @param action Name of the action to be registered
	 * @param payload Payload sent along when invoking the action.
	 * @param senderIdentity Identity of the sender.
	 * @return Result by invoking the action..
	 */
	public JsonValue invoke(String action, JsonValue payload, Identity senderIdentity);

}
