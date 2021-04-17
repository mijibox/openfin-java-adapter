package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinApplicationObject;
import com.mijibox.openfin.FinLauncher;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.FinRuntimeConnectionListener;
import com.mijibox.openfin.FinRuntimeLauncher;
import com.mijibox.openfin.FinRuntimeLauncherBuilder;
import com.mijibox.openfin.FinRvmLauncher;
import com.mijibox.openfin.FinRvmLauncherBuilder;
import com.mijibox.openfin.bean.ApplicationOptions;
import com.mijibox.openfin.bean.RuntimeConfig;
import com.sun.jna.Platform;

public class FinRuntimeLauncherTest {
	
	Logger logger = LoggerFactory.getLogger(FinRuntimeLauncherTest.class);
	
//	@Test
//	public void getRuntimeDirectory() throws Exception {
//		RuntimeLauncherOptions opts = new RuntimeLauncherOptions();
//		Path openFinDir = opts.getOpenFinDirectory();
//		Path runtimeDir = opts.getRuntimeDirectory();
//		assertNotNull(openFinDir);
//		assertNotNull(runtimeDir);
//		logger.info("openFinDir: {}", openFinDir.toAbsolutePath());
//		logger.info("runtimeDir: {}", runtimeDir.toAbsolutePath());
//	}
//	
//	@Test
//	public void getDefaultRuntimeVersion() throws Exception {
//		OpenFinRuntimeLauncher launcher = new OpenFinRuntimeLauncher();
//		String version = launcher.getRuntimeVersion().toCompletableFuture().get(10, TimeUnit.SECONDS);
//		logger.info("default version: {}", version);
//		assertNotNull(version);
//	}
//
//	@Test
//	public void getRuntimeChannelVersion() throws Exception {
//		RuntimeLauncherOptions opts = new RuntimeLauncherOptions();
//		opts.getRuntimeConfig().getRuntime().setVersion("stable-v9");
//		OpenFinRuntimeLauncher launcher = new OpenFinRuntimeLauncher(opts);
//		String version = launcher.getRuntimeVersion().toCompletableFuture().get(10, TimeUnit.SECONDS);
//		assertEquals("9.61.38.43", version);
//	}
//	
//	@Test
//	public void getStaticRuntimeVersion() throws Exception {
//		RuntimeLauncherOptions opts = new RuntimeLauncherOptions();
//		opts.getRuntimeConfig().getRuntime().setVersion("9.61.38.43");
//		OpenFinRuntimeLauncher launcher = new OpenFinRuntimeLauncher(opts);
//		String version = launcher.getRuntimeVersion().toCompletableFuture().get(10, TimeUnit.SECONDS);
//		assertEquals("9.61.38.43", version);
//	}
//
//	@Test
//	public void getRuntimeExecutablePath() throws Exception {
//		RuntimeLauncherOptions opts = new RuntimeLauncherOptions();
//		OpenFinRuntimeLauncher launcher = new OpenFinRuntimeLauncher(opts);
//		Path execPath = launcher.getRuntimeExecutablePath().toCompletableFuture().get(60, TimeUnit.SECONDS);
//		assertNotNull(execPath);
//		assertTrue(execPath.toFile().exists());
//	}
//	
//	@Test
//	public void runtimeLauncherlaunch() throws Exception {
//		RuntimeLauncherOptions opts = new RuntimeLauncherOptions();
//		opts.getRuntimeConfig().getRuntime().setArguments("--v=1", "--disable-gpu");
//		OpenFinRuntimeLauncher launcher = new OpenFinRuntimeLauncher(opts);
//		OpenFinConnection conn = launcher.launch().toCompletableFuture().get(120, TimeUnit.SECONDS);
//		assertNotNull(conn);
//		conn.disconnect();
//	}
//
//	@Test
//	public void rvmLauncherlaunch() throws Exception {
//		RvmLauncherOptions opts = new RvmLauncherOptions();
//		OpenFinRvmLauncher launcher = new OpenFinRvmLauncher(opts);
//		OpenFinConnection conn = launcher.launch().toCompletableFuture().get(120, TimeUnit.SECONDS);
//		assertNotNull(conn);
//		conn.disconnect();
//	}
	
