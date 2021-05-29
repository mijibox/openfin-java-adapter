package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonArray;

import com.mijibox.openfin.bean.ClientIdentity;
import com.mijibox.openfin.bean.Context;
import com.mijibox.openfin.bean.ContextGroupInfo;
import com.mijibox.openfin.bean.FinBeanUtils;

public class FinInteropClient extends FinApiObject {

	private FinChannelClient channelClient;
	private String brokerName;
	
	FinInteropClient(FinConnectionImpl finConnection, String brokerName) {
		super(finConnection);
		this.brokerName = brokerName;
	}
	
	CompletionStage<FinInteropClient> connect() {
		return this.finConnection._channel.connect("interop-broker-" + this.brokerName).thenApply(cClient->{
			this.channelClient = cClient;
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
		return this.channelClient
				.dispatch("joinContextGroup", Json.createObjectBuilder().add("contextGroupId", contextGroupId).build())
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
