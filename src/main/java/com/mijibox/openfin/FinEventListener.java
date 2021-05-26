package com.mijibox.openfin;

/**
 * The OpenFin Runtime event listener.
 * @author Anthony
 *
 */
@FunctionalInterface
public interface FinEventListener {
	/**
	 * The method to be invoked when an event has been received.
	 * @param event OpenFin Runtime event
	 */
	void onEvent(FinEvent event);
}
