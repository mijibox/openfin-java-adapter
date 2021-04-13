package com.mijibox.openfin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.locks.LockSupport;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.FinGlobalHotkey.HotkeyListener;

public class FinGlobalHotkeyTest {
	
	private final static Logger logger = LoggerFactory.getLogger(FinGlobalHotkeyTest.class);

	
	private static FinRuntime fin;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime();
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.runSync(fin.System.exit());
	}

	@Test
	public void registerAndUnregister() throws Exception {
		String hotkey = "CommandOrControl+;";
		HotkeyListener listener = key ->{
			logger.debug("hotkey \"{}\" pressed", key);
		};
		TestUtils.runSync(fin.GlobalHotkey.register(hotkey, listener));
		TestUtils.runSync(fin.GlobalHotkey.register(hotkey, listener));
		TestUtils.runSync(fin.GlobalHotkey.unregister(hotkey, listener));
		TestUtils.runSync(fin.GlobalHotkey.unregister(hotkey, listener));
	}
	
	@Test
	public void isRegistered() throws Exception {
		String hotkey = "CommandOrControl+]";
		HotkeyListener listener = key ->{
			logger.debug("hotkey \"{}\" pressed", key);
		};
		TestUtils.runSync(fin.GlobalHotkey.register(hotkey, listener));
		Boolean registered = TestUtils.runSync(fin.GlobalHotkey.isRegistered(hotkey));
		assertNotNull(registered);
		assertTrue(registered);
		TestUtils.runSync(fin.GlobalHotkey.unregister(hotkey, listener));
		registered = TestUtils.runSync(fin.GlobalHotkey.isRegistered(hotkey));
		assertNotNull(registered);
		assertFalse(registered);
	}
}
