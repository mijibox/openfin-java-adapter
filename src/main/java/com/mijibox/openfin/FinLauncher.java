package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import com.sun.jna.Platform;

public interface FinLauncher {

	/**
	 * Gets a new FinLauncherBuilder.
	 * @return The new FinLauncherBuilder.
	 */
	public static FinLauncherBuilder newLauncherBuilder() {
		if (Platform.isWindows()) {
			return new FinRvmLauncherBuilder();
		}
		else {
			return new FinRuntimeLauncherBuilder();
		}
	}
	
	/**
	 * Launch OpenFin Runtime and get the runtime object that communicates to it.
	 * @return OpenFinRuntime object
	 */
	public CompletionStage<FinRuntime> launch();
}
