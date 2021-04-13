package com.mijibox.openfin;

@FunctionalInterface
public interface FinEventListener {
	void onEvent(FinEvent event);
}
