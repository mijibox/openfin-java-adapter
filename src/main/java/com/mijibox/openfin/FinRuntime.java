package com.mijibox.openfin;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

public class FinRuntime {
	/**
	 * API Object for OpenFin Application
	 */
	public final FinApplication Application;
	/**
	 * API Object for OpenFin Message Channel
	 */
	public final FinChannel Channel;
	/**
	 * API Object for OpenFin Clipboard
	 */
	public final FinClipboard Clipboard;
	/**
	 * API Object for OpenFin Global Hotkeys
	 */
	public final FinGlobalHotkey GlobalHotkey;
	/**
	 * API Object for OpenFin Inter-Application Message Bus
	 */
	public final FinInterApplicationBus InterApplicationBus;
	/**
	 * API Object for OpenFin Layout
	 */
	public final FinLayout Layout;
	/**
	 * API Object for OpenFin Platform
	 */
	public final FinPlatform Platform;
	/**
	 * API Object for OpenFin System
	 */
	public final FinSystem System;
	/**
	 * API Object for OpenFin View
	 */
	public final FinView View;
	/**
	 * API Object for OpenFin Window
	 */
	public final FinWindow Window;
	
	final FinSubscriptionManager SubscriptionManager;
	
	private final FinConnectionImpl finConnection;
	
	String requestedVersion;
	String version;
	String assetsUrl;
	Path executablePath;

	FinRuntime(FinConnectionImpl conn) {
		this.finConnection = conn;
		this.Application = this.finConnection._application;
		this.Channel = this.finConnection._channel;
		this.Clipboard = this.finConnection._clipboard;
		this.GlobalHotkey = this.finConnection._globalHotkey;
		this.InterApplicationBus = this.finConnection._interApplicationBus;
		this.Layout = this.finConnection._layout;
		this.Platform = this.finConnection._platform;
		this.System = this.finConnection._system;
		this.View = this.finConnection._view;
		this.Window = this.finConnection._window;
		this.SubscriptionManager = this.finConnection._subscriptionManager;
	}
	
	/**
	 * Get the connection UUID, same as getConnection().getUuid();
	 * @return The connection UUID.
	 */
	public String getConnectionUuid() {
		return this.finConnection.getUuid();
	}
	
	/**
	 * Get the connection instance between Java and OpenFin Runtime
	 * @return The connection object.
	 */
	public FinRuntimeConnection getConnection() {
		return this.finConnection;
	}
	
	/**
	 * Get the executor that's used for asynchronous calls.
	 * @return The executor.
	 */
	public Executor getExecutor() {
		return this.finConnection.executor;
	}
	
	/**
	 * Disconnects the current connection between Java and OpenFin Runtime
	 * @return The new CompletionStage after the request is sent.
	 */
	public CompletionStage<Void> disconnect() {
		return this.finConnection.disconnect();
	}
	
	/**
	 * Gets the assetUrl setting for this OpenFin runtime instance.
	 * @return The setting of assetUrl.
	 */
	public String getAssetsUrl() {
		return this.assetsUrl;
	}
	
	/**
	 * Gets the request OpenFin Runtime version.
	 * @return The requested OpenFin Runtime version.
	 */
	public String getRequestedVersion() {
		return this.requestedVersion;
	}
	
	/**
	 * Gets the version number of the OpenFin Runtime instance.
	 * @return The version of this OpenFin Runtime.
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * Adds a OpenFin runtime connection listener
	 * @param connectionListener The connection listener.
	 * @return true if the listener is added to the end of the listener list.
	 */
	public boolean addConnectionListener(FinRuntimeConnectionListener connectionListener) {
		return this.finConnection.addConnectionListener(connectionListener);
	}
	
	/**
	 * Removes specified OpenFin runtime connection listener
	 * @param connectionListener The connection listener.
	 * @return true if the listener is removed from the listener list.
	 */
	public boolean removeConnectionListener(FinRuntimeConnectionListener connectionListener) {
		return this.finConnection.removeConnectionListener(connectionListener);
	}

	/**
	 * Check if it's currently connected to an OpenFin Runtime instance. Same as this.getConnection().isConnected()
	 * @return true if currently connected to an OpenFin Runtime instance.
	 */
	public boolean isConnected() {
		return this.finConnection.isConnected();
	}
}
