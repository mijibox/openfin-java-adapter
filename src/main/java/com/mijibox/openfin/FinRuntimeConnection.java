package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.JsonObject;

import com.mijibox.openfin.bean.Ack;

/**
 * The connection between Java application and OpenFin Runtime.
 * @author Anthony
 *
 */
public interface FinRuntimeConnection {
	
	interface MessageListener {
		public void onMessage(String action, JsonObject message);
	}
	
	public String getUuid();
	/**
	 * Sends API message to OpenFin Runtime.
	 * 
	 * @param action
	 *            action name
	 * @return A new CompletinStage for the response Ack from the action.
	 */
	public CompletionStage<Ack> sendMessage(String action);
	/**
	 * Sends API message to OpenFin runtime with action payload.
	 * 
	 * @param action
	 *            action name
	 * @param payload
	 *            action payload
	 * @return A new CompletinStage for the response Ack from the action.
	 */
	public CompletionStage<Ack> sendMessage(String action, JsonObject payload);
	/**
	 * Disconnects the connection.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> disconnect();
	/**
	 * Checks if the connection is currently connected.
	 * @return true if it's connected to OpenFin Runtime.
	 */
	public boolean isConnected();
	/**
	 * Adds the message listener to the end of the listener list. 
	 * @param listener The message listener to be added.
	 * @return true if the listener is added to the end of the listener list.
	 */
	public boolean addIncomingMessageListener(MessageListener listener);
	/**
	 * Removes the message listener from the listener list.
	 * @param listener The message listener to be removed.
	 * @return true if the listener is removed from the listener list.
	 */
	public boolean removeIncomingMessageListener(MessageListener listener);
//	public boolean addOutgoingMessageListener(MessageListener listener);
//	public boolean removeOutgoingMessageListener(MessageListener listener);
	/**
	 * Adds a OpenFin runtime connection listener
	 * @param connectionListener The connection listener.
	 * @return true if the listener is added to the end of the listener list.
	 */
	public boolean addConnectionListener(FinRuntimeConnectionListener connectionListener);
	/**
	 * Removes specified OpenFin runtime connection listener
	 * @param connectionListener The connection listener.
	 * @return true if the listener is removed from the listener list.
	 */
	public boolean removeConnectionListener(FinRuntimeConnectionListener connectionListener);
}
