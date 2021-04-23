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
		this.clientIdentity = FinBeanUtils.fromJsonString(FinBeanUtils.toJsonString(routingInfo), ClientIdentity.class);
		this.providerIdentity = FinBeanUtils.fromJsonString(FinBeanUtils.toJsonString(routingInfo), ProviderIdentity.class);
	}

	public RoutingInfo getRoutingInfo() {
		return routingInfo;
	}
	
	public ClientIdentity getClientIdentity() {
		return this.clientIdentity;
	}
	
	public CompletionStage<Void> disconnect() {
		return this.finConnection.sendMessage("disconnect-from-channel", FinBeanUtils.toJsonObject(this.routingInfo)).thenAccept(ack->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error disconnecting channel client, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<JsonValue> dispatch(String action) {
		return this.dispatch(action, null);
	}
	
	public CompletionStage<JsonValue> dispatch(String action, JsonValue payload) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(this.providerIdentity))
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
	
	public boolean addChannelDisconnectListener(FinEventListener listener) {
		return this.channelDisconnectListeners.add(listener);
	}
	
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
