package com.mijibox.openfin;

import javax.json.JsonValue;

import com.mijibox.openfin.bean.Identity;

@FunctionalInterface
public interface FinChannelAction {
	/**
	 * Channel action to be invoked.
	 * @param payload Payload sent along when invoking the action.
	 * @param senderIdentity Identity of the sender.
	 * @return Result by invoking the action..
	 */
	public JsonValue invoke(JsonValue payload, Identity senderIdentity);
}
