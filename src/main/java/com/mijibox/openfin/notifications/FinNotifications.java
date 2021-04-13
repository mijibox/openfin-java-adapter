package com.mijibox.openfin.notifications;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinChannelClient;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.sun.jna.Platform;

public class FinNotifications {
	
	private final static Logger logger = LoggerFactory.getLogger(FinNotifications.class);

	private CompletionStage<FinChannelClient> channelClientFuture;
	private FinRuntime fin;
	private ConcurrentHashMap<String, CopyOnWriteArrayList<NotificationEventListener>> eventListenerMap;

	public FinNotifications(FinRuntime ofRuntime) {
		this.fin = ofRuntime;
		this.eventListenerMap = new ConcurrentHashMap<>();
		this.eventListenerMap.put(NotificationEvent.TYPE_ACTION, new CopyOnWriteArrayList<NotificationEventListener>());
		this.eventListenerMap.put(NotificationEvent.TYPE_CREATED, new CopyOnWriteArrayList<NotificationEventListener>());
		this.eventListenerMap.put(NotificationEvent.TYPE_CLOSED, new CopyOnWriteArrayList<NotificationEventListener>());

		if (Platform.isWindows() && Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				//it will fail if rvm is not already installed
				desktop.browse(new URI("fins://system-apps/notification-center"));
			}
			catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		else {
			fin.Application.startFromManifest(fin.getAssetsUrl() + "/release/system-apps/notification-center/app.json");
		}
		this.channelClientFuture = fin.Channel.connect("of-notifications-service-v1").thenCompose(client->{
			client.register("event", (payload, senderIdentity) -> {
				logger.debug("channel client event: {}", payload);
				this.invokeEventListeners(payload.asJsonObject());
				return null;
			});
			return client.dispatch("add-event-listener", Json.createValue(NotificationEvent.TYPE_ACTION)).thenApply(result->{
				return client;
			});
		});
	}
	
	private void invokeEventListeners(JsonObject payload) {
		logger.debug("invokeEventListeners, payload: {}", payload);
		String eventType = payload.getString("type");
		CopyOnWriteArrayList<NotificationEventListener> listeners = this.eventListenerMap.get(eventType);
		for (NotificationEventListener listener : listeners) {

			NotificationEvent event = null;
			if (NotificationEvent.TYPE_ACTION.equals(eventType)) {
				event = FinBeanUtils.fromJsonObject(payload, NotificationActionEvent.class);
			}
			else if (NotificationEvent.TYPE_CREATED.equals(eventType)) {
				event = FinBeanUtils.fromJsonObject(payload, NotificationCreatedEvent.class);
			}
			else if (NotificationEvent.TYPE_CLOSED.equals(eventType)) {
				event = FinBeanUtils.fromJsonObject(payload, NotificationClosedEvent.class);
			}

			if (event != null) {
				listener.onEvent(event);
			}
			else {
				// error
			}
		}
	}

	/**
	 * Creates a new notification.
	 * 
	 * The notification will appear in the Notification Center and as a toast if the
	 * Center is not visible.
	 * 
	 * If a notification is created with an id of an already existing notification,
	 * the existing notification will be recreated with the new content.
	 * 
	 * @param options
	 *            Notification configuration options.
	 * @return new CompletionStage for the fully-hydrated NotificationOptions.
	 */
	public CompletionStage<NotificationOptions> create(NotificationOptions options) {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("create-notification", FinBeanUtils.toJsonObject(options)).thenApply(result -> {
				return FinBeanUtils.fromJsonObject(result.asJsonObject(), NotificationOptions.class);
			});
		});
	}

	/**
	 * Get notification service provider status.
	 * @return new CompletionStage for the ProviderStatus.
	 */
	public CompletionStage<ProviderStatus> getProviderStatus() {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("get-provider-status").thenApply(result -> {
				return FinBeanUtils.fromJsonObject(result.asJsonObject(), ProviderStatus.class);
			});
		});
	}
	
	/**
	 * Clears a specific notification from the Notification Center.
	 * 
	 * @param id
	 *            ID of the notification to clear.
	 * @return new CompletionStage for the status, true if the notification was
	 *         successfully cleared. false if the notification was not cleared,
	 *         without errors.
	 */
	public CompletionStage<Boolean> clear(String id) {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("clear-notification", Json.createObjectBuilder().add("id", id).build()).thenApply(result->{
				return JsonValue.ValueType.TRUE.equals(result.getValueType()); 
			});
		});
	}

	/**
	 * Clears all Notifications which were created by the calling application,
	 * including child windows.
	 * 
	 * @return new CompletionStage for the the number of successfully cleared
	 *         Notifications.
	 */
	public CompletionStage<Integer> clearAll() {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("clear-app-notifications").thenApply(result -> {
				if (JsonValue.ValueType.NUMBER.equals(result.getValueType())) {
					return ((JsonNumber)result).intValue();
				}
				else {
					throw new RuntimeException("error clearAll");
				}
			});
		});
	}

	/**
	 * Retrieves all Notifications which were created by the calling application,
	 * including child windows.
	 * 
	 * @return new CompletionStage for all NotificationOptions.
	 */
	public CompletionStage<List<NotificationOptions>> getAll() {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("fetch-app-notifications").thenApply(result -> {
				JsonArray resultArray = result.asJsonArray();
				int resultCnt = resultArray.size();
				ArrayList<NotificationOptions> resultList = new ArrayList<NotificationOptions>(resultCnt);
				for (int i = 0; i < resultCnt; i++) {
					resultList.add(FinBeanUtils.fromJsonObject(resultArray.get(i).asJsonObject(), NotificationOptions.class));
				}
				return resultList;
			});
		});
	}

	/**
	 * Toggles the visibility of the Notification Center.
	 * 
	 * @return new CompletionStage when the command is delivered.
	 */
	public CompletionStage<Void> toggleNotificationCenter() {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("toggle-notification-center").thenAccept(result -> {
			});
		});
	}

	/**
	 * Add a listener to handle specified notification events.
	 * @param eventType TYPE_ACTION, TYPE_CREATED or TYPE_CLOSED.
	 * @param listener the event listener to be added.
	 * @return true if the listener was added as a result of the call.
	 */
	public boolean addEventListener(String eventType, NotificationEventListener listener) {
		return this.eventListenerMap.get(eventType).add(listener);
	}

	/**
	 * Removes a listener previously added with addEventListener.
	 * @param eventType TYPE_ACTION, TYPE_CREATED or TYPE_CLOSED.
	 * @param listener the event listener to be removed.
	 * @return true if the listener was removed as a result of the call.
	 */
	public boolean removeEventListener(String eventType, NotificationEventListener listener) {
		return this.eventListenerMap.get(eventType).remove(listener);
	}



}
