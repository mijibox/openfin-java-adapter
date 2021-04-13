package com.mijibox.openfin.fdc3;

import javax.json.JsonValue;

import com.mijibox.openfin.fdc3.context.Context;

@FunctionalInterface
public interface IntentListener {
	public JsonValue onIntent(Context context);
}
