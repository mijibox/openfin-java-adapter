package com.mijibox.openfin;

import java.nio.file.Path;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public class FinRuntime {
	public final FinApplication Application;
	public final FinChannel Channel;
	public final FinClipboard Clipboard;
	public final FinGlobalHotkey GlobalHotkey;
	public final FinInterApplicationBus InterApplicationBus;
	public final FinLayout Layout;
	public final FinPlatform Platform;
	public final FinSystem System;
	public final FinView View;
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
	
	public String getConnectionUuid() {
		return this.finConnection.getUuid();
	}
	
	public FinRuntimeConnection getConnection() {
		return this.finConnection;
	}
	
	public Executor getExecutor() {
		return this.finConnection.executor;
	}
	
	public CompletionStage<Void> disconnect() {
		return this.finConnection.disconnect();
	}
	
	public String getAssetsUrl() {
		return this.assetsUrl;
	}
	
	public String getRequestedVersion() {
		return this.requestedVersion;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public boolean addConnectionListener(FinRuntimeConnectionListener connectionListener) {
		return this.finConnection.addConnectionListener(connectionListener);
	}
	
	public boolean removeConnectionListener(FinRuntimeConnectionListener connectionListener) {
		return this.finConnection.removeConnectionListener(connectionListener);
	}
}
