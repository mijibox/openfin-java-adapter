package com.mijibox.openfin.fdc3;

@FunctionalInterface
public interface EventListener {
	public void onEvent(Fdc3Event event);
}
