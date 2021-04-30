package com.mijibox.openfin;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinIabMessageListener;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.RuntimeConfig;

public class FinInterApplicationBusTest {
	private final static Logger logger = LoggerFactory.getLogger(FinInterApplicationBusTest.class);
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
	public void subscribeAndPublish() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);

		String topic = "AABBCCDD";
		String message = "1qaz\"2wsx";

		fin.InterApplicationBus.subscribe(new Identity(), topic, (identity, jsonValue) -> {
			if (JsonValue.ValueType.STRING.equals(jsonValue.getValueType())) {
				JsonString jsonString = (JsonString) jsonValue;
				logger.info("topic: {}, jsonValue: {}, string: {}", topic, jsonValue, jsonString.getString());
				if (message.equals(jsonString.getString())) {
					latch.countDown();
				}
			}
		}).thenAccept(v -> {
			fin.InterApplicationBus.publish(topic, Json.createValue(message));
		});

		latch.await(60, TimeUnit.SECONDS);

		assertEquals(0, latch.getCount());
	}

	@Test
	public void subscribeAndSend() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);

		String topic = "AABBCCDD";
		String message = "1qaz\"2wsx";


		fin.InterApplicationBus.subscribe(new Identity(), topic, (identity, jsonValue) -> {
			if (JsonValue.ValueType.STRING.equals(jsonValue.getValueType())) {
				JsonString jsonString = (JsonString) jsonValue;
				logger.info("topic: {}, jsonValue: {}, string: {}", topic, jsonValue, jsonString.getString());
				if (message.equals(jsonString.getString())) {
					latch.countDown();
				}
			}
		}).thenAccept(v -> {
			fin.InterApplicationBus.send(new Identity(fin.getConnection().getUuid()), topic, Json.createValue(message));
		});

		latch.await(60, TimeUnit.SECONDS);

		assertEquals(0, latch.getCount());
	}

	@Test
	public void subscribeAndUnsubscribe() throws Exception {
		CountDownLatch latch1 = new CountDownLatch(2);
		CountDownLatch latch2 = new CountDownLatch(2);

		String topic = "AABBCCDD";
		String message = "1qaz\"2wsx";

		FinIabMessageListener listener1 = (identity, jsonValue) -> {
			if (JsonValue.ValueType.STRING.equals(jsonValue.getValueType())) {
				JsonString jsonString = (JsonString) jsonValue;
				logger.info("topic: {}, jsonValue: {}, string: {}", topic, jsonValue, jsonString.getString());
				if (message.equals(jsonString.getString())) {
					latch1.countDown();
				}
			}
		};
		FinIabMessageListener listener2 = (identity, jsonValue) -> {
			if (JsonValue.ValueType.STRING.equals(jsonValue.getValueType())) {
				JsonString jsonString = (JsonString) jsonValue;
				logger.info("topic: {}, jsonValue: {}, string: {}", topic, jsonValue, jsonString.getString());
				if (message.equals(jsonString.getString())) {
					latch2.countDown();
				}
			}
		};

		// subscribe twice
		fin.InterApplicationBus.subscribe(new Identity(), topic, listener1).thenAccept(v -> {
			fin.InterApplicationBus.subscribe(new Identity(), topic, listener2).thenCompose(v2 -> {
				return fin.InterApplicationBus.publish(topic, Json.createValue(message));
			}).thenAccept(v3 -> {
				//wait a bit to process the published message.
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				fin.InterApplicationBus.unsubscribe(new Identity(), topic, listener1).thenAccept(v4 -> {
					fin.InterApplicationBus.publish(topic, Json.createValue(message));
				});
			});
		});

		latch2.await(60, TimeUnit.SECONDS);

		assertEquals(1, latch1.getCount());
		assertEquals(0, latch2.getCount());
	}
	
	
	@Test
	public void iabAndRuntimeMesh() throws Exception {
		String topic = "MyTopic";
		
		FinRuntime fin2 = TestUtils.getOpenFinRuntime("stable-v16");
		
		CountDownLatch latch = new CountDownLatch(1);
		
		fin.InterApplicationBus.subscribe(null, topic, (identity, jsonValue)->{
			latch.countDown();
		}).thenAccept(v->{
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			fin2.InterApplicationBus.publish(topic, Json.createValue(12345));
		});
		
		latch.await(10, TimeUnit.SECONDS);
		
		assertEquals(0, latch.getCount());
		TestUtils.dispose(fin2);
	}
	
}
