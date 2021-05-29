package com.mijibox.openfin;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.FinBeanUtils;

public class FinInteropTest {

	private final static Logger logger = LoggerFactory.getLogger(FinInteropTest.class);
	private static FinRuntime fin;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime("stable");
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.dispose(fin);
	}

	@Test
	public void connect() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		TestUtils.runSync(app.quit());
	}

	@Test
	public void joinContextGroup() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		TestUtils.runSync(interopClient.joinContextGroup("red"));
		TestUtils.runSync(app.quit());
	}

}
