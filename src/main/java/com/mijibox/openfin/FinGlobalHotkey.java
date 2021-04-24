package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.json.Json;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;

/**
 * The GlobalHotkey module can register/unregister a global hotkeys.
 * @author Anthony
 *
 */
public class FinGlobalHotkey extends FinApiObject {
	
	@FunctionalInterface
	public interface HotkeyListener {
		public void onHotkey(String hotkey);
	}
	
	private final static Logger logger = LoggerFactory.getLogger(FinGlobalHotkey.class);
	
	private ConcurrentHashMap<String, CopyOnWriteArrayList<HotkeyListener>> listenerMap;

	FinGlobalHotkey(FinConnectionImpl finConnection) {
		super(finConnection);
		this.listenerMap = new ConcurrentHashMap<>();
		this.finConnection.addMessageListener((action, payload)->{
			if ("process-desktop-event".equals(action)) {
				String topic = payload.getString("topic");
				if ("global-hotkey".equals(topic)) {
					String type = payload.getString("type");
					CopyOnWriteArrayList<HotkeyListener> listeners = this.listenerMap.get(type);
					if (listeners != null) {
						listeners.forEach(l->{
							try {
								l.onHotkey(type);
							}
							catch (Exception ex) {
								logger.error("error invoking global hotkey listener", ex);
							}
						});
					}
				}
			}
		});
	}
	
	/**
	 * Registers a global hotkey listener with the operating system.
	 * @param hotkey A hotkey string.
	 * @param listener The listener to call when the registered hotkey is pressed by the user.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> register(String hotkey, HotkeyListener listener) {
		AtomicBoolean first = new AtomicBoolean(false);
		this.listenerMap.compute(hotkey, (key, listeners)->{
			if (listeners == null) {
				first.set(true);
				return new CopyOnWriteArrayList<>(new HotkeyListener[] {listener});
			}
			else {
				if (listeners.size() == 0) {
					first.set(true);
				}
				listeners.add(listener);
				return listeners;
			}
		});
		if (first.get()) {
			return this.finConnection.sendMessage("global-hotkey-register", Json.createObjectBuilder().add("hotkey", hotkey).build()).thenAccept(ack->{
				if (!ack.isSuccess()) {
					throw new RuntimeException("error register hotkey, reason: " + ack.getReason());
				}
			});
		}
		else {
			return CompletableFuture.completedStage(null);
		}
	}

	/**
	 * Unregisters a global hotkey with the operating system.
	 * @param hotkey The hotkey string
	 * @param listener The listener to be removed
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> unregister(String hotkey, HotkeyListener listener) {
		AtomicBoolean last = new AtomicBoolean(false);
		this.listenerMap.computeIfPresent(hotkey, (key, existingListeners)->{
			boolean removed = existingListeners.remove(listener);
			last.set(removed && existingListeners.size() == 0);
			return existingListeners;
		});
		if (last.get()) {
			return this.finConnection.sendMessage("global-hotkey-unregister", Json.createObjectBuilder().add("hotkey", hotkey).build()).thenAccept(ack->{
				if (!ack.isSuccess()) {
					throw new RuntimeException("error unregister hotkey, reason: " + ack.getReason());
				}
			});
		}
		else {
			return CompletableFuture.completedStage(null);
		}
	}

	/**
	 * Checks if a given hotkey has been registered
	 * @param hotkey The hotkey
	 * @return A new CompletionStage of the result.
	 */
	public CompletionStage<Boolean> isRegistered(String hotkey) {
		return this.finConnection.sendMessage("global-hotkey-is-registered", Json.createObjectBuilder().add("hotkey", hotkey).build()).thenApply(ack->{
			if (!ack.isSuccess()) {
				throw new RuntimeException("error unregister hotkey, reason: " + ack.getReason());
			}
			else {
				return JsonValue.TRUE.equals(ack.getData());
			}
		});
	}

}
