package com.mijibox.openfin;

import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;
import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.RuntimeConfig;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Advapi32Util;

public abstract class AbstractFinLauncherBuilder implements FinLauncherBuilder {
	private final static Logger logger = LoggerFactory.getLogger(AbstractFinLauncherBuilder.class);
	
	protected String assetsUrl;
	protected Path openFinDirectory;
	protected RuntimeConfig runtimeConfig;
	protected FinRuntimeConnectionListener listener;
	protected String connectionUuid;
	protected Executor executor;

	@Override
	public FinLauncherBuilder assetsUrl(String assetsUrl) {
		this.assetsUrl = assetsUrl;
		return this;
	}
	
	String getAssetsUrl() {
		if (this.assetsUrl == null) {
			this.assetsUrl = "https://cdn.openfin.co";
		}
		return assetsUrl;
	}


	@Override
	public FinLauncherBuilder openFinDirectory(Path openFinDirectory) {
		this.openFinDirectory = openFinDirectory;
		return this;
	}
	
	Path getOpenFinDirectory() {
		if (this.openFinDirectory == null) {
			if (Platform.isWindows()) {
				String dir = null;
				if (Advapi32Util.registryValueExists(HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\OpenFin\\RVM\\Settings", "rvmInstallDirectory")) {
					dir = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE, "Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\OpenFin\\RVM\\Settings", "rvmInstallDirectory");
				}
				if (dir == null && Advapi32Util.registryValueExists(HKEY_LOCAL_MACHINE, "Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\OpenFin\\RVM\\Settings", "rvmInstallDirectory")) {
					dir = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE, "Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\OpenFin\\RVM\\Settings", "rvmInstallDirectory");
				}
				if (dir == null && Advapi32Util.registryValueExists(HKEY_CURRENT_USER, "SOFTWARE\\OpenFin\\RVM\\Settings\\Deployment", "rvmInstallDirectory")) {
					dir = Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER, "SOFTWARE\\OpenFin\\RVM\\Settings\\Deployment", "rvmInstallDirectory");
				}
				if (dir == null && Advapi32Util.registryValueExists(HKEY_LOCAL_MACHINE, "SOFTWARE\\OpenFin\\RVM\\Settings\\Deployment", "rvmInstallDirectory")) {
					dir = Advapi32Util.registryGetStringValue(HKEY_LOCAL_MACHINE, "SOFTWARE\\OpenFin\\RVM\\Settings\\Deployment", "rvmInstallDirectory");
				}
				if (dir != null) {
					this.openFinDirectory = Paths.get(dir);
					logger.debug("openFinDirectory: {}", this.openFinDirectory);
				}
				else {
					String localAppData = System.getenv("LOCALAPPDATA");
					if (localAppData == null) {
						throw new RuntimeException("unable to determine OpenFin directory");
					}
					else {
						this.openFinDirectory = Paths.get(localAppData, "OpenFin");
						logger.debug("openFinDirectory: {}", this.openFinDirectory);
					}
				}
			}
			else {
				this.openFinDirectory = Paths.get(System.getProperty("user.home"), "OpenFin");
				logger.debug("openFinDirectory: {}", this.openFinDirectory);
			}
		}
		return this.openFinDirectory;
	}

	@Override
	public FinLauncherBuilder runtimeConfig(RuntimeConfig runtimeConfig) {
		this.runtimeConfig = runtimeConfig;
		return this;
	}
	
	RuntimeConfig getRuntimeConfig() {
		if (runtimeConfig == null) {
			this.runtimeConfig = new RuntimeConfig();
		}
		return runtimeConfig;
	}

	@Override
	public FinLauncherBuilder connectionListener(FinRuntimeConnectionListener listener) {
		this.listener = listener;
		return this;
	}
	
	FinRuntimeConnectionListener getConnectionListener() {
		return this.listener;
	}
	
	@Override
	public FinLauncherBuilder connectionUuid(String connectionUuid) {
		this.connectionUuid = connectionUuid;
		return this;
	}
	
	String getConnectionUuid() {
		if (this.connectionUuid == null) {
			this.connectionUuid = UUID.randomUUID().toString();
		}
		return this.connectionUuid;
	}
	
	@Override
	public FinLauncherBuilder executor(Executor executor) {
		this.executor = executor;
		return this;
	}
	
	Executor getExecutor() {
		if (this.executor == null) {
			this.executor = ForkJoinPool.commonPool();
		}
		return this.executor;
	}
	
}
