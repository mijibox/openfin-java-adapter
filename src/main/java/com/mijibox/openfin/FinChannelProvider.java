package com.mijibox.openfin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.ClientIdentity;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.ProviderIdentity;

public class FinChannelProvider extends FinChannelBase {
	Logger logger = LoggerFactory.getLogger(FinChannelProvider.class);

	private ProviderIdentity providerIdentity;
	private Set<ClientIdentity> channelClients;
	private List<FinEventListener> clientConnectListeners;
	private List<FinEventListener> clientDisconnectListeners;

	FinChannelProvider(FinConnectionImpl finConnection, ProviderIdentity providerIdentity) {
		super(finConnection);
		this.providerIdentity = providerIdentity;
		this.channelClients = new HashSet<>();
		this.clientConnectListeners = new CopyOnWriteArrayList<>();
		this.clientDisconnectListeners = new CopyOnWriteArrayList<>();
	}

	public ProviderIdentity getProviderIdentity() {
		return providerIdentity;
	}

	void processConnection(ClientIdentity clientIdentity, JsonValue connectionPayload) {
		logger.debug("processConnection, client: {}, payload: {}", clientIdentity, connectionPayload);
		this.channelClients.add(clientIdentity);
		this.fireClientConnectedEvent(new FinEvent(Json.createObjectBuilder()
				.add("clientIdentity", FinBeanUtils.toJsonObject(clientIdentity))
				.add("payload", connectionPayload == null ? JsonValue.NULL : connectionPayload).build()));
	}
	
	public List<CompletionStage<JsonValue>> publish(String action) {
		return this.publish(action, null);
	}
	
	public List<CompletionStage<JsonValue>> publish(String action, JsonValue payload) {
		ArrayList<CompletionStage<JsonValue>> futures = new ArrayList<>(this.channelClients.size());
		this.channelClients.forEach(clientIdentity -> {
			futures.add(this.dispatch(clientIdentity, action, payload));
		});
		return futures;
	}
	
	public CompletionStage<JsonValue> dispatch(ClientIdentity clientIdentity, String action) {
		return this.dispatch(clientIdentity, action, null);
	}
	
	public CompletionStage<JsonValue> dispatch(ClientIdentity clientIdentity, String action, JsonValue payload) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(clientIdentity))
				.add("providerIdentity", FinBeanUtils.toJsonObject(this.providerIdentity)).add("action", action);
		if (payload != null) {
			builder.add("payload", payload);
		}
		return this.finConnection.sendMessage("send-channel-message", builder.build()).thenApply(ack -> {
			if (ack.isSuccess()) {
				return ack.getData().asJsonObject().get("result");
			}
			else {
				throw new RuntimeException("error invoking action, reason: " + ack.getReason());
			}
		});
	}

	public CompletionStage<Void> destroy() {
		return this.finConnection
				.sendMessage("destroy-channel",
						Json.createObjectBuilder().add("channelName", this.providerIdentity.getChannelName()).build())
				.thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error destroy channel, reason: " + ack.getReason());
					}
				});
	}
	
	public boolean addClientConnectListener(FinEventListener listener) {
		return this.clientConnectListeners.add(listener);
	}
	
	public boolean addClientDisconnectListener(FinEventListener listener) {
		return this.clientDisconnectListeners.add(listener);
	}
	
	public boolean removeClientConnectListener(FinEventListener listener) {
		return this.clientConnectListeners.remove(listener);
	}
	
	public boolean removeClientDisconnectListener(FinEventListener listener) {
		return this.clientDisconnectListeners.remove(listener);
	}
	
	void fireClientConnectedEvent(FinEvent event) {
		this.clientConnectListeners.forEach(l->{
			try {
				l.onEvent(event);
			}
			catch (Exception ex) {
				logger.error("error invoking channel client connect listener", ex);
			}
		});
	}

	void fireClientDisconnectedEvent(FinEvent event) {
		this.clientDisconnectListeners.forEach(l->{
			try {
				l.onEvent(event);
			}
			catch (Exception ex) {
				logger.error("error invoking channel client disconnect listener", ex);
			}
		});
	}
}
