package com.mijibox.openfin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.ChannelConnectionOptions;
import com.mijibox.openfin.bean.ClientIdentity;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.ProviderIdentity;
import com.mijibox.openfin.bean.RoutingInfo;

public class FinChannel extends FinApiObject {

	private final static Logger logger = LoggerFactory.getLogger(FinChannel.class);

	// channelId, channelProvider
	private Map<String, FinChannelProvider> providerMap;
	// endpointId, channelClient
	private Map<String, FinChannelClient> clientMap;
	private List<FinEventListener> channelConnectListeners;
	private List<FinEventListener> channelDisconnectListeners;
	private ConcurrentHashMap<String, CopyOnWriteArrayList<CompletableFuture<Void>>> pendingConnectionsMap;
	
	FinChannel(FinConnectionImpl finConnection) {
		super(finConnection);
		this.providerMap = new ConcurrentHashMap<>();
		this.clientMap = new ConcurrentHashMap<>();
		this.channelConnectListeners = new CopyOnWriteArrayList<>();
		this.channelDisconnectListeners = new CopyOnWriteArrayList<>();
		this.pendingConnectionsMap = new ConcurrentHashMap<>();

		finConnection.addMessageListener((action, payload) -> {
			if ("process-channel-message".equals(action)) {
				this.processChannelMessage(payload);
			}
			else if ("process-channel-connection".equals(action)) {
				this.processChannelConnection(payload);
			}
		});
		
		//this is very confusing, it's not for channel client connection, it's a channel that's connected to openfin runtime.
		finConnection._subscriptionManager.addListener("channel", "connected", event ->{
			logger.debug("channel connected: {}", event.getEventObject());
			this.fireChannelConnectedEvent(event);
			String channelName = event.getEventObject().getString("channelName");
			//check if pending connections
			this.pendingConnectionsMap.computeIfPresent(channelName, (key, value) ->{
				ArrayList<CompletableFuture<Void>> pendingConnectionFutures = new ArrayList<>(value);
				pendingConnectionFutures.forEach(future->{
					future.complete(null);
				});
				value.clear();
				return value;
			});
		});
		
		finConnection._subscriptionManager.addListener("channel", "disconnected", event ->{
			logger.debug("channel disconnected: {}", event.getEventObject());
			this.fireChannelDisconnectedEvent(event);
			String channelName = event.getEventObject().getString("channelName");
			this.clientMap.values().forEach(c->{
				if (channelName.equals(c.getRoutingInfo().getChannelName())) {
					c.fireChannelDisconnectedEvent(event);
				}
			});
		});

		finConnection._subscriptionManager.addListener("channel", "client-disconnected", event ->{
			logger.debug("channel client disconnected: {}", event.getEventObject());
			RoutingInfo routingInfo = FinBeanUtils.fromJsonObject(event.getEventObject(), RoutingInfo.class);
			FinChannelClient channelClient = this.clientMap.get(routingInfo.getEndpointId());
			if (channelClient != null) {
				channelClient.fireChannelDisconnectedEvent(event);
			}
			//check if has provider
			String channelName = routingInfo.getChannelName(); //it has channelName, no channelId
			this.providerMap.values().forEach(provider->{
				if (Objects.equals(provider.getProviderIdentity().getChannelName(), channelName)) {
					provider.fireClientDisconnectedEvent(event);
				}
			});
		});
	}
	
