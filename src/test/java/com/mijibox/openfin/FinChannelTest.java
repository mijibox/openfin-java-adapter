package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinChannelClient;
import com.mijibox.openfin.FinChannelProvider;
import com.mijibox.openfin.FinEvent;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.bean.ApplicationOptions;
import com.mijibox.openfin.bean.ChannelConnectionOptions;
import com.mijibox.openfin.bean.ClientIdentity;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.RuntimeConfig;

public class FinChannelTest {
	private final static Logger logger = LoggerFactory.getLogger(FinChannelTest.class);
	private static FinRuntime fin;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime("stable-v15");
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.runSync(fin.System.exit());
	}

	@Test
	public void create() throws Exception {
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
	}

	@Test
	public void connectAfterProviderDestroy() throws Exception {
		String channelName = UUID.randomUUID().toString();
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		TestUtils.runSync(provider.destroy());
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName, new ChannelConnectionOptions(false, null)).exceptionally(e -> {
			return null;
		}));
		assertNull(client);
	}

	@Test
	public void connectThenDestroyProvider() throws Exception {
		String channelName = UUID.randomUUID().toString();
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName).exceptionally(e -> {
			return null;
		}));
		assertNotNull(client);
		TestUtils.runSync(provider.destroy());
	}

	@Test
	public void createDuplicateChannelProvider() throws Exception {
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		FinChannelProvider provider2 = TestUtils.runSync(fin.Channel.create(channelName).exceptionally(e -> {
			// e.printStackTrace();
			return null;
		}));
		assertNull(provider2);
	}

	@Test
	public void connect() throws Exception {
		String channelName = UUID.randomUUID().toString();
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("provider.providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		logger.info("routingInfo: {}", FinBeanUtils.toJsonString(client.getRoutingInfo()));
		logger.info("client.providerIdentity: {}", FinBeanUtils.toJsonString(client.getProviderIdentity()));
		assertNotNull(client.getRoutingInfo());
		assertEquals(provider.getProviderIdentity(), client.getProviderIdentity());
		TestUtils.runSync(client.disconnect());
		Thread.sleep(500);
	}

	@Test
	public void connectWithOptions() throws Exception {
		String channelName = UUID.randomUUID().toString();
		ChannelConnectionOptions connectOpts = new ChannelConnectionOptions(true, Json.createValue(System.currentTimeMillis()));
		CompletableFuture<FinChannelClient> clientFuture = new CompletableFuture<>();
		//connect before provider is created, it should connect when provider is ready.
		fin.Channel.connect(channelName, connectOpts).thenAccept(client->{
			clientFuture.complete(client);
		});
		
		Thread.sleep(3000);
		
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		
		FinChannelClient channelClient = TestUtils.runSync(clientFuture);
		assertNotNull(channelClient);
		Thread.sleep(500);
		TestUtils.runSync(provider.destroy());
	}

	@Test
	public void multipleConnection() throws Exception {
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		logger.info("routingInfo: {}", FinBeanUtils.toJsonString(client.getRoutingInfo()));
		assertNotNull(client.getRoutingInfo());
		FinChannelClient client2 = TestUtils.runSync(fin.Channel.connect(channelName));
		logger.info("routingInfo: {}", FinBeanUtils.toJsonString(client2.getRoutingInfo()));
		assertNotNull(client2.getRoutingInfo());
	}

	@Test
	public void disconnect() throws Exception {
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		logger.info("routingInfo: {}", FinBeanUtils.toJsonString(client.getRoutingInfo()));
		assertNotNull(client.getRoutingInfo());
		TestUtils.runSync(client.disconnect());
	}

	@Test
	public void providerDispatchAfterClientDisconnect() throws Exception {
		String action = "AAA";
		String actionResult = "BBB";
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		assertNotNull(provider.getProviderIdentity());
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		client.register(action, (payload, senderIdentity) -> {
			return Json.createValue(actionResult);
		});
		assertNotNull(client.getRoutingInfo());
		TestUtils.runSync(client.disconnect());
		try {
			TestUtils.runSync(provider.dispatch(client.getClientIdentity(), action, Json.createValue("1qazxsw23edc")));
			fail("should have exception but didn't get one.");
		}
		catch (Exception ex) {
			// great
			// ex.printStackTrace();
		}
	}

	@Test
	public void clientRegisterProviderPublish() throws Exception {
		String action = "AAA";
		String actionResult = "BBB";
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		logger.info("routingInfo: {}", FinBeanUtils.toJsonString(client.getRoutingInfo()));
		assertNotNull(client.getRoutingInfo());
		client.register(action, (payload, senderIdentity) -> {
			return Json.createValue(actionResult);
		});

		List<CompletionStage<JsonValue>> futures = provider.publish(action, Json.createValue("1qazxsw23edc"));
		CountDownLatch latch = new CountDownLatch(1);
		futures.forEach(f -> {
			JsonValue result = TestUtils.runSync(f.toCompletableFuture().exceptionally(e -> {
				e.printStackTrace();
				return null;
			}));
			if (result instanceof JsonString) {
				if (actionResult.equals(((JsonString) result).getString())) {
					latch.countDown();
				}
			}
		});

		latch.await(10, TimeUnit.SECONDS);
		assertEquals(0, latch.getCount());
	}

	@Test
	public void clientRegisterProviderDispatch() throws Exception {
		String action = "AAA";
		String actionResult = "BBB";
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		logger.info("routingInfo: {}", FinBeanUtils.toJsonString(client.getRoutingInfo()));
		assertNotNull(client.getRoutingInfo());
		client.register(action, (payload, senderIdentity) -> {
			return Json.createValue(actionResult);
		});

		JsonValue result = TestUtils
				.runSync(provider.dispatch(client.getClientIdentity(), action, Json.createValue("1qazxsw23edc")));
		assertTrue(result instanceof JsonString);
		assertEquals(actionResult, ((JsonString) result).getString());
	}

	@Test
	public void clientRegisterProviderDispatchNoPayload() throws Exception {
		String action = "AAA";
		String actionResult = "BBB";
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		Thread.sleep(500);
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		logger.info("routingInfo: {}", FinBeanUtils.toJsonString(client.getRoutingInfo()));
		assertNotNull(client.getRoutingInfo());
		client.register(action, (payload, senderIdentity) -> {
			logger.debug("got payload: {}", payload);
			return Json.createValue(actionResult);
		});

		JsonValue result = TestUtils.runSync(provider.dispatch(client.getClientIdentity(), action));
		assertTrue(result instanceof JsonString);
		assertEquals(actionResult, ((JsonString) result).getString());
	}

	@Test
	public void providerRegisterClientDispatch() throws Exception {
		String action = "AAA";
		String actionResult = "BBB";
		String channelName = UUID.randomUUID().toString();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		logger.info("providerIdentity: {}", FinBeanUtils.toJsonString(provider.getProviderIdentity()));
		assertNotNull(provider.getProviderIdentity());
		provider.register(action, (payload, senderIdentity) -> {
			return Json.createValue(actionResult);
		});

		Thread.sleep(500);

		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		logger.info("routingInfo: {}", FinBeanUtils.toJsonString(client.getRoutingInfo()));
		assertNotNull(client.getRoutingInfo());

		JsonValue result = TestUtils.runSync(client.dispatch(action, Json.createValue("1qazxsw23edc")));
		assertTrue(result instanceof JsonString);
		assertEquals(actionResult, ((JsonString) result).getString());
	}

	@Test
	public void providerDefaultAction() throws Exception {
		String channelName = UUID.randomUUID().toString();
		String result = "providerDefaultActionResult";
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		provider.setDefaultAction((action, payload, identity)->{
			return Json.createValue(result);
		});
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		JsonValue defaultActionResult = TestUtils.runSync(client.dispatch("whatever_doesnt_matter"));
		assertNotNull(defaultActionResult);
		assertEquals(result, ((JsonString)defaultActionResult).getString());
	}

	@Test
	public void providerBeforeAction() throws Exception {
		String channelName = UUID.randomUUID().toString();
		String beforeActionResultPrefix = "BEFORE_";
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		provider.setBeforeAction((action, payload, identity)->{
			return Json.createValue(beforeActionResultPrefix + ((JsonString)payload).getString());
		});
		provider.setDefaultAction((action, payload, identity)->{
			//do nothing
			return payload;
		});
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		JsonValue actionResult = TestUtils.runSync(client.dispatch("whatever_doesnt_matter", Json.createValue("whatever_doesnt_matter_too")));
		assertNotNull(actionResult);
		assertTrue(((JsonString)actionResult).getString().startsWith(beforeActionResultPrefix));
	}

	@Test
	public void providerAfterAction() throws Exception {
		String channelName = UUID.randomUUID().toString();
		String afterActionResultPrefix = "AFTER_";
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		provider.setAfterAction((action, payload, identity)->{
			return Json.createValue(afterActionResultPrefix + ((JsonString)payload).getString());
		});
		provider.setDefaultAction((action, payload, identity)->{
			//do nothing
			return payload;
		});
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		JsonValue actionResult = TestUtils.runSync(client.dispatch("whatever_doesnt_matter", Json.createValue("whatever_doesnt_matter_too")));
		assertNotNull(actionResult);
		assertTrue(((JsonString)actionResult).getString().startsWith(afterActionResultPrefix));
	}
	
	@Test
	public void providerActionError() throws Exception {
		String channelName = UUID.randomUUID().toString();
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		provider.setDefaultAction((action, payload, identity)->{
			//payload comes in as long, intentionally convert it to string to cause error
			return Json.createValue("GGYY: " + ((JsonString)payload).getString());
		});
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		JsonValue defaultActionResult = TestUtils.runSync(client.dispatch("whatever_doesnt_matter", Json.createValue(System.currentTimeMillis())).exceptionally(e->{
			e.printStackTrace();
			return null;
		}));
		assertNull(defaultActionResult);
	}

	@Test
	public void providerActionErrorOnError() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		String channelName = UUID.randomUUID().toString();
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		provider.setDefaultAction((action, payload, identity)->{
			//payload comes in as long, intentionally convert it to string to cause error
			return Json.createValue("GGYY: " + ((JsonString)payload).getString());
		});
		
		provider.setOnError((action, payload, identity)->{
			latch.countDown();
			return null;
		});
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		TestUtils.runSync(client.dispatch("whatever_doesnt_matter", Json.createValue(System.currentTimeMillis())).exceptionally(e->{
			return null;
		}));
		latch.await(5, TimeUnit.SECONDS);
		assertEquals(0, latch.getCount());
	}
	
	@Test
	public void channelClientProviderDestroyDisconnectionListener() throws Exception {
		String channelName = UUID.randomUUID().toString();
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		
		CompletableFuture<FinEvent> disconnectedFuture = new CompletableFuture<>();
		
		TestUtils.runSync(fin.Channel.connect(channelName).thenAccept(channelClient -> {
			channelClient.addChannelDisconnectListener(e -> {
				logger.debug("channel client disconnected: {}", e);
				disconnectedFuture.complete(e);
			});
		}));
		
		TestUtils.runSync(provider.destroy());
		
		TestUtils.runSync(disconnectedFuture);
	}

	@Test
	public void channelClientDisconnectDisconnectionListener() throws Exception {
		String channelName = UUID.randomUUID().toString();
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		
		CompletableFuture<FinEvent> disconnectedFuture = new CompletableFuture<>();
		
		FinChannelClient channelClient = TestUtils.runSync(fin.Channel.connect(channelName));

		channelClient.addChannelDisconnectListener(e -> {
			logger.debug("channel client disconnected: {}", e);
			disconnectedFuture.complete(e);
		});
		
		TestUtils.runSync(channelClient.disconnect());
		
		TestUtils.runSync(disconnectedFuture);
	}

	@Test
	public void channelProviderConnectedDisconnectedListeners() throws Exception {
		String channelName = UUID.randomUUID().toString();
		
		CompletableFuture<FinEvent> connectedFuture = new CompletableFuture<>();
		CompletableFuture<FinEvent> disconnectedFuture = new CompletableFuture<>();

		fin.Channel.addChannelConnectListener(e->{
			connectedFuture.complete(e);
		});
		
		fin.Channel.addChannelDisconnectListener(e->{
			disconnectedFuture.complete(e);
		});
		
		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));
		
		TestUtils.runSync(connectedFuture);
		
		provider.destroy();

		TestUtils.runSync(disconnectedFuture);
	}
	
	@Test
	public void channelProviderClientConnectDisconnectListeners() throws Exception {
		String channelName = UUID.randomUUID().toString();
		
		CompletableFuture<FinEvent> connectedFuture = new CompletableFuture<>();
		CompletableFuture<FinEvent> disconnectedFuture = new CompletableFuture<>();

		FinChannelProvider provider = TestUtils.runSync(fin.Channel.create(channelName));

		provider.addClientConnectListener(e->{
			connectedFuture.complete(e);
		});
		
		provider.addClientDisconnectListener(e->{
			disconnectedFuture.complete(e);
		});
		
		FinChannelClient client = TestUtils.runSync(fin.Channel.connect(channelName));
		
		TestUtils.runSync(connectedFuture);
		
		client.disconnect();

		TestUtils.runSync(disconnectedFuture);
	}
	
	@Test
	public void jsClientJavaProvider() {
		String channelName = "openfin-test-channel";
		String jsClientUrl = TestUtils.getTestHtmlUrl("channel_client");
		ApplicationOptions appOpts = new ApplicationOptions(UUID.randomUUID().toString());
		appOpts.setUrl(jsClientUrl);
		appOpts.setAutoShow(true);
		CompletableFuture<?> clientConnectedFuture = new CompletableFuture<>();
		//start js channel client first then start java provider
		fin.Application.start(appOpts).thenAccept(appObj->{
			fin.Channel.create(channelName).thenAccept(provider->{
				provider.addClientConnectListener(e->{
					clientConnectedFuture.complete(null);
				});
				
				clientConnectedFuture.thenAccept(v->{
					provider.publish("openfin-action", Json.createValue("LaLaLa"));
				});
			});
		});
		TestUtils.runSync(clientConnectedFuture);
	}
}
