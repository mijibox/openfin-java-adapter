package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

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
	
	FinInteropClient(FinConnectionImpl finConnection, String brokerName) {
		super(finConnection);
		this.brokerName = brokerName;
	}
	
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
	
	public CompletionStage<ContextGroupInfo> getInfoForContextGroup(String contextGroupId) {
		return this.channelClient
				.dispatch("getInfoForContextGroup", Json.createObjectBuilder().add("contextGroupId", contextGroupId).build())
				.thenApply(result -> {
					return FinBeanUtils.fromJsonObject(result.asJsonObject(), ContextGroupInfo.class);
				});
	}
	
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
	
	public CompletionStage<Void> joinContextGroup(String contextGroupId) {
		return this.joinContextGroup(contextGroupId, null);
	}
	
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
	
	public CompletionStage<Void> removeFromContextGroup() {
		return this.removeFromContextGroup(null);
	}
	
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
	
	public CompletionStage<Void> setContext(Context context) {
		return this.channelClient
				.dispatch("setContext", Json.createObjectBuilder().add("context", FinBeanUtils.toJsonObject(context)).build())
				.thenAccept(result -> {
				});
	}

}
