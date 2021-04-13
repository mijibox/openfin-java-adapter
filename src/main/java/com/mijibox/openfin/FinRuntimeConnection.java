package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.JsonObject;

import com.mijibox.openfin.bean.Ack;

public interface FinRuntimeConnection {
	
	interface MessageListener {
		public void onMessage(String action, JsonObject message);
	}
	
	public String getUuid();
	public CompletionStage<Ack> sendMessage(String action);
	public CompletionStage<Ack> sendMessage(String action, JsonObject payload);
	public CompletionStage<Void> disconnect();
	public boolean isConnected();
	public boolean addMessageListener(MessageListener listener);
	public boolean removeMessageListener(MessageListener listener);
	public boolean addConnectionListener(FinRuntimeConnectionListener connectionListener);
	public boolean removeConnectionListener(FinRuntimeConnectionListener connectionListener);
}
