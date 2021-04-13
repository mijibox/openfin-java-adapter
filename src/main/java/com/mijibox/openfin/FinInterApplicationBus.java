package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.Identity;

/**
 * A messaging bus that allows for pub/sub messaging between different applications.
 * @author Anthony
 *
 */
public class FinInterApplicationBus extends FinApiObject {
	private final static Logger logger = LoggerFactory.getLogger(FinInterApplicationBus.class);

	private ConcurrentHashMap<String, CopyOnWriteArrayList<FinIabMessageListener>> listenerMap;

	private FinRuntimeConnection.MessageListener ofMessageListener;

	FinInterApplicationBus(FinConnectionImpl connection) {
		super(connection);
		this.listenerMap = new ConcurrentHashMap<>();
		this.ofMessageListener = (action, payload)->{
			if ("process-message".equals(action)) {
				this.processMessage(payload);
			}
		};
		this.finConnection.addMessageListener(this.ofMessageListener);
	}

	/**
	 * Publishes a message to all applications running on OpenFin Runtime that are subscribed to the specified topic.
	 * @param topic The topic on which the message is sent
	 * @param message The message to be published. 
	 * @return the new CompletionStage
	 */
	public CompletionStage<Void> publish(String topic, JsonValue message) {
		JsonObject payload = Json.createObjectBuilder().add("topic", topic)
				.add("message", message).build();
		return this.finConnection.sendMessage("publish-message", payload).thenAcceptAsync(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error publish, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Sends a message to a specific application on a specific topic.
	 * @param destionation The identity of the application to which the message is sent
	 * @param topic The topic on which the message is sent
	 * @param message The message to be sent. 
	 * @return the new CompletionStage
	 */
	public CompletionStage<Void> send(Identity destionation, String topic, JsonValue message) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder = builder.add("destinationUuid", destionation.getUuid())
		.add("topic", topic)
		.add("message", message);
		if (destionation.getName() != null) {
			builder.add("destinationWindowName", destionation.getName());
		}
		JsonObject payload = builder.build();
		return this.finConnection.sendMessage("send-message", payload).thenAcceptAsync(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error send, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Subscribes to messages from the specified application on the specified topic.
	 * @param source Source identity
	 * @param topic The topic on which the message is sent
	 * @param listener The listener that is called when a message has been received
	 * @return the new CompletionStage
	 */
	public CompletionStage<Void> subscribe(Identity source, String topic, FinIabMessageListener listener) {
		if (source == null) {
			source = new Identity();
		}
		String uuid = source.getUuid() == null ? "*" : source.getUuid();
		String name = source.getName() == null ? "*" : source.getName();
		String key = this.getSubscriptionKey(uuid, name, topic);
		CopyOnWriteArrayList<FinIabMessageListener> listeners = this.listenerMap.get(key);
		if (listeners == null) {
			CopyOnWriteArrayList<FinIabMessageListener> existingListener = this.listenerMap.putIfAbsent(key,
					new CopyOnWriteArrayList<>(new FinIabMessageListener[] { listener }));
			if (existingListener == null) {
				// first one, send out the subscription
				JsonObject payload = Json.createObjectBuilder()
						.add("sourceUuid", uuid)
						.add("sourceWindowName", name)
						.add("topic", topic).build();
				return this.finConnection.sendMessage("subscribe", payload).thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException(
								"error subscribe, reason: " + ack.getReason());
					}
					logger.debug("added listener to new listener list, size={}", this.listenerMap.get(key).size());
				});
			}
			else {
				existingListener.add(listener);
				logger.debug("added listener to exising listener list, size={}", existingListener.size());
				return CompletableFuture.completedStage(null);
			}
		}
		else {
			listeners.add(listener);
			logger.debug("added listener to exising listener list, size={}", listeners.size());
			return CompletableFuture.completedStage(null);
		}
	}

	/**
	 * Unsubscribes to messages from the specified application on the specified topic.
	 * @param source Source identity
	 * @param topic The topic on which the message is sent
	 * @param listener the listener previously registered with subscribe()
	 * @return the new CompletionStage
	 */
	public CompletionStage<Void> unsubscribe(Identity source, String topic, FinIabMessageListener listener) {
		String uuid = source.getUuid() == null ? "*" : source.getUuid();
		String name = source.getName() == null ? "*" : source.getName();
		String key = this.getSubscriptionKey(uuid, name, topic);
		CopyOnWriteArrayList<FinIabMessageListener> listeners = this.listenerMap.get(key);
		if (listeners != null) {
			boolean removed = listeners.remove(listener);
			if (removed && listeners.size() == 0) {
				//last one, unsubscribe the topic
				JsonObject payload = Json.createObjectBuilder()
						.add("sourceUuid", uuid)
						.add("sourceWindowName", name)
						.add("topic", topic).build();
				return this.finConnection.sendMessage("unsubscribe", payload).thenAcceptAsync(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error unsubscribe, reason: " + ack.getReason());
					}
				});
			}
			else {
				return CompletableFuture.completedStage(null);
			}
		}
		else {
			//should be error
			return CompletableFuture.completedStage(null);
		}
		
	}

	private String getSubscriptionKey(String uuid, String name, String topic) {
		return uuid + "::" + name + "::" + topic;
	}

	private void processMessage(JsonObject payload) {
		// check if it has subsubscribed topic
		String sourceUuid = payload.getString("sourceUuid");
		String sourceWindowName = payload.getString("sourceWindowName");
		String topic = payload.getString("topic");

		Identity identity = new Identity(sourceUuid, sourceWindowName);

		//exact match
		String key = this.getSubscriptionKey(sourceUuid, sourceWindowName, topic);
		this.processMessage(key, identity, payload);
		//wildcard name
		key = this.getSubscriptionKey(sourceUuid, "*", topic);
		this.processMessage(key, identity, payload);
		//wildcard uuid and name
		key = this.getSubscriptionKey("*", "*", topic);
		this.processMessage(key, identity, payload);
	}
	
	private void processMessage(String key, Identity identity, JsonObject payload) {
		CopyOnWriteArrayList<FinIabMessageListener> listeners = this.listenerMap.get(key);
		if (listeners != null) {
			logger.debug("key: {}, listeners count: {}", key, listeners.size());
			listeners.forEach(l -> {
				JsonValue msg = payload.get("message");
				try {
					l.onMessage(identity, msg);
				}
				catch (Exception e) {
					logger.error("error invoking IAB message listener", e);
				}
				finally {

				}
			});
		}
	}
	
}