	private void processChannelMessage(JsonObject msgPayload) {
		logger.debug("processChannelMessage: {}", msgPayload);
		JsonObject senderIdentityJson = msgPayload.getJsonObject("senderIdentity");
		RoutingInfo senderIdentity = FinBeanUtils.fromJsonObject(senderIdentityJson, RoutingInfo.class);
		JsonObject providerIdentity = msgPayload.getJsonObject("providerIdentity");
		String action = msgPayload.getString("action");
		JsonObject ackToSender = msgPayload.getJsonObject("ackToSender");
		JsonValue payload = msgPayload.get("payload");
		JsonObject intendedTargetIdentity = msgPayload.getJsonObject("intendedTargetIdentity");
		String ackToSenderAction = ackToSender.getString("action");
		JsonObject ackToSenderPayload = ackToSender.getJsonObject("payload");
		
		try {
			if (intendedTargetIdentity.containsKey("endpointId")) { // target is channelClient
				String endpointId = intendedTargetIdentity.getString("endpointId");
				logger.debug("process message for channel client, endpointId: {}, actioin: {}", endpointId, action);
				FinChannelClient channelClient = this.clientMap.get(endpointId);
				if (channelClient != null && channelClient.hasAction(action)) {
					JsonValue result = channelClient.processAction(action, payload, senderIdentity);
					JsonPatch resultPatch = Json.createPatchBuilder().add("/payload/result", result == null ? JsonValue.NULL : result).build();
					ackToSenderPayload = resultPatch.apply(ackToSenderPayload);
					this.finConnection.sendMessage(ackToSenderAction, ackToSenderPayload);
				}
				else {
					JsonPatch resultPatch = Json.createPatchBuilder().replace("/success", false)
							.add("/reason", "No action registered at target for " + action).build();
					ackToSenderPayload = resultPatch.apply(ackToSenderPayload);
					this.finConnection.sendMessage(ackToSenderAction, ackToSenderPayload);
				}
			}
			else if (intendedTargetIdentity.containsKey("channelId")) { // target is channelProvider
				String channelId = intendedTargetIdentity.getString("channelId");
				logger.debug("process message for channel provider, channelId: {}", channelId);
				FinChannelProvider channelProvider = this.providerMap.get(channelId);
				if (channelProvider.hasAction(action)) {
					JsonValue result = channelProvider.processAction(action, payload, senderIdentity);
					JsonPatch resultPatch = Json.createPatchBuilder().add("/payload/result", result == null ? JsonValue.NULL : result).build();
					ackToSenderPayload = resultPatch.apply(ackToSenderPayload);
					this.finConnection.sendMessage(ackToSenderAction, ackToSenderPayload);
				}
				else {
					JsonPatch resultPatch = Json.createPatchBuilder().replace("/success", false)
							.add("/reason", "No action registered at target for " + action).build();
					ackToSenderPayload = resultPatch.apply(ackToSenderPayload);
					this.finConnection.sendMessage(ackToSenderAction, ackToSenderPayload);
				}
			}
			else {
				JsonPatch resultPatch = Json.createPatchBuilder().replace("/success", false)
						.add("/reason", "No action registered at target for " + action).build();
				ackToSenderPayload = resultPatch.apply(ackToSenderPayload);
				this.finConnection.sendMessage(ackToSenderAction, ackToSenderPayload);
			}
		}
		catch (Exception ex) {
			logger.error("error processing channel action", ex);
			JsonPatch resultPatch = Json.createPatchBuilder().replace("/success", false)
					.add("/reason", "Error processing channel action " + action + ", error: " + ex.getMessage()).build();
			ackToSenderPayload = resultPatch.apply(ackToSenderPayload);
			this.finConnection.sendMessage(ackToSenderAction, ackToSenderPayload);
		}
	}

	private void processChannelConnection(JsonObject payload) {
		ClientIdentity clientIdentity = FinBeanUtils.fromJsonObject(payload.getJsonObject("clientIdentity"),
				ClientIdentity.class);
		ProviderIdentity providerIdentity = FinBeanUtils.fromJsonObject(payload.getJsonObject("providerIdentity"),
				ProviderIdentity.class);
		JsonValue connectionPayload = payload.get("payload");
		JsonObject ackToSender = payload.getJsonObject("ackToSender");

		FinChannelProvider provider = this.providerMap.get(providerIdentity.getChannelId());
		String ackToSenderAction = ackToSender.getString("action");
		JsonObject ackToSenderPayload = ackToSender.getJsonObject("payload");
		if (provider == null) {
			JsonPatch resultPatch = Json.createPatchBuilder().replace("/success", false)
					.add("/reason", "Channel \"" + providerIdentity.getChannelName()  + "\" has been destroyed").build();
			ackToSenderPayload = resultPatch.apply(ackToSenderPayload);
			this.finConnection.sendMessage(ackToSenderAction, ackToSenderPayload);
		}
		else {
			provider.processConnection(clientIdentity, connectionPayload);
			this.finConnection.sendMessage(ackToSenderAction, ackToSenderPayload);
		}
	}

