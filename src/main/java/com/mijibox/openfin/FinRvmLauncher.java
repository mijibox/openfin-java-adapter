package com.mijibox.openfin;

import java.io.BufferedInputStream;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinRvmLauncher extends AbstractFinLauncher {
	private final static Logger logger = LoggerFactory.getLogger(FinRvmLauncher.class);
	private final FinRvmLauncherBuilder builder;

	public FinRvmLauncher(FinRvmLauncherBuilder builder) {
		super(builder);
		this.builder = builder;
	}

	public CompletionStage<String> getRvmLatestVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				String v = null;
				String latestVersionUrl = this.builder.getAssetsUrl() + "/release/rvm/latestVersion";
				logger.info("retrieving latest RVM version number from: {}", latestVersionUrl);
				URL url = new URL(latestVersionUrl);
				BufferedInputStream bis = new BufferedInputStream(url.openStream());
				v = new String(bis.readAllBytes());
				logger.info("Got RVM latestVersion: {}", v);
				bis.close();
				return v;
			}
			catch (Exception e) {
				logger.error("error getRVMLatestVersion", e);
				throw new RuntimeException("error getRVMLatestVersion", e);
			}
			finally {
			}
		}, builder.getExecutor());
	}

	@Override
	public CompletionStage<Path> getExecutablePath() {
		Path rvmPath = this.builder.getRvmInstallDirectory().resolve("OpenFinRVM.exe");
		return CompletableFuture.supplyAsync(()->{
			if (!Files.exists(rvmPath, LinkOption.NOFOLLOW_LINKS)) {
				logger.debug("{} not available.", rvmPath);
				try {
					String rvmTarget = "/release/rvm/latest";
					logger.info("download OpenFinRVM from {}", rvmTarget);
					Path rvmZip = this.download(rvmTarget);
					logger.debug("RVM downloaded, path: {}", rvmZip);
					this.unzip(rvmZip, this.builder.getRvmInstallDirectory());
					Files.delete(rvmZip);
					super.executablePath = rvmPath;
					return rvmPath;
				}
				catch (Exception e) {
					throw new RuntimeException("error downloading OpenFinRVM", e);
				}
			}
			else {
				logger.debug("OpenFinRVM executable located: {}", rvmPath);
				super.executablePath = rvmPath;
				return rvmPath;
			}
		}, this.builder.getExecutor());		
	}

	@Override
	public CompletionStage<Process> startProcess() {
		return this.getExecutablePath().thenApply(rvmPath -> {
			try {
				// rvm can handle runtime channel version
				List<String> command = new ArrayList<>();
				command.add(rvmPath.toAbsolutePath().normalize().toString());
				for (String s : this.builder.getRvmOptions()) {
					command.add(s);
				}
				command.add("--config=" + configPath.toUri().toString());

				logger.info("start process: {}", command);
				ProcessBuilder pb = new ProcessBuilder(command.toArray(new String[command.size()]))
						.redirectOutput(Redirect.DISCARD).redirectError(Redirect.DISCARD);
				return pb.start();
			}
			catch (Exception e) {
				logger.error("error launching OpenFinRVM", e);
				throw new RuntimeException("error launching OpenFinRVM", e);
			}
		});
	}

}