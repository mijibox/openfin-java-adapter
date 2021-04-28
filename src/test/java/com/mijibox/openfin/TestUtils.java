package com.mijibox.openfin;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.mijibox.openfin.FinLauncher;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.bean.RuntimeConfig;
import com.mijibox.openfin.bean.Service;

public class TestUtils {

	private static ConcurrentHashMap<String, FinRuntime> runtimeMap;
	private static int runSyncTimeout;
	private static String runtimeVersion;

	static {
		runtimeVersion = System.getProperty("openfin.runtime.version", "stable");
		String strTimeout = System.getProperty("openfin.runsync.timeout", "10");
		runSyncTimeout = Integer.parseInt(strTimeout);
	}

	public static FinRuntime getOpenFinRuntime() {
		return getOpenFinRuntime(runtimeVersion);
	}

	public static FinRuntime getOpenFinRuntime(String version) {
		return getOpenFinRuntime(version, false);
	}
	
	public static FinRuntime getOpenFinRuntime(String version, boolean enableFdc3) {
		RuntimeConfig config = new RuntimeConfig();
		config.setLicenseKey("JavaUnitTestLicenseKey");
		config.getRuntime().setArguments("--v=1");
		config.getRuntime().setVersion(version);
		if (enableFdc3) {
			config.setServices(new Service("fdc3"));
		}
		FinLauncher launcher = FinLauncher.newLauncherBuilder()
				.runtimeConfig(config)
				.build();
		return runSync(launcher.launch(), 180);
	}
	
	public static String getTestManifestUrl(String appName) {
		try {
			URI uri = TestUtils.class.getClassLoader().getResource("test_apps/" + appName + ".json").toURI();
			return uri.getScheme() + "://" + uri.getSchemeSpecificPart();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getTestHtmlUrl(String appName) {
		try {
			URI uri = TestUtils.class.getClassLoader().getResource("test_apps/" + appName + ".html").toURI();
			return uri.getScheme() + "://" + uri.getSchemeSpecificPart();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T runSync(CompletionStage<T> future) {
		return runSync(future, runSyncTimeout);
	}

	public static <T> T runSync(CompletionStage<T> future, int timeoutInSeconds) {
		try {
			return future.toCompletableFuture().exceptionally(e -> {
				throw new RuntimeException(e);
			}).get(timeoutInSeconds, TimeUnit.SECONDS);
		}
		catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}
}
