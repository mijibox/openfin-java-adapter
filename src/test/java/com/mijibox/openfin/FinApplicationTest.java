package com.mijibox.openfin;

import static com.mijibox.openfin.TestUtils.runSync;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.json.JsonObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinApplicationObject;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.FinWindowObject;
import com.mijibox.openfin.bean.ApplicationInfo;
import com.mijibox.openfin.bean.ApplicationOptions;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.RuntimeConfig;
import com.mijibox.openfin.bean.ShortCutConfig;
import com.mijibox.openfin.bean.TrayIconInfo;
import com.mijibox.openfin.bean.WindowOptions;
import com.sun.jna.Platform;

public class FinApplicationTest {
	private final static Logger logger = LoggerFactory.getLogger(FinApplicationTest.class);
	private static FinRuntime fin;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime();
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.dispose(fin);
	}

	@Test
	public void startThenQuit() throws Exception {
		String appUuid = UUID.randomUUID().toString();
		WindowOptions winOpts = new WindowOptions(appUuid);
		winOpts.setUrl("https://www.google.com");
		winOpts.setAutoShow(true);
		ApplicationOptions appOpts = new ApplicationOptions(appUuid);
		appOpts.setDisableIabSecureLogging(true);
		appOpts.setMainWindowOptions(winOpts);
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.start(appOpts));
		assertNotNull(appObj);
		TestUtils.runSync(appObj.quit());
	}

	@Test
	public void startSameApp() throws Exception {
		String appUuid = UUID.randomUUID().toString();

		ApplicationOptions appOpts = new ApplicationOptions(appUuid);
		appOpts.setDisableIabSecureLogging(true);
		appOpts.setAutoShow(true);
		appOpts.setUrl("https://www.google.com");

		FinApplicationObject appObj = TestUtils.runSync(fin.Application.start(appOpts));

		assertNotNull(appObj);

		FinApplicationObject appObj2 = TestUtils.runSync(fin.Application.start(appOpts).exceptionally(e -> {
			return null;
		}));

		assertNull(appObj2);

		TestUtils.runSync(appObj.quit());
	}

	@Test
	public void startFromManifest() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		assertNotNull(appObj);
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void startPlatformFromManifest() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application
				.startFromManifest("https://openfin.github.io/platform-api-project-seed/public.json"));
		assertNotNull(appObj);
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void isRunning() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		assertNotNull(appObj);
		Boolean running = TestUtils.runSync(appObj.isRunning());
		assertTrue(running);
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void terminate() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		assertNotNull(appObj);
		try {
			TestUtils.runSync(appObj.terminate());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void getInfo() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		assertNotNull(appObj);
		ApplicationInfo appInfo = TestUtils.runSync(appObj.getInfo());
		assertNotNull(appInfo);
		assertNotNull(appInfo.getRuntime().getVersion());
		logger.info("runtime version: {}", appInfo.getRuntime().getVersion());
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Ignore
	@Test
	public void addRemoveEventListener() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		TestUtils.runSync(appObj.addEventListener(FinApplicationObject.EVENT_WINDOW_FOCUSED, eventArgs -> {
			logger.debug("AAA window focused");
		}));

		ApplicationOptions appOpts = new ApplicationOptions(UUID.randomUUID().toString());
		appOpts.setAutoShow(true);
		appOpts.setUrl("https://www.google.com");
		
		FinApplicationObject appObj2 = TestUtils.runSync(fin.Application.start(appOpts));
		TestUtils.runSync(appObj2.addEventListener(FinApplicationObject.EVENT_WINDOW_FOCUSED, eventArgs -> {
			logger.debug("BBB window focused");
		}));

		LockSupport.parkUntil(System.currentTimeMillis() + 120000);
	}

	@Test
	public void getChildWindows() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		Thread.sleep(500);
		List<FinWindowObject> childWins = TestUtils.runSync(appObj.getChildWindows());
		assertNotNull(childWins);
		assertTrue(childWins.size() > 0);
		try {
			TestUtils.runSync(appObj.quit(true));
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void getWindow() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		Thread.sleep(500);
		FinWindowObject mainWin = TestUtils.runSync(appObj.getWindow());
		assertNotNull(mainWin);
		try {
			TestUtils.runSync(mainWin.close());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void getGroups() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		Thread.sleep(500);
		List<FinWindowObject> childWins = TestUtils.runSync(appObj.getChildWindows());
		assertNotNull(childWins);
		assertTrue(childWins.size() > 1);
		if (childWins.size() > 1) {
			FinWindowObject targetWindow = childWins.get(0);
			for (int i = 1; i < childWins.size(); i++) {
				FinWindowObject childWin = childWins.get(i);
				TestUtils.runSync(childWin.joinGroup(targetWindow));
			}
		}
		List<List<FinWindowObject>> winGroups = TestUtils.runSync(appObj.getGroups());
		logger.info("winGroups: {}", winGroups);
		assertNotNull(winGroups);
		assertTrue(winGroups.size() > 0);
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}
	
	@Test
	public void getManifest() throws Exception {
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		
		JsonObject appManifest = TestUtils.runSync(appObj.getManifest());
		logger.info("getManifest: {}", appManifest);
		assertNotNull(appManifest);
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}
	
	@Test
	public void getParentUuid() throws Exception {
		ApplicationOptions appOpts = new ApplicationOptions(UUID.randomUUID().toString());
		FinApplicationObject appObj = runSync(fin.Application.start(appOpts));
		assertNotNull(appObj);
		String parentUuid = TestUtils.runSync(appObj.getParentUuid());
		logger.debug("getParentUuid: {}", parentUuid);
		assertNotNull(parentUuid);
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void registerUser() throws Exception {
		assumeTrue(Platform.isWindows());
		ApplicationOptions appOpts = new ApplicationOptions(UUID.randomUUID().toString());
		FinApplicationObject appObj = runSync(fin.Application.start(appOpts));
		assertNotNull(appObj);
		TestUtils.runSync(appObj.registerUser("MyUserName", "MyAppName"));
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void getShortCuts() throws Exception {
		assumeTrue(Platform.isWindows());
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		
		ShortCutConfig shortCutConfig = TestUtils.runSync(appObj.getShortCuts());
		assertNotNull(shortCutConfig);
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}

	@Test
	public void setGetTrayIconInfo() throws Exception {
		assumeTrue(Platform.isWindows());
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));

		TestUtils.runSync(appObj.setTrayIcon("http://cdn.openfin.co/assets/testing/icons/circled-digit-one.png"));
		
		Thread.sleep(2000);
		
		TrayIconInfo trayInfo = TestUtils.runSync(appObj.getTrayIconInfo());
		assertNotNull(trayInfo);
		logger.debug("trayInfo: {}", trayInfo);
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
	}
	
	@Test
	public void setThenGetZoomLevel() throws Exception {
		int level = 3;
		FinApplicationObject appObj = TestUtils.runSync(fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json"));
		TestUtils.runSync(appObj.setZoomLevel(level));
		Thread.sleep(1000);
		Integer zoomLevel = TestUtils.runSync(appObj.getZoomLevel());
		try {
			TestUtils.runSync(appObj.quit());
		}
		catch (Exception ex) {
			// openfin runtime bug
		}
		assertNotNull(zoomLevel);
		assertEquals(level, zoomLevel.intValue());
	}
	
}
