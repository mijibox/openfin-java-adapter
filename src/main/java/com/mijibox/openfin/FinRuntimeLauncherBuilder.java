package com.mijibox.openfin;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinRuntimeLauncherBuilder extends AbstractFinLauncherBuilder {
	private final static Logger logger = LoggerFactory.getLogger(FinRuntimeLauncherBuilder.class);
	
	private Path runtimeDirectory;

	public FinRuntimeLauncherBuilder() {
	}
	
	public FinRuntimeLauncherBuilder runtimeDirectory(Path runtimeDirectory) {
		this.runtimeDirectory = runtimeDirectory;
		return this;
	}
	
	Path getRuntimeDirectory() {
		if (this.runtimeDirectory == null) {
			this.runtimeDirectory = super.getOpenFinDirectory().resolve("Runtime");
			logger.debug("runtimeDirectory: {}", this.runtimeDirectory);
		}
		return this.runtimeDirectory;
	}

	@Override
	public FinLauncher build() {
		return new FinRuntimeLauncher(this);
	}

}
