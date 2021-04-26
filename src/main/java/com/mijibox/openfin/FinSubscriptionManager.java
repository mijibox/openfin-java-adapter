package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.Json;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;

public class FinSubscriptionManager extends FinApiObject {

	private final static Logger logger = LoggerFactory.getLogger(FinSubscriptionManager.class);
	
	private ConcurrentHashMap<String, CopyOnWriteArrayList<FinEventListener>> listenerMap;
	private Identity emptyIdentity = new Identity();

	FinSubscriptionManager(FinConnectionImpl finConnection) {
		super(finConnection);
		this.listenerMap = new ConcurrentHashMap<>();
		this.finConnection.addIncomingMessageListener((action, payload)->{
			FinEvent event = new FinEvent(payload);
			if ("process-desktop-event".equals(action)) {
				Identity identity = FinBeanUtils.fromJsonObject(payload, Identity.class); //uuid and name
				Identity appIdentity = new Identity(identity.getUuid()); //uuid only
				String topic = payload.getString("topic");
				String type = payload.getString("type");
				if (!identity.equals(emptyIdentity)) {
					if (!appIdentity.equals(identity)) {
						this.invokeListeners(appIdentity, topic, type, event);
					}
					this.invokeListeners(identity, topic, type, event);
				}
				this.invokeListeners(this.emptyIdentity, topic, type, event);
			}
		});
	}
	
	private void invokeListeners(Identity identity, String topic, String eventType, FinEvent eventObj) {
		String key = this.getSubscriptionKey(identity, topic, eventType);
		CopyOnWriteArrayList<FinEventListener> listeners = this.listenerMap.get(key);
		if (listeners != null) {
			listeners.forEach(l ->{
				try {
					l.onEvent(eventObj);
				}
				catch (Exception e) {
					logger.error("error invoking listener", e);
				}
			});
		}
	}
	
	private String getSubscriptionKey(Identity identity, String topic, String eventType) {
		return identity+ "::" + topic + "::" + eventType;
	}
	
	public CompletionStage<Boolean> addListener(String topic, String eventType, FinEventListener listener) {
		return this.addListener(this.emptyIdentity, topic, eventType, listener);
	}
	
	public CompletionStage<Boolean> addListener(Identity identity, String topic, String eventType, FinEventListener listener) {
		String key = this.getSubscriptionKey(identity, topic, eventType);
		CopyOnWriteArrayList<FinEventListener> listeners = this.listenerMap.get(key);
		if (listeners == null) {
			CopyOnWriteArrayList<FinEventListener> existingListener = this.listenerMap.putIfAbsent(key,
					new CopyOnWriteArrayList<>(new FinEventListener[] { listener }));
			if (existingListener == null) {
				// first one, send out the subscription
				JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(identity))
						.add("topic", topic)
						.add("type", eventType).build();
				return this.finConnection.sendMessage("subscribe-to-desktop-event", payload).thenApply(ack -> {
					if (!ack.isSuccess()) {
						logger.error("error subscribe, reason: " + ack.getReason());
					}
					return ack.isSuccess();
				});
			}
			else {
				return CompletableFuture.completedStage(existingListener.add(listener));
			}
		}
		else {
			return CompletableFuture.completedStage(listeners.add(listener));
		}
	}
	
	public CompletionStage<Boolean> removeListener(String topic, String eventType, FinEventListener listener) {
		return this.removeListener(this.emptyIdentity, topic, eventType, listener);
	}

	public CompletionStage<Boolean> removeListener(Identity identity, String topic, String eventType, FinEventListener listener) {
		String key = this.getSubscriptionKey(identity, topic, eventType);
		CopyOnWriteArrayList<FinEventListener> listeners = this.listenerMap.get(key);
		if (listeners == null) {
			return CompletableFuture.completedStage(false);
		}
		else {
			boolean removed = listeners.remove(listener);
			if (removed && listeners.size() == 0) {
				CopyOnWriteArrayList<FinEventListener> existingListeners = this.listenerMap.computeIfPresent(key, (keyInMap, existingListener) -> {
					if (existingListener.size() == 0) {
						return null;
					}
					else {
						return existingListener;
					}
				});
				
				if (existingListeners == null) {
					JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(identity))
							.add("topic", topic)
							.add("type", eventType).build();
					return this.finConnection.sendMessage("unsubscribe-to-desktop-event", payload).thenApply(ack -> {
						if (!ack.isSuccess()) {
							logger.error("error subscribe, reason: " + ack.getReason());
						}
						return ack.isSuccess();
					});
				}
			}
			return CompletableFuture.completedStage(removed);
		}
	}
}
