package com.mijibox.openfin;

import java.nio.file.Path;
import java.util.concurrent.Executor;

import com.mijibox.openfin.bean.RuntimeConfig;

public interface FinLauncherBuilder {
	
	/**
	 * Sets the UUID of the connection to OpenFin Runtime.
	 * @param uuid The UUID.
	 * @return This FinLauncherBuilder instance.
	 */
	public FinLauncherBuilder connectionUuid(String uuid);

	/**
	 * Sets the assetsUrl.
	 * @param assetsUrl The assets URL.
	 * @return This FinLauncherBuilder instance.
	 */
	public FinLauncherBuilder assetsUrl(String assetsUrl);

	/**
	 * Sets the path to the OpenFin directory in the file system.
	 * @param openFinDirectory The path of the OpenFin directory.
	 * @return This FinLauncherBuilder instance.
	 */
	public FinLauncherBuilder openFinDirectory(Path openFinDirectory);

	/**
	 * Sets the OpenFin runtime configuration.
	 * @param runtimeConfig The OpenFin runtime configuration.
	 * @return This FinLauncherBuilder instance.
	 */
	public FinLauncherBuilder runtimeConfig(RuntimeConfig runtimeConfig);
	
	/**
	 * Convenient method to set runtimeConfig.runtime.version.
	 * @param version The runtime version.
	 * @return This FinLauncherBuilder instance.
	 */
	public FinLauncherBuilder runtimeVersion(String version);
	
	/**
	 * Skips port discovery, connect directly to specified websocket port
	 * @param port The websocket port on OpenFin Runtime.
	 * @return This FinLauncherBuilder instance.
	 */
	public FinLauncherBuilder runtimePort(Integer port);

	/**
	 * Sets the OpenFin Runtime connection listener.
	 * @param listener The OpenFin Runtime connection listener.
	 * @return This FinLauncherBuilder instance.
	 */
	public FinLauncherBuilder connectionListener(FinRuntimeConnectionListener listener);
	
	/**
	 * Sets the Executor for asynchronous calls.
	 * @param executor The Executor to use for asynchronous calls.
	 * @return This FinLauncherBuilder instance.
	 */
	public FinLauncherBuilder executor(Executor executor);
	
	/**
	 * Builds the FinLauncher that can launch OpenFin Runtime process.
	 * @return A new FinLauncher.
	 */
	public FinLauncher build();
}
