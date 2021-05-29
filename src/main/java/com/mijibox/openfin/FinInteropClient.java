package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.Json;

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
	
	public CompletionStage<Void> joinContextGroup(String contextGroupId) {
		return this.channelClient
				.dispatch("joinContextGroup", Json.createObjectBuilder().add("contextGroupId", contextGroupId).build())
				.thenAccept(result -> {
					
				});
	}

}
