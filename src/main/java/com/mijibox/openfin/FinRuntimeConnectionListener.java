package com.mijibox.openfin;

public interface FinRuntimeConnectionListener {
	default void onOpen(FinRuntime runtime) {
	};

	default void onError(Throwable error) {
	};

	default void onClose(String reason) {
	};
}