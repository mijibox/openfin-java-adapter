package com.mijibox.openfin;

/**
 * The listener to the OpenFin Runtime Connection.
 * @author Anthony
 *
 */
public interface FinRuntimeConnectionListener {
	/**
	 * The method to be invoked when the connection to an OpenFin Runtime is established.
	 * @param runtime The OpenFin Runtime that's been connected to.
	 */
	default void onOpen(FinRuntime runtime) {
	};

	/**
	 * The method to be invoked when there is an error with the connection.
	 * @param error The Throwable that caused the error.
	 */
	default void onError(Throwable error) {
	};

	/**
	 * The method to be invoked when the connection is disconnected.
	 * @param reason The reason for the disconnection.
	 */
	default void onClose(String reason) {
	};
}