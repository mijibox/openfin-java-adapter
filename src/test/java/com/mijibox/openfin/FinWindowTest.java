package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.json.JsonValue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinApplicationObject;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.FinWindowObject;
import com.mijibox.openfin.FinWindowObject.Anchor;
import com.mijibox.openfin.bean.ApplicationInfo;
import com.mijibox.openfin.bean.ApplicationOptions;
import com.mijibox.openfin.bean.Bounds;
import com.mijibox.openfin.bean.CapturePageOptions;
import com.mijibox.openfin.bean.FindInPageOptions;
import com.mijibox.openfin.bean.FrameInfo;
import com.mijibox.openfin.bean.PrintOptions;
import com.mijibox.openfin.bean.PrinterInfo;
import com.mijibox.openfin.bean.Rectangle;
import com.mijibox.openfin.bean.WindowBounds;
import com.mijibox.openfin.bean.WindowInfo;
import com.mijibox.openfin.bean.WindowOptions;
import com.mijibox.openfin.bean.WindowUpdatableOptions;

public class FinWindowTest {
	private final static Logger logger = LoggerFactory.getLogger(FinWindowTest.class);

	private static FinRuntime fin;
	private static FinApplicationObject ofApplicationObject;

	private FinWindowObject ofWindowObject;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime();
		ApplicationOptions appOpts = new ApplicationOptions(UUID.randomUUID().toString());
		ofApplicationObject = TestUtils.runSync(fin.Application.start(appOpts));
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.runSync(ofApplicationObject.quit());
		TestUtils.runSync(fin.System.exit());
	}

	@Before
	public void beforeTest() throws Exception {
		WindowOptions winOpts = new WindowOptions(UUID.randomUUID().toString());
		winOpts.setUrl("https://www.google.com");
		winOpts.setAutoShow(true);
		ofWindowObject = TestUtils.runSync(ofApplicationObject.createChildWindow(winOpts));
	}

	@After
	public void afterTest() throws Exception {
		if (ofWindowObject != null) {
			ofWindowObject.close(true);
		}
	}

	@Test
	public void show() throws Exception {
		TestUtils.runSync(ofWindowObject.show());
	}

	@Test
	public void hide() throws Exception {
		TestUtils.runSync(ofWindowObject.hide());
	}

	@Test
	public void resizeTo() throws Exception {
		TestUtils.runSync(ofWindowObject.resizeTo(400, 300, Anchor.TOP_LEFT));
	}

	@Test
	public void navigate() throws Exception {
		TestUtils.runSync(ofWindowObject.navigate("https://www.google.com"));
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.navigate("https://www.bing.com"));
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.navigate("https://www.google.com"));
		Thread.sleep(100);
		TestUtils.runSync(ofWindowObject.stopNavigation());
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.navigateBack());
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.navigateBack());
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.navigateForward());
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.reload());
		Thread.sleep(500);
	}

	@Test
	public void focus() throws Exception {
		TestUtils.runSync(ofWindowObject.focus());
	}

	@Test
	public void flash() throws Exception {
		TestUtils.runSync(ofWindowObject.flash());
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.stopFlashing());
//		Thread.sleep(3000);
	}

	@Test
	public void center() throws Exception {
		TestUtils.runSync(ofWindowObject.center());
	}

	@Test
	public void blur() throws Exception {
		TestUtils.runSync(ofWindowObject.blur());
	}

	@Test
	public void bringToFront() throws Exception {
		TestUtils.runSync(ofWindowObject.bringToFront());
	}

	@Test
	public void showDeveloperTools() throws Exception {
		TestUtils.runSync(ofWindowObject.showDeveloperTools());
	}

	@Test
	public void getNativeId() throws Exception {
		String nativeId = TestUtils.runSync(ofWindowObject.getNativeId());
		logger.debug("getNativeId: {}", nativeId);
		assertNotNull(nativeId);
	}

	@Test
	public void getOptions() throws Exception {
		WindowOptions winOpts = TestUtils.runSync(ofWindowObject.getOptions());
		logger.debug("getOptions: {}", winOpts);
		logger.debug("getOptions.fromJson: {}", winOpts.getFromJson());
		assertNotNull(winOpts);
	}
	
	@Test
	public void executeJavacript() throws Exception {
		JsonValue result = TestUtils.runSync(ofWindowObject.executeJavaScript("fin.me.identity"));
		logger.debug("executeJavacript result: {}", result);
	}
	
	@Test
	public void forceClose() throws Exception {
		
		ofWindowObject.addEventListener(FinWindowObject.EVENT_CLOSE_REQUESTED, e->{
			logger.debug("window close requested, event: {}", e);
		});

		ofWindowObject.addEventListener(FinWindowObject.EVENT_CLOSED, e->{
			logger.debug("window closed, event: {}", e);
		});
		
		try {
			ofWindowObject.close().toCompletableFuture().get(5, TimeUnit.SECONDS);
			fail("should get timeout closing the window");
		}
		catch (Exception ex) {
			//should have exception
		}
		
		TestUtils.runSync(ofWindowObject.close(true));
		ofWindowObject = null;
	}
	
	@Test
	public void setGetZoomLevel() throws Exception {
		int zoomLevel = 3;
		TestUtils.runSync(ofWindowObject.setZoomLevel(zoomLevel)); //openfin bug with 19.89.59.24 
		Thread.sleep(500);
		Integer gotZoomLevel = TestUtils.runSync(ofWindowObject.getZoomLevel());
		assertEquals(zoomLevel, gotZoomLevel.intValue());
	}

	@Test
	public void findInPage() throws Exception {
		TestUtils.runSync(ofWindowObject.findInPage("search", null));
		Thread.sleep(500);
		FindInPageOptions options = new FindInPageOptions();
		options.setMatchCase(true);
		options.setForward(false);
		TestUtils.runSync(ofWindowObject.findInPage("Gmail", options));
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.stopFindInPage(FinWindowObject.FindInPageAction.ACTIVATE_SELECTION));
		Thread.sleep(500);
	}
	
	@Test
	public void getPrinters() throws Exception {
		PrinterInfo[] printers = TestUtils.runSync(ofWindowObject.getPrinters());
		assertNotNull(printers);
		for (PrinterInfo pInfo : printers) {
			logger.debug("printer name: {}, isDefault: {}", pInfo.getName(), pInfo.getIsDefault());
		}
	}

	@Ignore
	@Test
	public void print() throws Exception {
//		OfUtils.runSync(ofWindowObject.print(null));
		
		Thread.sleep(500);
		
		PrintOptions opts = new PrintOptions();
		opts.setCopies(2);
//		opts.setSilent(true);
		TestUtils.runSync(ofWindowObject.print(opts));
		LockSupport.park();
		
	}
	
	@Test
	public void capturePage() throws Exception {
		String encodedImage = TestUtils.runSync(ofWindowObject.capturePage(null));
		assertNotNull(encodedImage);
		logger.debug("encodedImage.length() = {}", encodedImage.length());
		CapturePageOptions options = new CapturePageOptions();
		options.setArea(new Rectangle(50, 50, 640, 480));
		options.setFormat(CapturePageOptions.IMAGE_FORMAT_PNG);
		encodedImage = TestUtils.runSync(ofWindowObject.capturePage(options));
		assertNotNull(encodedImage);
		logger.debug("encodedImage.length() = {}", encodedImage.length());
		
	}
	
	@Test
	public void disableEnableUserMovement() throws Exception {
		TestUtils.runSync(ofWindowObject.disableUserMovement().thenCompose(v->{
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			return ofWindowObject.enableUserMovement().thenAccept(v2->{
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}));
	}
	
	@Test
	public void getAllFrames() throws Exception {
		FrameInfo[] frames = TestUtils.runSync(ofWindowObject.getAllFrames());
		assertNotNull(frames);
		for (FrameInfo info : frames) {
			logger.debug("FrameInfo: {}", info);
		}
	}

	@Test
	public void getBounds() throws Exception {
		WindowBounds bounds = TestUtils.runSync(ofWindowObject.getBounds());
		assertNotNull(bounds);
		logger.debug("bounds: {}", bounds);
	}

	@Test
	public void getGroup() throws Exception {
		FinWindowObject[] winObjs = TestUtils.runSync(ofWindowObject.getGroup());
		assertNotNull(winObjs);
		for (FinWindowObject winObj : winObjs) {
			logger.debug("winObj: {}", winObj.getIdentity());
		}
	}

	@Test
	public void getInfo() throws Exception {
		WindowInfo winInfo = TestUtils.runSync(ofWindowObject.getInfo());
		assertNotNull(winInfo);
		logger.debug("winInfo: {}", winInfo);
	}

	@Test
	public void getParentApplcation() throws Exception {
		FinApplicationObject parentApp = TestUtils.runSync(ofWindowObject.getParentApplication());
		assertNotNull(parentApp);
		ApplicationInfo appInfo = TestUtils.runSync(parentApp.getInfo());
		assertNotNull(appInfo);
		logger.debug("appInfo: {}", appInfo);
	}

	@Test
	public void getState() throws Exception {
		String winState = TestUtils.runSync(ofWindowObject.getState());
		assertNotNull(winState);
		logger.debug("winState: {}", winState);
	}

	@Test
	public void isMainWindow() throws Exception {
		assertFalse(ofWindowObject.isMainWindow());
	}

	@Test
	public void isShowing() throws Exception {
		TestUtils.runSync(ofWindowObject.isShowing());
	}

	@Test
	public void groupTest() throws Exception {
		//create another window
		WindowOptions winOpts = new WindowOptions(UUID.randomUUID().toString());
		winOpts.setUrl("https://www.bing.com");
		winOpts.setAutoShow(true);
		FinWindowObject newWindow = TestUtils.runSync(ofApplicationObject.createChildWindow(winOpts));
		assertNotNull(newWindow);
		TestUtils.runSync(ofWindowObject.joinGroup(newWindow).thenCompose(v->{
			return ofWindowObject.getGroup().thenAccept(groupWins->{
				assertEquals(2, groupWins.length);
			}).thenCompose(v2->{
				return ofWindowObject.leaveGroup();
			}).thenCompose(v3->{
				return ofWindowObject.getGroup();
			}).thenAccept(groupWins2->{
				assertEquals(0, groupWins2.length);
			});
		}));
	}
	
	@Test
	public void windowStates() throws Exception {
		TestUtils.runSync(ofWindowObject.maximize());
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.restore());
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.minimize());
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.restore());
		Thread.sleep(500);
	}

	@Test
	public void resize() throws Exception {
		TestUtils.runSync(ofWindowObject.resizeTo(500, 500));
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.resizeTo(200, 200, Anchor.BOTTOM_LEFT));
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.resizeBy(200, 200));
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.resizeBy(-100, -100, Anchor.BOTTOM_RIGHT));
		Thread.sleep(500);
	}

	@Test
	public void move() throws Exception {
		TestUtils.runSync(ofWindowObject.moveTo(500, 500));
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.moveBy(-200, -200));
		Thread.sleep(500);
	}

	@Test
	public void setAsForeground() throws Exception {
		//create another window
		WindowOptions winOpts = new WindowOptions(UUID.randomUUID().toString());
		winOpts.setUrl("https://www.bing.com");
		winOpts.setAutoShow(true);
		FinWindowObject newWindow = TestUtils.runSync(ofApplicationObject.createChildWindow(winOpts));
		assertNotNull(newWindow);

		TestUtils.runSync(ofWindowObject.setAsForeground());
		Thread.sleep(500);
		TestUtils.runSync(newWindow.setAsForeground());
		Thread.sleep(500);
	}

	@Test
	public void setBounds() throws Exception {
		Bounds b = new Bounds(0, 0, 300, 300);
		TestUtils.runSync(ofWindowObject.setBounds(b));
		Thread.sleep(500);
		b = new Bounds();
		b.setWidth(1500);
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.setBounds(b));
		Thread.sleep(500);
		b = new Bounds();
		b.setTop(500);
		TestUtils.runSync(ofWindowObject.setBounds(b));
		Thread.sleep(500);
	}
	
	@Test
	public void showAt() throws Exception {
		TestUtils.runSync(ofWindowObject.showAt(500, 300, false));
		Thread.sleep(500);
		TestUtils.runSync(ofWindowObject.showAt(0, 0, true));
		Thread.sleep(500);
	}
	
	@Test
	public void updateOptions() throws Exception {
		WindowUpdatableOptions opts = new WindowUpdatableOptions();
		opts.setAlwaysOnTop(true);
		TestUtils.runSync(ofWindowObject.updateOptions(opts));
	}
}
