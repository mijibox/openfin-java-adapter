package com.mijibox.openfin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.ClientIdentity;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.ProviderIdentity;
import com.mijibox.openfin.bean.RoutingInfo;

public class FinChannelClient extends FinChannelBase {
	private final static Logger logger = LoggerFactory.getLogger(FinChannelClient.class);

	private RoutingInfo routingInfo;
	private ClientIdentity clientIdentity;
	private ProviderIdentity providerIdentity;
	private List<FinEventListener> channelDisconnectListeners;

	FinChannelClient(FinConnectionImpl finConnection, RoutingInfo routingInfo) {
		super(finConnection);
		this.channelDisconnectListeners = new CopyOnWriteArrayList<>();
		this.routingInfo = routingInfo;
		this.clientIdentity = new ClientIdentity();
		this.clientIdentity.setUuid(this.finConnection.getUuid());
		this.clientIdentity.setName(this.finConnection.getUuid());
		this.clientIdentity.setEndpointId(this.routingInfo.getEndpointId());
		this.providerIdentity = FinBeanUtils.fromJsonString(FinBeanUtils.toJsonString(routingInfo), ProviderIdentity.class);
	}

	RoutingInfo getRoutingInfo() {
		return routingInfo;
	}
	
	/**
	 * Gets the identity of this ChannelClient instance.
	 * @return The identity of the channel client.
	 */
	public ClientIdentity getClientIdentity() {
		return this.clientIdentity;
	}
	
	/**
	 * Gets the identity of the ChannelProvider which this ChannelClient instance connected to.
	 * @return The identity of the channel provider.
	 */
	public ProviderIdentity getProviderIdentity() {
		return this.providerIdentity;
	}
	
	/**
	 * Disconnects from the channel.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> disconnect() {
		return this.finConnection.sendMessage("disconnect-from-channel", FinBeanUtils.toJsonObject(this.routingInfo)).thenAccept(ack->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error disconnecting channel client, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Dispatches the given action to the channel provider.
	 * @param action The action name.
	 * @return A new CompletionStage of the action result returned from the provider.
	 */
	public CompletionStage<JsonValue> dispatch(String action) {
		return this.dispatch(action, null);
	}
	
	/**
	 * Dispatches the given action with payload to the channel provider.
	 * @param action The action name.
	 * @param payload The action payload.
	 * @return A new CompletionStage of the action result returned from the provider.
	 */
	public CompletionStage<JsonValue> dispatch(String action, JsonValue payload) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.routingInfo))
				.add("providerIdentity", FinBeanUtils.toJsonObject(providerIdentity))
				.add("action", action);
		if (payload != null) {
			builder.add("payload", payload);
		}
		return this.finConnection.sendMessage("send-channel-message", builder.build()).thenApply(ack->{
			if (ack.isSuccess()) {
				return ack.getData().asJsonObject().get("result");
			}
			else {
				throw new RuntimeException("error invoking action, reason: " + ack.getReason());
			}
		});
	}
	
	/**
	 * Registers a listener that is called on channel disconnection. It is passed the disconnection event of the disconnecting channel.
	 * @param listener The listener to be added.
	 * @return true if the listener is added to the end of the listener list.
	 */
	public boolean addChannelDisconnectListener(FinEventListener listener) {
		return this.channelDisconnectListeners.add(listener);
	}
	
	/**
	 * Removes specified channel disconnection listener from the listener list.
	 * @param listener The listener to be removed.
	 * @return true if the listener is removed from the listener list.
	 */
	public boolean removeChannelDisconnectListener(FinEventListener listener) {
		return this.channelDisconnectListeners.remove(listener);
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
