package com.mijibox.openfin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;

import com.mijibox.openfin.bean.ClientIdentity;
import com.mijibox.openfin.bean.Context;
import com.mijibox.openfin.bean.ContextGroupInfo;
import com.mijibox.openfin.bean.FinBeanUtils;

public class FinInteropClient extends FinApiObject {

	private FinChannelClient channelClient;
	private String brokerName;
	private ClientIdentity clientIdentity;
	private ConcurrentHashMap<String, List<FinContextListener>> contextTypeListenersMap;
	
	FinInteropClient(FinConnectionImpl finConnection, String brokerName) {
		super(finConnection);
		this.brokerName = brokerName;
		this.contextTypeListenersMap = new ConcurrentHashMap<>();
	}
	
	/**
	 * Gets the client identity of this interop client.
	 * @return The client identity.
	 */
	public ClientIdentity getClientIdentity() {
		return this.clientIdentity;
	}
	
	CompletionStage<FinInteropClient> connect() {
		return this.finConnection._channel.connect("interop-broker-" + this.brokerName).thenApply(cClient->{
			this.channelClient = cClient;
			this.clientIdentity = this.channelClient.getClientIdentity();
			return this;
		});
	}
	
	/**
	 * Returns the Interop-Broker-defined context groups available for an entity to join.
	 * @return new CompletionStage of an array of the context group info.
	 */
	public CompletionStage<ContextGroupInfo[]> getContextGroups() {
		return this.channelClient
				.dispatch("getContextGroups")
				.thenApply(result -> {
					JsonArray grps = result.asJsonArray();
					ContextGroupInfo[] ctxGrps = new ContextGroupInfo[grps.size()];
					for (int i=0; i<grps.size(); i++) {
						ctxGrps[i] = FinBeanUtils.fromJsonObject(grps.getJsonObject(i), ContextGroupInfo.class);
					}
					return ctxGrps;
				});
	}
	
	/**
	 * Gets display info for a context group.
	 * @param contextGroupId The id of context group you wish to get display info for.
	 * @return new CompletionStage of the context group info.
	 */
	public CompletionStage<ContextGroupInfo> getInfoForContextGroup(String contextGroupId) {
		return this.channelClient
				.dispatch("getInfoForContextGroup", Json.createObjectBuilder().add("contextGroupId", contextGroupId).build())
				.thenApply(result -> {
					return FinBeanUtils.fromJsonObject(result.asJsonObject(), ContextGroupInfo.class);
				});
	}
	
	/**
	 * Gets all clients for a context group. 
	 * @param contextGroupId The id of context group you wish to get clients for.
	 * @return new CompletionStage of all clients in the context group.
	 */
	public CompletionStage<ClientIdentity[]> getAllClientsInContextGroup(String contextGroupId) {
		return this.channelClient
				.dispatch("getAllClientsInContextGroup", Json.createObjectBuilder().add("contextGroupId", contextGroupId).build())
				.thenApply(result -> {
					JsonArray jsonClients = result.asJsonArray();
					ClientIdentity[] clients = new ClientIdentity[jsonClients.size()];
					for (int i=0; i<jsonClients.size(); i++) {
						clients[i] = FinBeanUtils.fromJsonObject(jsonClients.getJsonObject(i), ClientIdentity.class);
					}
					return clients;
				});
	}
	
	/**
	 * Joins this interop client to the context group.
	 * @param contextGroupId The id of context group.
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> joinContextGroup(String contextGroupId) {
		return this.joinContextGroup(contextGroupId, null);
	}
	
	/**
	 * Joins specified interop client to the context group.
	 * @param contextGroupId The id of context group.
	 * @param target The identity of target interop client.
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> joinContextGroup(String contextGroupId, ClientIdentity target) {
		JsonObjectBuilder builder = Json.createObjectBuilder().add("contextGroupId", contextGroupId);
		if (target != null) {
			builder.add("target", FinBeanUtils.toJsonObject(target));
		}
		return this.channelClient
				.dispatch("joinContextGroup", builder.build())
				.thenAccept(result -> {
				});
	}
	
	/**
	 * Removes this interop client from the context group which currently joined..
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> removeFromContextGroup() {
		return this.removeFromContextGroup(null);
	}
	
	/**
	 * Removes specified interop client from the context group which currently joined.
	 * @param target The identity of target interop client.
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> removeFromContextGroup(ClientIdentity target) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (target != null) {
			builder.add("target", FinBeanUtils.toJsonObject(target));
		}
		return this.channelClient
				.dispatch("removeFromContextGroup", builder.build())
				.thenAccept(result -> {
				});
	}
	
	/**
	 * Sets a context for the context group.
	 * @param context New context to set.
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> setContext(Context context) {
		return this.channelClient
				.dispatch("setContext", Json.createObjectBuilder().add("context", FinBeanUtils.toJsonObject(context)).build())
				.thenAccept(result -> {
				});
	}
	
	/**
	 * Add a context listener for incoming context.
	 * @param listener The context listener to be added.
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> addContextListener(FinContextListener listener) {
		return this.addContextListener(null, listener);
	}

	/**
	 * Add a context listener for incoming context of specified context type.
	 * @param contextType The type of context the listener listens to.
	 * @param listener The context listener to be added.
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> addContextListener(String contextType, FinContextListener listener) {
		String contextTypeKey = contextType == null ? "*" : contextType;
		AtomicBoolean first = new AtomicBoolean(false);
		List<FinContextListener> listeners = this.contextTypeListenersMap.computeIfAbsent(contextTypeKey, key->{
			first.set(true);
			return new ArrayList<>();
		});
		listeners.add(listener);
		if (first.get()) {
			String handlerId = "contextHandler-" + contextTypeKey;
			this.channelClient.register(handlerId, (payload, senderIdentity)->{
				Context context = FinBeanUtils.fromJsonObject(payload.asJsonObject(), Context.class);
				listeners.forEach(l->{
					l.onContext(context);
				});
				return null;
			});
			JsonObjectBuilder builder = Json.createObjectBuilder().add("handlerId", handlerId);
			if (contextType != null) {
				builder.add("contextType", contextType);
			}
			return this.channelClient.dispatch("contextHandlerRegistered", builder.build()).thenAccept(result->{
			});
		}
		else {
			return CompletableFuture.completedFuture(null);
		}
	}

	/**
	 * Removes the context listener from the listener list.
	 * @param listener The context listener to be removed..
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> removeContextListener(FinContextListener listener) {
		return this.removeContextListener(null, listener);
	}

	/**
	 * Removes the context listener of specified context type from the listener list.
	 * @param contextType The type of context the listener listens to.
	 * @param listener The context listener to be removed.
	 * @return new CompletionStage for the task.
	 */
	public CompletionStage<Void> removeContextListener(String contextType, FinContextListener listener) {
		String contextTypeKey = contextType == null ? "*" : contextType;
		AtomicBoolean last = new AtomicBoolean(false);
		this.contextTypeListenersMap.computeIfPresent(contextTypeKey, (key, listeners)->{
			boolean removed = listeners.remove(listener);
			if (removed && listeners.size() == 0) {
				last.set(true);
				return null;
			}
			else {
				return listeners;
			}
		});
		
		if (last.get()) {
			String handlerId = "contextHandler-" + contextTypeKey;
			this.channelClient.remove(handlerId);
			return this.channelClient
					.dispatch("removeContextHandler", Json.createObjectBuilder().add("handlerId", handlerId).build())
					.thenAccept(result -> {
					});
		}
		else {
			return CompletableFuture.completedFuture(null);
		}
	}
}
