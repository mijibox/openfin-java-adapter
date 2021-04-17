package com.mijibox.openfin;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Platform;

public class FinRuntimeLauncher extends AbstractFinLauncher {
	private final static Logger logger = LoggerFactory.getLogger(FinRuntimeLauncher.class);
	private FinRuntimeLauncherBuilder builder;

	public FinRuntimeLauncher(FinRuntimeLauncherBuilder builder) {
		super(builder);
		this.builder = builder;
	}

	public CompletionStage<String> getRuntimeVersion() {
		String v = this.builder.getRuntimeConfig().getRuntime().getVersion();
		return CompletableFuture.supplyAsync(() -> {
			if (v != null && v.contains(".")) {
				return v;
			}
			else {
				String downloadPath;
				if (v == null) {
					downloadPath = "/release/runtime/stable";
				}
				else {
					downloadPath = "/release/runtime/" + v;
				}
				try {
					Path f = this.download(downloadPath);
					String runtimeVersion = new String(Files.readAllBytes(f));
					Files.delete(f);
					return runtimeVersion.trim();
				}
				catch (Exception e) {
					throw new RuntimeException("unable to get runtime version", e);
				}
			}
		}, builder.getExecutor());
	}

	@Override
	public CompletionStage<Path> getExecutablePath() {
		return this.getRuntimeVersion().thenApply(runtimeVersion -> {
			logger.debug("runtime version: {}", runtimeVersion);
			Path runtimePath;
			if (Platform.isWindows()) {
				runtimePath = this.builder.getRuntimeDirectory()
						.resolve(Paths.get(runtimeVersion, "OpenFin/openfin.exe"));
			}
			else if (Platform.isLinux()) {
				runtimePath = this.builder.getRuntimeDirectory()
						.resolve(Paths.get(runtimeVersion, "openfin"));
			}
			else if (Platform.isMac()) {
				runtimePath = this.builder.getRuntimeDirectory()
						.resolve(Paths.get(runtimeVersion, "OpenFin.app/Contents/MacOS/OpenFin"));
			}
			else {
				throw new RuntimeException("OpenFin runtime unsupported on this platform");
			}
			logger.debug("runtimePath: {}", runtimePath);
			if (!Files.exists(runtimePath, LinkOption.NOFOLLOW_LINKS)) {
				logger.debug("{} not available.", runtimePath);
				try {
					String target = null;
					if (Platform.isWindows() && Platform.is64Bit()) {
						target = "/release/runtime/x64/" + runtimeVersion;
					}
					else if (Platform.isWindows()) {
						target = "/release/runtime/" + runtimeVersion;
					}
					else if (Platform.isLinux() && Platform.isARM()) {
						target = "/release/runtime/linux/arm/" + runtimeVersion;
					}
					else if (Platform.isLinux()) {
						target = "/release/runtime/linux/x64/" + runtimeVersion;
					}
					else if (Platform.isMac()) {
						target = "/release/runtime/mac/x64/" + runtimeVersion;
					}

					if (target != null) {
						Path runtimeZip = this.download(target);
						this.unzip(runtimeZip,
								this.builder.getRuntimeDirectory().resolve(runtimeVersion));
						Files.delete(runtimeZip);
						super.executablePath = runtimePath;
						return runtimePath;
					}
					else {
						throw new RuntimeException("no applicable OpenFin runtime available.");
					}
				}
				catch (Exception e) {
					throw new RuntimeException("error downloading OpenFin runtime", e);
				}
			}
			else {
				logger.debug("OpenFin runtime executable located: {}", runtimePath);
				super.executablePath = runtimePath;
				return runtimePath;
			}
		});
	}

	@Override
	public CompletionStage<Process> startProcess() {
		return getExecutablePath().thenApply(runtimePath -> {
			try {
				List<String> command = new ArrayList<>();
				command.add(runtimePath.toAbsolutePath().normalize().toString());

				if (this.builder.getRuntimeConfig().getRuntime().getArguments() != null) {
					for (String arg : this.builder.getRuntimeConfig().getRuntime().getArguments()) {
						command.add(arg);
					}
				}

				if (Platform.isWindows()) {
					command.add("--user-data-dir="
							+ this.builder.getOpenFinDirectory().normalize().toAbsolutePath().toString());
					command.add("--runtime-information-channel-v6=" + namedPipeName);
					command.add("--startup-url=" + configPath.normalize().toAbsolutePath().toUri());
				}
				else {
					command.add("--user-data-dir=/"
							+ this.builder.getOpenFinDirectory().normalize().toAbsolutePath().toString());
					command.add("--runtime-information-channel-v6=/"
							+ PosixPortDiscoverer.getNamedPipeFilePath(namedPipeName));
					command.add("--startup-url=file:///" + configPath.normalize().toAbsolutePath().toString());
				}
				logger.info("start process: {}", command);
				ProcessBuilder pb = new ProcessBuilder(command.toArray(new String[command.size()])).inheritIO();
				return pb.start();
			}
			catch (Exception e) {
				logger.error("error launching OpenFin runtime", e);
				throw new RuntimeException("error launching OpenFin runtime", e);
			}
		});
	}
}