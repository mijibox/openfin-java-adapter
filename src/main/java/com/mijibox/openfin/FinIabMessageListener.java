package com.mijibox.openfin;

import javax.json.JsonValue;

import com.mijibox.openfin.bean.Identity;

@FunctionalInterface
public interface FinIabMessageListener {
	public void onMessage(Identity identity, JsonValue jsonValue);
}