	@Test
	public void rvmLauncherLaunch() throws Exception {
		assumeTrue(Platform.isWindows());
		FinRvmLauncher launcher = new FinRvmLauncher(new FinRvmLauncherBuilder());
		FinRuntime runtime = TestUtils.runSync(launcher.launch());
		assertNotNull(runtime);
		TestUtils.runSync(runtime.System.exit());
		Thread.sleep(2000);
	}
	
	@Test
	public void runtimeLauncherLaunch() throws Exception {
		FinRuntimeLauncherBuilder builder = new FinRuntimeLauncherBuilder();
		FinRuntimeLauncher launcher = new FinRuntimeLauncher(builder);
		FinRuntime runtime = TestUtils.runSync(launcher.launch());
		assertNotNull(runtime);
		TestUtils.runSync(runtime.System.exit());
		Thread.sleep(2000);
	}

	@Test
	public void launch() throws Exception {
		FinLauncher launcher = FinLauncher.newLauncherBuilder().build();
		FinRuntime runtime = TestUtils.runSync(launcher.launch());
		assertNotNull(runtime);
		TestUtils.runSync(runtime.System.exit());
		Thread.sleep(2000);
	}
	
	@Test
	public void connectionListenerOpenAndClose() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		FinLauncher launcher = FinLauncher.newLauncherBuilder().connectionListener(new FinRuntimeConnectionListener() {
			@Override
			public void onOpen(FinRuntime runtime) {
				runtime.disconnect();
			}
			
			@Override
			public void onClose(String reason) {
				logger.debug("connection closed, reason: {}", reason);
				latch.countDown();
			}
		}).build();

		FinRuntime runtime = TestUtils.runSync(launcher.launch());
		
		latch.await(10, TimeUnit.SECONDS);
		
