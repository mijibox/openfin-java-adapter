package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinApplicationObject;
import com.mijibox.openfin.FinEventListener;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.RuntimeConfig;

@Ignore
public class FinSubscriptionManagerTest {
	private final static Logger logger = LoggerFactory.getLogger(FinSubscriptionManagerTest.class);

	private static String version = "stable-v16";
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
	public void addRemoveSubscription() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		CountDownLatch latch2 = new CountDownLatch(2);
		String topic = "system";
		String eventType = "application-started";
		
		FinEventListener listener = eventObj ->{
			logger.debug("application started, eventObj: {}", eventObj);
			latch.countDown();
			latch2.countDown();
		};
		fin.SubscriptionManager.addListener(topic, eventType, listener);
		
		FinApplicationObject appObj = fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json").toCompletableFuture().get(10, TimeUnit.SECONDS);
		try {
			appObj.terminate().toCompletableFuture().get(10, TimeUnit.SECONDS);
		}
		catch (Exception ex) {
			//ignore openfin bug
		}
		
		latch.await(10, TimeUnit.SECONDS);
		assertEquals(0, latch.getCount());
		
		fin.SubscriptionManager.removeListener(topic, eventType, listener).toCompletableFuture().get(10, TimeUnit.SECONDS);

		FinApplicationObject appObj2 = fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json").toCompletableFuture().get(10, TimeUnit.SECONDS);
		try {
			appObj2.terminate().toCompletableFuture().get(10, TimeUnit.SECONDS);
		}
		catch (Exception ex) {
			//ignore openfin bug
		}
		
		latch2.await(10, TimeUnit.SECONDS);
		assertEquals(1, latch2.getCount());

		fin.SubscriptionManager.addListener(topic, eventType, listener);

		FinApplicationObject appObj3 = fin.Application.startFromManifest("https://cdn.openfin.co/demos/hello/app.json").toCompletableFuture().get(10, TimeUnit.SECONDS);
		try {
			appObj3.terminate().toCompletableFuture().get(10, TimeUnit.SECONDS);
		}
		catch (Exception ex) {
			//ignore openfin bug
		}
		
		latch2.await(10, TimeUnit.SECONDS);
		assertEquals(0, latch2.getCount());
	
	}
}
