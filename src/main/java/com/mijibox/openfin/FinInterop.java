package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

public class FinInterop extends FinApiObject {

	FinInterop(FinConnectionImpl finConnection) {
		super(finConnection);
	}
	
	/**
	 * Connects a client to an Interop broker. 
	 * @param brokerName The name of the Interop Broker to connect to.
	 * @return new CompletionStage of the Interop Client.
	 */
	public CompletionStage<FinInteropClient> connect(String brokerName) {
		FinInteropClient client = new FinInteropClient(this.finConnection, brokerName);
		return client.connect();
	}
}
