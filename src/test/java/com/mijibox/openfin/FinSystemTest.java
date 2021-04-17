package com.mijibox.openfin;

import static com.mijibox.openfin.TestUtils.getOpenFinRuntime;
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinApplicationObject;
import com.mijibox.openfin.FinEventListener;
import com.mijibox.openfin.FinLauncher;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.FinRuntimeConnectionListener;
import com.mijibox.openfin.bean.AppInfo;
import com.mijibox.openfin.bean.ApplicationInfo;
import com.mijibox.openfin.bean.ApplicationOptions;
import com.mijibox.openfin.bean.ClearCacheOption;
import com.mijibox.openfin.bean.CookieDetails;
import com.mijibox.openfin.bean.CookieInfo;
import com.mijibox.openfin.bean.MonitorInfo;
import com.mijibox.openfin.bean.RuntimeConfig;
import com.mijibox.openfin.bean.RuntimeInfo;
import com.mijibox.openfin.bean.RvmInfo;
import com.mijibox.openfin.bean.WinInfo;
import com.mijibox.openfin.bean.WindowOptions;
import com.sun.jna.Platform;

public class FinSystemTest {
	private final static Logger logger = LoggerFactory.getLogger(FinSystemTest.class);
	
	private static String version = "19.89.57.15";
	private static FinRuntime fin;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime(version);
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.runSync(fin.System.exit());
	}

	@Test
	public void getVersion() throws Exception {
		String v = fin.System.getVersion().toCompletableFuture().get(60, TimeUnit.SECONDS);
		logger.info("verson: {}", v);
		assertNotNull(v);
		assertEquals(version, v);
	}

	@Test
	public void getCommandLineArguments() throws Exception {
		String args = fin.System.getCommandLineArguments().toCompletableFuture().get(60, TimeUnit.SECONDS);
		logger.info("command line arguments: {}", args);
		assertFalse(args.isEmpty());
	}
	
	@Test
	public void addRemoveEventListener() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		CountDownLatch latch2 = new CountDownLatch(2);
		String eventType = "application-closed";
		FinEventListener listener = eventObj ->{
			logger.debug("application closed, eventObj: {}", eventObj);
			latch.countDown();
			latch2.countDown();
		};
		TestUtils.runSync(fin.System.addEventListener(eventType, listener));
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		appObj.terminate();
		latch.await(20, TimeUnit.SECONDS);
		assertEquals(0, latch.getCount());
		TestUtils.runSync(fin.System.removeEventListener(eventType, listener));
		FinApplicationObject appObj2 = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		appObj2.terminate();
		latch2.await(10, TimeUnit.SECONDS);
		assertEquals(1, latch2.getCount());
		TestUtils.runSync(fin.System.addEventListener(eventType, listener));
		FinApplicationObject appObj3 = fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json").toCompletableFuture().get(10, TimeUnit.SECONDS);
		appObj3.terminate();
		latch2.await(10, TimeUnit.SECONDS);
		assertEquals(0, latch2.getCount());
	}

	@Test
	public void clearCache() throws Exception {
		ClearCacheOption opts = new ClearCacheOption();
		opts.setCookies(true);
		fin.System.clearCache(opts).toCompletableFuture().get(60, TimeUnit.SECONDS);
		
		fin.System.clearCache(null).toCompletableFuture().get(60, TimeUnit.SECONDS);
	}
	
	
	@Test
	public void exit() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		RuntimeConfig config = new RuntimeConfig();
		config.getRuntime().setVersion("stable-v17"); //intentionally use different version, otherwise it might exit the current runtime.
		FinRuntime runtime = TestUtils.runSync(FinLauncher.newLauncherBuilder()
				.runtimeConfig(config)
				.connectionListener(new FinRuntimeConnectionListener() {
					@Override
					public void onClose(String reason) {
						latch.countDown();
					}
				}).build().launch().exceptionally(e->{
					e.printStackTrace();
					return null;
				}), 180);
		TestUtils.runSync(runtime.System.exit());
		
		latch.await(10, TimeUnit.SECONDS);
		assertEquals(0, latch.getCount());
	}

	@Test
	public void getRuntimeInfo() throws Exception {
		RuntimeInfo runtimeInfo = TestUtils.runSync(fin.System.getRuntimeInfo());
		logger.debug("runtiemInfo.port: {}", runtimeInfo.getPort());
		
		Map<String, Object> args = runtimeInfo.getArgs();
		args.forEach((key, value)->{
			logger.debug("runtiemInfo.args, name: {}, value: {}", key, value);
		});
		assertNotNull(runtimeInfo.getPort());
	}

	@Test
	public void getMousePosition() throws Exception {
		Point pos = TestUtils.runSync(fin.System.getMousePosition());
		logger.debug("mouse position, x={}, y={}", pos.x, pos.y);
		assertNotNull(pos);
	}

	@Test
	public void getMachineId() throws Exception {
		String machineId = TestUtils.runSync(fin.System.getMachineId());
		assertNotNull(machineId);
		logger.debug("machineId={}", machineId);
	}

	@Test
	public void deleteCacheOnExit() throws Exception {
		TestUtils.runSync(fin.System.deleteCacheOnExit());
	}
	
	@Test
	public void openUrlWithBrowser() throws Exception {
		TestUtils.runSync(fin.System.openUrlWithBrowser("https://www.google.com").thenAccept(n->{
			logger.debug("opened url with browser");
		}));
	}
	
	@Test
	public void setGetCookie() throws Exception {
		String cookieName = "MyCookie";
		String cookieValue = UUID.randomUUID().toString();
		CookieDetails cookie = new CookieDetails("https://www.google.com/doodles");
		cookie.setTtl(1000*60*10);
		cookie.setName(cookieName);
		cookie.setValue(cookieValue);
		cookie.setHttpOnly(false);
		cookie.setPath("/");
		cookie.setSameSite(CookieDetails.SAME_SITE_LAX);
		TestUtils.runSync(fin.System.setCookie(cookie));
		List<CookieInfo> cookies = TestUtils.runSync(fin.System.getCookies("https://www.google.com", cookieName));
		assertNotNull(cookies);
		assertTrue(cookies.size() > 0);
		assertEquals(cookies.get(0).getName(), cookieName);
	}
	
	@Test
	public void flushCookieStore() throws Exception {
		TestUtils.runSync(fin.System.flushCookieStore());
	}
	
	@Test
	public void getAllApplications() throws Exception {
		TestUtils.runSync(fin.Application.start(new ApplicationOptions(UUID.randomUUID().toString())));
		AppInfo[] apps = TestUtils.runSync(fin.System.getAllApplications());
		assertNotNull(apps);
		assertTrue(apps.length > 0);
		logger.debug("app count: {}", apps.length);
		for (AppInfo info : apps) {
			logger.debug("appInfo: {}", info);
		}
	}

	@Test
	public void getAllWindows() throws Exception {
		TestUtils.runSync(fin.Application.start(new ApplicationOptions(UUID.randomUUID().toString())).thenCompose(app->{
			WindowOptions winOpts = new WindowOptions("childWindowName");
			winOpts.setUrl("https://www.google.com");
			return app.createChildWindow(winOpts);
		}));
		WinInfo[] wins = TestUtils.runSync(fin.System.getAllWindows());
		assertNotNull(wins);
		assertTrue(wins.length > 0);
		logger.debug("win count: {}", wins.length);
		for (WinInfo info : wins) {
			logger.debug("winInfo: {}", info);
		}
	}

	@Test
	public void getUniqueUserId() throws Exception {
		String uid = TestUtils.runSync(fin.System.getUniqueUserId());
		assertNotNull(uid);
		logger.debug("getUniqueUserId: {}", uid);
	}

	@Test
	public void getMonitorInfo() throws Exception {
		MonitorInfo moitorInfo = TestUtils.runSync(fin.System.getMonitorInfo());
		assertNotNull(moitorInfo);
		logger.debug("getMonitorInfo: {}", moitorInfo);
	}
	
	@Test
	public void launchExternalProcess() throws Exception {
		TestUtils.runSync(fin.System.launchExternalProcess("notepad", null));
	}

	@Test
	public void getRvmInfo() throws Exception {
		assumeTrue(Platform.isWindows());
		RvmInfo rvmInfo = TestUtils.runSync(fin.System.getRvmInfo());
		assertNotNull(rvmInfo);
	}
	
	@Test
	public void manyMessages() throws Exception {
		int msgCnt = 1000;
		CountDownLatch latch = new CountDownLatch(msgCnt);
		for (int i=0; i<msgCnt; i++) {
			int requestId = i;
			fin.System.getVersion().thenAccept(v->{
				logger.debug("requeset {}: {}", requestId, v);
				latch.countDown();
			});
		}
		latch.await(20, TimeUnit.SECONDS);
		assertEquals(0, latch.getCount());
	}
}