		assertEquals(0, latch.getCount());
	}
	
	@Test
	public void duplicateConnectionUuid() throws Exception {
		String connectionUuid = "myConnectionUuid";
		CompletableFuture<?> exceptionFuture = new CompletableFuture<>();
		FinLauncher.newLauncherBuilder().connectionUuid(connectionUuid).build().launch().thenCompose(runtime1 ->{
			return FinLauncher.newLauncherBuilder().connectionUuid(connectionUuid).build().launch();
		}).exceptionally(e->{
			e.printStackTrace();
			exceptionFuture.complete(null);
			return null;
		});
		TestUtils.runSync(exceptionFuture);
	}

	@Test
	public void duplicateConnectionUuidWithDifferentRuntimeVersion() throws Exception {
		String connectionUuid = "myConnectionUuid";
		RuntimeConfig config1 = new RuntimeConfig();
		config1.getRuntime().setVersion("stable-v15");
		RuntimeConfig config2 = new RuntimeConfig();
		config2.getRuntime().setVersion("stable-v16");
		FinRuntime runtime1 = TestUtils.runSync(FinLauncher.newLauncherBuilder().connectionUuid(connectionUuid).runtimeConfig(config1).build().launch());
		FinRuntime runtime2 = TestUtils.runSync(FinLauncher.newLauncherBuilder().connectionUuid(connectionUuid).runtimeConfig(config2).build().launch().exceptionally(e->{
			logger.debug("error creating openfin connection", e);
			return null;
		}));
		
		assertNotNull(runtime1);
		assertNull(runtime2);
		TestUtils.runSync(runtime1.System.exit());
		Thread.sleep(2000);
	}
	
	@Test
	public void download() throws Exception {
		FinRuntimeLauncher runtimeLauncher = (FinRuntimeLauncher) new FinRuntimeLauncherBuilder().build();
		Path tmpFile = runtimeLauncher.download("/release/runtime/x64/19.89.57.15");
		assertNotNull(tmpFile);
		Files.delete(tmpFile);
	}

	@Ignore 
	@Test
	public void startUpAppSameUuid() throws Exception {
		String uuid = UUID.randomUUID().toString();
		RuntimeConfig config = new RuntimeConfig();
		config.setStartupApp(new ApplicationOptions(uuid));
		FinRuntime fin = TestUtils.runSync(FinLauncher.newLauncherBuilder().connectionUuid(uuid).runtimeConfig(config).build().launch().exceptionally(e->{
			//expected
			return null;
		}));
		assertNull(fin);
		//when using runtime launcher, it leaves the process hanging.
	}
	
	@Test
	public void rvmOptions() throws Exception {
		assumeTrue(Platform.isWindows());
		CompletableFuture<?> processExitFuture = new CompletableFuture<>();
		FinRvmLauncherBuilder builder = new FinRvmLauncherBuilder();
		RuntimeConfig config = new RuntimeConfig();
		config.getRuntime().setVersion("stable-v8");
		FinRvmLauncher launcher = (FinRvmLauncher) builder.doNotLaunch().noUi().runtimeConfig(config).build();
		launcher.startProcess().thenAccept(process->{
			process.onExit().thenAccept(v->{
				processExitFuture.complete(null);
			});
		});
		
		TestUtils.runSync(processExitFuture, 60);
	}
	
	@Test
	public void localHtml() throws Exception {
		ApplicationOptions startupApp = new ApplicationOptions(UUID.randomUUID().toString());
		startupApp.setUrl(TestUtils.getTestHtmlUrl("echo"));
		startupApp.setAutoShow(true);
		RuntimeConfig config = new RuntimeConfig();
		config.setStartupApp(startupApp);

		FinRuntime fin = TestUtils.runSync(FinLauncher.newLauncherBuilder().runtimeConfig(config).build().launch());

		Thread.sleep(2000);
		
		TestUtils.runSync(fin.System.exit());
		Thread.sleep(2000);
	}
	
	@Test
	public void localManifest() throws Exception {
		FinRuntime fin = TestUtils.runSync(FinLauncher.newLauncherBuilder().build().launch());
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("google")));
		TestUtils.runSync(app.getWindow().close());
		TestUtils.runSync(fin.System.exit());
		Thread.sleep(2000);
	}
	
	@Test
	public void skipPortDiscovery() throws Exception {
		FinRuntime runtime = TestUtils.runSync(new FinRvmLauncherBuilder().build().launch()); //start a runtime process, if 9696 is not already taken, then this one will take the default port
		assertNotNull(runtime);
		FinRuntime runtime1 = TestUtils.runSync(new FinRvmLauncherBuilder().runtimePort(9696).build().launch()); 
		assertNotNull(runtime1);
		TestUtils.runSync(runtime1.disconnect());
		Thread.sleep(1000);
		FinRuntime runtime2 = TestUtils.runSync(new FinRuntimeLauncherBuilder().runtimePort(9696).build().launch()); 
		assertNotNull(runtime2);
		TestUtils.runSync(runtime2.disconnect());
		TestUtils.runSync(runtime.disconnect());
	}

	@Test
	public void runtimeVersion() throws Exception {
		String requestedVersion = "18.87.57.37";
		FinRuntime runtime = TestUtils.runSync(new FinRvmLauncherBuilder().runtimeVersion(requestedVersion).build().launch());
		assertNotNull(runtime);
		assertEquals(requestedVersion, runtime.getVersion());
		TestUtils.runSync(runtime.disconnect());
		Thread.sleep(1000);
		FinRuntime runtime2 = TestUtils.runSync(new FinRuntimeLauncherBuilder().runtimeVersion(requestedVersion).build().launch());
		assertNotNull(runtime2);
		assertEquals(requestedVersion, runtime2.getVersion());
	}
}
