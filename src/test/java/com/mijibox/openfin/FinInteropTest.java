package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.ClientIdentity;
import com.mijibox.openfin.bean.Context;
import com.mijibox.openfin.bean.ContextGroupInfo;

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
	public void joinContextGroupWithTarget() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		FinInteropClient interopClient2 = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		TestUtils.runSync(interopClient.joinContextGroup("red", interopClient2.getClientIdentity()));
		TestUtils.runSync(app.quit());
	}
	
	@Test
	public void removeFromContextGroup() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		ClientIdentity[] clients1 = TestUtils.runSync(interopClient.getAllClientsInContextGroup("red"));
		int cnt1 = clients1.length;
		TestUtils.runSync(interopClient.joinContextGroup("red"));
		ClientIdentity[] clients2 = TestUtils.runSync(interopClient.getAllClientsInContextGroup("red"));
		int cnt2 = clients2.length;
		assertEquals(cnt1 + 1, cnt2);
		TestUtils.runSync(interopClient.removeFromContextGroup());
		ClientIdentity[] clients3 = TestUtils.runSync(interopClient.getAllClientsInContextGroup("red"));
		int cnt3 = clients3.length;
		assertEquals(cnt1, cnt3);
		TestUtils.runSync(app.quit());
	}

	@Test
	public void removeFromContextGroupWithTarget() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		FinInteropClient interopClient2 = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		TestUtils.runSync(interopClient.joinContextGroup("red", interopClient2.getClientIdentity()));
		TestUtils.runSync(interopClient.removeFromContextGroup(interopClient2.getClientIdentity()));
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
	
	@Test
	public void addContextListener() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		TestUtils.runSync(interopClient.joinContextGroup("red"));
		Context context = new Context();
		String id = "MyId";
		String name = "MyName";
		String type = "MyType";
		context.setId(id);
		context.setName(name);
		context.setType(type);
		CompletableFuture<?> listenerFuture = new CompletableFuture<>();
		TestUtils.runSync(interopClient.addContextListener(ctx->{
			if (id.equals(ctx.getId())
					&& name.equals(ctx.getName())
					&& type.equals(ctx.getType())) {
				listenerFuture.complete(null);
			}
		}));
		TestUtils.runSync(interopClient.setContext(context));
		TestUtils.runSync(listenerFuture, 5);
		TestUtils.runSync(app.quit());
	}

	@Test
	public void removeContextListener() throws Exception {
		String brokerName = "InteropTest";
		FinApplicationObject app = TestUtils.runSync(fin.Application.startFromManifest(TestUtils.getTestManifestUrl("interop")));
		FinInteropClient interopClient = TestUtils.runSync(fin.Interop.connect(brokerName));
		assertNotNull(interopClient);
		TestUtils.runSync(interopClient.joinContextGroup("red"));
		Context context = new Context();
		String id = "MyId";
		String name = "MyName";
		String type = "MyType";
		context.setId(id);
		context.setName(name);
		context.setType(type);
		AtomicInteger counter = new AtomicInteger(0);
		FinContextListener listener = ctx->{
			if (id.equals(ctx.getId())
					&& name.equals(ctx.getName())
					&& type.equals(ctx.getType())) {
				counter.incrementAndGet();
			}
		};
		TestUtils.runSync(interopClient.addContextListener(listener));
		TestUtils.runSync(interopClient.setContext(context));
		TestUtils.runSync(interopClient.removeContextListener(listener));
		TestUtils.runSync(interopClient.setContext(context));
		Thread.sleep(1000);
		assertEquals(1, counter.get());
		TestUtils.runSync(app.quit());
	}
}
