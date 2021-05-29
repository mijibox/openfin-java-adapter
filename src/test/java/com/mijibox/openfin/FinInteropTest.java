package com.mijibox.openfin;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.ClientIdentity;
import com.mijibox.openfin.bean.Context;
import com.mijibox.openfin.bean.ContextGroupInfo;
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
	public void getContextGroups() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		ContextGroupInfo[] contextGroups = TestUtils.runSync(interopClient.getContextGroups());
		assertNotNull(contextGroups);
		logger.debug("contextGroups.length: {}", contextGroups.length);
		logger.debug("contextGroups[0].displayMetadata.color: {}", contextGroups[0].getDisplayMetadata().getColor());
		TestUtils.runSync(app.quit());
	}

	@Test
	public void getInfoForContextGroup() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		ContextGroupInfo contextGroupInfo = TestUtils.runSync(interopClient.getInfoForContextGroup("red"));
		assertNotNull(contextGroupInfo);
		logger.debug("contextGroup.displayMetadata.color: {}", contextGroupInfo.getDisplayMetadata().getColor());
		TestUtils.runSync(app.quit());
	}

	@Test
	public void getAllClientsInContextGroup() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		TestUtils.runSync(interopClient.joinContextGroup("red"));
		ClientIdentity[] clients = TestUtils.runSync(interopClient.getAllClientsInContextGroup("red"));
		assertNotNull(clients);
		logger.debug("clients.length: {}", clients.length);
		logger.debug("clients[0].getEndpointId(): {}", clients[0].getEndpointId());
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

	@Test
	public void setContext() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		TestUtils.runSync(interopClient.joinContextGroup("red"));
		Context context = new Context();
		context.setId("MyId");
		context.setName("MyName");
		context.setType("MyType");
		TestUtils.runSync(interopClient.setContext(context));
		TestUtils.runSync(app.quit());
	}
}
