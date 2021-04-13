package com.mijibox.openfin;

import java.nio.file.Path;
import java.util.concurrent.Executor;

import com.mijibox.openfin.bean.RuntimeConfig;

public interface FinLauncherBuilder {
	
	public FinLauncherBuilder connectionUuid(String uuid);

	public FinLauncherBuilder assetsUrl(String assetsUrl);

	public FinLauncherBuilder openFinDirectory(Path openFinDirectory);

	public FinLauncherBuilder runtimeConfig(RuntimeConfig runtimeConfig);

	public FinLauncherBuilder connectionListener(FinRuntimeConnectionListener listener);
	
	public FinLauncherBuilder executor(Executor executor);
	
	public FinLauncher build();
}
