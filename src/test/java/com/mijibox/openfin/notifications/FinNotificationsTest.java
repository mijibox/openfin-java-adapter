package com.mijibox.openfin.notifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.json.Json;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.TestUtils;
import com.mijibox.openfin.notifications.ButtonOptions;
import com.mijibox.openfin.notifications.FinNotifications;
import com.mijibox.openfin.notifications.NotificationActionEvent;
import com.mijibox.openfin.notifications.NotificationEvent;
import com.mijibox.openfin.notifications.NotificationEventListener;
import com.mijibox.openfin.notifications.NotificationIndicator;
import com.mijibox.openfin.notifications.NotificationOptions;
import com.mijibox.openfin.notifications.ProviderStatus;

public class FinNotificationsTest {
	private final static Logger logger = LoggerFactory.getLogger(FinNotificationsTest.class);
	private static FinRuntime fin;
	private static FinNotifications notifications;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		fin = TestUtils.getOpenFinRuntime();
		notifications = new FinNotifications(fin);
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		TestUtils.runSync(fin.System.exit());
	}
	
	@Test
	public void create() throws Exception {
		String title = "MyTitle";
		String body = "MyBody";
		String category = "MyCategory";
		String customDataName = "this";
		String customDataValue = "that";
		Date expires = new Date(System.currentTimeMillis() + (60 * 1000));
		NotificationOptions opts = new NotificationOptions(title, body, category);
		opts.setCustomData(Json.createObjectBuilder().add(customDataName, customDataValue).build());
		opts.setExpires(expires);
		NotificationIndicator indicator = new NotificationIndicator(NotificationIndicator.TYPE_WARNING);
		indicator.setText("MyWarninigText");
		opts.setIndicator(indicator);
		
		NotificationOptions returnedOpts = TestUtils.runSync(notifications.create(opts));
		assertNotNull(returnedOpts);
		assertNotNull(returnedOpts.getDate());
		assertEquals(title, returnedOpts.getTitle());
		assertEquals(body, returnedOpts.getBody());
		assertEquals(category, returnedOpts.getCategory());
		assertEquals(expires, returnedOpts.getExpires());
		assertEquals(customDataValue, returnedOpts.getCustomData().getString(customDataName));
	}
	
	@Test
	public void getProviderStatus() throws Exception {
		ProviderStatus providerStatus = TestUtils.runSync(notifications.getProviderStatus());
		assertNotNull(providerStatus);
		assertTrue(providerStatus.isConnected());
	}
	
	@Test
	public void clear() throws Exception {
		Boolean b = TestUtils.runSync(notifications.create(new NotificationOptions("AAA", "Body", "Category")).thenCompose(nn -> {
			return notifications.clear(nn.getId());
		}));

		assertTrue(b);
	}

	@Test
	public void clearAll() throws Exception {
		final int cnt = 5;
		CompletionStage<NotificationOptions> opt = null;
		for (int i = 0; i < cnt; i++) {
			String title = "Title-" + i;
			if (opt != null) {
				opt = opt.thenCompose(v -> {
					return notifications.create(new NotificationOptions(title, "Body", "Category"));
				});
			}
			else {
				opt = notifications.create(new NotificationOptions(title, "Body", "Category"));
			}

		}

		Integer cleared = TestUtils.runSync(opt.thenCompose(v -> {
			return notifications.clearAll();
		}));
		
		assertTrue(cleared >= cnt);
	}

	@Test
	public void getAll() throws Exception {
		CompletionStage<Integer> clearAllFuture = notifications.clearAll();

		final int cnt = 5;
		CompletionStage<NotificationOptions> opt = null;
		for (int i = 0; i < cnt; i++) {
			String title = "Title-" + i;
			if (opt != null) {
				opt = opt.thenCompose(v -> {
					return notifications.create(new NotificationOptions(title, "Body", "Category"));
				});
			}
			else {
				opt = clearAllFuture.thenCompose(ii -> {
					return notifications.create(new NotificationOptions(title, "Body", "Category"));
				});
			}
		}

		List<NotificationOptions> opts = TestUtils.runSync(opt.thenCompose(v -> {
			return notifications.getAll();
		}));

		assertEquals(cnt, opts.size());
	}

	@Test
	public void toggleNotificationCenter() throws Exception {
		TestUtils.runSync(notifications.create(new NotificationOptions("Title", "Body", "Category")).thenCompose(nOpts -> {
			return notifications.toggleNotificationCenter();
		}));
	}

	@Test
	public void addThenRemoveEventListener() throws Exception {
		CountDownLatch latch1 = new CountDownLatch(1);
		CountDownLatch latch2 = new CountDownLatch(2);
		
		NotificationEventListener listener = e -> {
			if (NotificationEvent.TYPE_CREATED.equals(e.getType())) {
				latch1.countDown();
				latch2.countDown();
			}
		};
		notifications.addEventListener(NotificationEvent.TYPE_CREATED, listener);
		
		notifications.create(new NotificationOptions("Title", "Body", "Category"));
		
		latch1.await(5, TimeUnit.SECONDS);

		assertEquals(0, latch1.getCount());
		
		notifications.removeEventListener(NotificationEvent.TYPE_CREATED, listener);

		notifications.create(new NotificationOptions("Title", "Body", "Category"));
		
		latch2.await(5, TimeUnit.SECONDS);

		assertEquals(1, latch2.getCount());
	}

	@Ignore
	@Test
	public void buttonAction() throws Exception {
		CountDownLatch latch1 = new CountDownLatch(1);
		
		NotificationEventListener listener = e -> {
			if (NotificationEvent.TYPE_ACTION.equals(e.getType())) {
				logger.debug("NotificationEvent.TYPE_ACTION fromJson: {}", e.getFromJson());
				java.lang.System.out.println("event source: " + ((NotificationActionEvent)e).getSource());
				java.lang.System.out.println("event result: " + ((NotificationActionEvent)e).getResult());
				latch1.countDown();
			}
		};
		notifications.addEventListener(NotificationEvent.TYPE_ACTION, listener);
		
		NotificationOptions opts = new NotificationOptions("Title", "Body", "Category");

		ButtonOptions btn1 = new ButtonOptions("btn 1");
		btn1.setOnClick(Json.createValue("clicked on button 1"));
		
		opts.setButtons(btn1);

		opts.setOnClose(Json.createObjectBuilder().add("action", "notification_onClose").build());
		
		notifications.create(opts);
				
		latch1.await(10, TimeUnit.SECONDS);

		assertEquals(0, latch1.getCount());
	}
	
	

}
