package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

public class FinInterop extends FinApiObject {

	FinInterop(FinConnectionImpl finConnection) {
		super(finConnection);
	}
	
	public CompletionStage<FinInteropClient> connect(String brokerName) {
		FinInteropClient client = new FinInteropClient(this.finConnection, brokerName);
		return client.connect();
	}

}
