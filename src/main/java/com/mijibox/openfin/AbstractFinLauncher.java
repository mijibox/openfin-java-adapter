package com.mijibox.openfin;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.ApplicationOptions;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.WindowOptions;
import com.sun.jna.Platform;

public abstract class AbstractFinLauncher implements FinLauncher {

	private final static Logger logger = LoggerFactory.getLogger(AbstractFinLauncher.class);
	private AbstractFinLauncherBuilder builder;
	protected Path executablePath;
	protected String requestedVersion;
	protected String version;
	protected String namedPipeName;
	protected Path configPath;

	AbstractFinLauncher(AbstractFinLauncherBuilder builder) {
		this.builder = builder;
		this.namedPipeName = UUID.randomUUID().toString();
		try {
			this.configPath = this.createStartupConfig(Platform.isWindows() ? namedPipeName
					: "/" + PosixPortDiscoverer.getNamedPipeFilePath(namedPipeName));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract CompletionStage<Path> getExecutablePath();
	public abstract CompletionStage<Process> startProcess();

	protected Path download(String target) throws Exception {
		long startTime = System.currentTimeMillis();
		URL url = new URL(this.builder.getAssetsUrl() + target);
		logger.info("download: {}", url.toString());
		ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
		Path tempFile = Files.createTempFile("OpenFin", null);
		// Path tempFile = Paths.get("./runtime-" + this.runtimeVersion + ".zip");
		FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile());

		int pos = 0;
		long size = 0;
		int length = (1024 * 1024);
		long reportTime = startTime;
		while ((size = fileOutputStream.getChannel().transferFrom(readableByteChannel, (pos += size), length)) > 0) {
			long now = System.currentTimeMillis();
			if (now - reportTime > 1000) {
				reportTime = now;
				logger.debug("downloading {}, from pos={}, size={}", target, pos, size);
			}
		}
		logger.debug("{} downloaded, path: {}, size: {}, time spent: {}ms", url.toString(), tempFile, pos,
				(System.currentTimeMillis() - startTime));
		fileOutputStream.close();
		return tempFile;
	}

	protected void unzip(Path zipFilePath, Path targetFolder) throws IOException {
		logger.debug("unzip {} to {}", zipFilePath, targetFolder);
		Files.createDirectories(targetFolder);
		ZipFile zipFile = new ZipFile(zipFilePath.toFile());
		Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

		// need to do the symbolics the last
		List<ZipArchiveEntry> symLinks = new ArrayList<>();

		while (entries.hasMoreElements()) {
			ZipArchiveEntry entry = entries.nextElement();
			Path filePath = targetFolder.resolve(entry.getName());
			logger.debug("unzipping {}, size: {}", filePath, entry.getSize());
			// incase parent folder is not created yet.
			Files.createDirectories(filePath.getParent());
			if (entry.isUnixSymlink()) {
				logger.debug("symLink, do it later: {}");
				symLinks.add(entry);
			}
			else if (!entry.isDirectory()) {
				Files.createFile(filePath);
				ReadableByteChannel readableByteChannel = Channels.newChannel(zipFile.getInputStream(entry));
				FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
				long size = fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
				if (Platform.isLinux() || Platform.isMac()) {
					int unixMode = entry.getUnixMode();
					if ((unixMode & UnixStat.PERM_MASK & 01111) != 0) {
						logger.debug("can execute: {}", filePath);
						filePath.toFile().setExecutable(true);
					}
				}
				logger.debug("File extracted, path: {}, size: {}", filePath, size);
				fileOutputStream.close();
			}
			else {
				Files.createDirectories(filePath);
				logger.debug("created directory: {}", filePath);
			}
		}

		while (symLinks.size() > 0) {
			ZipArchiveEntry entry = symLinks.remove(0);
			Path filePath = targetFolder.resolve(entry.getName());
			BufferedReader br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));
			String linkTarget = br.readLine();
			br.close();
			try {
				Path targetPath = filePath.getParent().resolve(linkTarget);
				logger.debug("creating link from {} to {}", filePath, targetPath);
				if (Files.exists(targetPath)) {
					Files.createSymbolicLink(filePath, Paths.get(linkTarget));
				}
				else {
					logger.debug("target doesn't exist, do it later");
					symLinks.add(entry);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		zipFile.close();
	}

	private Path createStartupConfig(String namedPipeName) throws IOException {
		JsonObject runtimeConfigJson = FinBeanUtils.toJsonObject(this.builder.getRuntimeConfig());
		if (runtimeConfigJson.getJsonObject("runtime").containsKey("arguments")) {
			String orgArgs = runtimeConfigJson.getJsonObject("runtime").getString("arguments");
			runtimeConfigJson = Json.createPatchBuilder()
					.replace("/runtime/arguments", orgArgs + " --runtime-information-channel-v6=" + namedPipeName)
					.build().apply(runtimeConfigJson);
		}
		else {
			runtimeConfigJson = Json.createPatchBuilder()
					.add("/runtime/arguments", "--runtime-information-channel-v6=" + namedPipeName).build()
					.apply(runtimeConfigJson);
		}

		Path config = Files.createTempFile("OpenFinRuntimeConfig", ".json");

		Jsonb jsonb = JsonbBuilder.newBuilder().build();

		logger.debug("runtime config: {}", jsonb.toJson(runtimeConfigJson));

		jsonb.toJson(runtimeConfigJson, new FileWriter(config.toFile()));
		
		this.configPath = config;

		return config;
	}

	CompletionStage<JsonObject> getPortInfo(String namedPipeName) {
		if (Platform.isWindows()) {
			WindowsPortDiscoverer portDiscoverer = new WindowsPortDiscoverer(builder.getExecutor());
			return portDiscoverer.getPortInfo(namedPipeName);
		}
		else if (Platform.isLinux() || Platform.isMac()) {
			PosixPortDiscoverer portDiscoverer = new PosixPortDiscoverer(builder.getExecutor());
			return portDiscoverer.getPortInfo(namedPipeName);
		}
		else {
			return null;
		}
	}

	CompletionStage<Integer> findPortNumber(String namedPipeName) {
		return getPortInfo(namedPipeName).thenApply(portInfo -> {
			if (portInfo == null) {
				logger.debug("unable to get portInfo, returning default port 9696");
				return 9696;
			}
			else {
				JsonObject payload = portInfo.getJsonObject("payload");
				this.version = payload.getString("version");
				this.requestedVersion = payload.containsKey("requestedVersion") ? payload.getString("requestedVersion")
						: version;
				int port = payload.getInt("port");
				logger.info("requested version: {}", requestedVersion);
				logger.info("version: {}", version);
				logger.debug("port: {}", port);
				return port;
			}
		});
	}
	
	public CompletionStage<FinConnectionImpl> getOpenFinRuntimeConnection() {
		return this.startProcess().thenCompose(process->{
			return this.findPortNumber(namedPipeName);
		}).thenApply(port->{
			return new FinConnectionImpl(this.builder.getConnectionUuid(), port,
					this.builder.getRuntimeConfig().getLicenseKey(), configPath.toUri().toString(),
					builder.getExecutor(), builder.getRuntimeConfig().getNonPersistent());
		}).thenCompose(connection -> {
			return connection.connect();
		});
	}
	

	@Override
	public CompletionStage<FinRuntime> launch() {
		return this.getOpenFinRuntimeConnection().thenApply(conn -> {
			FinRuntime runtime = new FinRuntime(conn);
			runtime.requestedVersion = this.requestedVersion;
			runtime.version = this.version;
			runtime.assetsUrl = this.builder.getAssetsUrl();
			runtime.executablePath = this.executablePath;
			FinRuntimeConnectionListener connListener = builder.getConnectionListener();
			if (connListener != null) {
				connListener.onOpen(runtime);
				conn.addConnectionListener(connListener);
			}
			return runtime;
		});
	}
}