	/**
	 * Creates a new channel.
	 * @param channelName Name of the new channel.
	 * @return new CompletionStage of the new ChannelProvider instance of the channel.
	 */
	public CompletionStage<FinChannelProvider> create(String channelName) {
		JsonObject payload = Json.createObjectBuilder().add("channelName", channelName).build();
		return this.finConnection.sendMessage("create-channel", payload).thenApply(ack -> {
			if (ack.isSuccess()) {
				ProviderIdentity providerIdentity = FinBeanUtils.fromJsonObject((JsonObject) ack.getData(),
						ProviderIdentity.class);
				FinChannelProvider provider = new FinChannelProvider(this.finConnection, providerIdentity) {
					@Override
					public CompletionStage<Void> destroy() {
						return super.destroy().thenAccept(v->{
							logger.debug("provider {} destroyed, removing it from provider map using channelId: {}", providerIdentity.getChannelName(), providerIdentity.getChannelId());
							providerMap.remove(providerIdentity.getChannelId());
						});
					}
				};
				logger.debug("provider {} created, adding it to provider map using channelId: {}", providerIdentity.getChannelName(), providerIdentity.getChannelId());
				this.providerMap.put(providerIdentity.getChannelId(), provider);
				return provider;
			}
			else {
				throw new RuntimeException("error create provider, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Connects to a channel.
	 * @param channelName Name of the channel to connect to.
	 * @return new CompletionStage of the new ChannelClient instance of the channel.
	 */
	public CompletionStage<FinChannelClient> connect(String channelName) {
		return this.connect(channelName, null);
	}
	
	/**
	 * Connects to a channel.
	 * @param channelName Name of the channel to connect to.
	 * @param opts Channel connection options.
	 * @return new CompletionStage of the new ChannelClient instance of the channel.
	 */
	public CompletionStage<FinChannelClient> connect(String channelName, ChannelConnectionOptions opts) {
		CompletableFuture<FinChannelClient> channelClientFuture = new CompletableFuture<>();
		
		JsonObject payload = Json.createObjectBuilder(opts == null ? JsonValue.EMPTY_JSON_OBJECT : FinBeanUtils.toJsonObject(opts)).add("channelName", channelName).build();
		this.finConnection.sendMessage("connect-to-channel", payload).thenAccept(ack -> {
			if (ack.isSuccess()) {
				RoutingInfo routingInfo = FinBeanUtils.fromJsonObject((JsonObject) ack.getData(), RoutingInfo.class);
				logger.debug("connected to channel: {}, client routingInfo: {}", channelName, routingInfo);
				FinChannelClient channelClient = new FinChannelClient(this.finConnection, routingInfo) {
					@Override
					public CompletionStage<Void> disconnect() {
						return super.disconnect().thenAccept(v->{
							logger.debug("channelClient {} disconnected, removing it from client map", routingInfo.getEndpointId());
							clientMap.remove(routingInfo.getEndpointId());
						});
					}
				};
				if (this.clientMap.containsKey(routingInfo.getEndpointId())) {
					logger.error("already has client???? endpointId: {}", routingInfo.getEndpointId());
				}
				this.clientMap.put(routingInfo.getEndpointId(), channelClient);
				channelClientFuture.complete(channelClient);
			}
			else {
				if (!Objects.equals("internal-nack", ack.getReason()) || (opts != null && !opts.isWait())) {
					channelClientFuture.completeExceptionally(new RuntimeException("error connect to channel, reason: " + ack.getReason()));
				}
				else {
					logger.debug("channel[{}] not available, add to pending connection", channelName);
					CopyOnWriteArrayList<CompletableFuture<Void>> pendingFutures = this.pendingConnectionsMap.computeIfAbsent(channelName, key->{
						return new CopyOnWriteArrayList<>();
					});
					CompletableFuture<Void> pendingConnection = new CompletableFuture<>();
					pendingFutures.add(pendingConnection);
					pendingConnection.thenAccept(v -> {
						// channel provider connected, we can try to connect again.
						this.connect(channelName, opts).thenAccept(client -> {
							channelClientFuture.complete(client);
						});
					});
				}
			}
		});
		return channelClientFuture;
	}

	/**
	 * Adds the listener to the listener list to get the event when a ChannelProvider connects to OpenFin Runtime.
	 * @param listener The listener to be added.
	 * @return true if the lister is added to the end of the listener list.
	 */
	public boolean addChannelConnectListener(FinEventListener listener) {
		return this.channelConnectListeners.add(listener);
	}
	
	/**
	 * Adds the listener to the listener list to get the event when a ChannelProvider disconnects from OpenFin Runtime.
	 * @param listener The listener to be added.
	 * @return true if the lister is added to the end of the listener list.
	 */
	public boolean addChannelDisconnectListener(FinEventListener listener) {
		return this.channelDisconnectListeners.add(listener);
	}
	
	/**
	 * Removes the listener from the listener list that gets the event when a ChannelProvider connects to OpenFin Runtime.
	 * @param listener The listener to be removed.
	 * @return true if the listener is removed from the listener list.
	 */
	public boolean removeChannelConnectListener(FinEventListener listener) {
		return this.channelConnectListeners.remove(listener);
	}
	
	/**
	 * Removes the listener from the listener list that gets the event when a ChannelProvider disconnects from OpenFin Runtime.
	 * @param listener The listener to be removed.
	 * @return true if the listener is removed from the listener list.
	 */
	public boolean removeChannelDisconnectListener(FinEventListener listener) {
		return this.channelDisconnectListeners.remove(listener);
	}
	
	void fireChannelConnectedEvent(FinEvent event) {
		this.channelConnectListeners.forEach(l->{
			try {
				l.onEvent(event);
			}
			catch (Exception ex) {
				logger.error("error invoking channel connect listener", ex);
			}
		});
	}

	void fireChannelDisconnectedEvent(FinEvent event) {
		this.channelDisconnectListeners.forEach(l->{
			try {
				l.onEvent(event);
			}
			catch (Exception ex) {
				logger.error("error invoking channel disconnect listener", ex);
			}
		});
	}
}
