package com.mijibox.openfin.fdc3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.FinChannelClient;
import com.mijibox.openfin.FinRuntime;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.fdc3.channel.AppChannel;
import com.mijibox.openfin.fdc3.channel.ContextChannel;
import com.mijibox.openfin.fdc3.channel.DefaultChannel;
import com.mijibox.openfin.fdc3.channel.SystemChannel;
import com.mijibox.openfin.fdc3.context.Context;

public class FinDesktopAgent {
	private final static Logger logger = LoggerFactory.getLogger(FinDesktopAgent.class);
	
	private FinRuntime fin;
	private CompletionStage<FinChannelClient> channelClientFuture;
	private List<ContextListener> contextListeners;
	private List<EventListener> eventListeners;
	private Map<String, List<IntentListener>> intentListenersMap;

	public FinDesktopAgent(FinRuntime ofRuntime) {
		this.fin = ofRuntime;
		this.contextListeners = new CopyOnWriteArrayList<>();
		this.eventListeners = new CopyOnWriteArrayList<>();
		this.intentListenersMap = new ConcurrentHashMap<>();
		
		this.channelClientFuture = fin.Channel.connect("of-fdc3-service-v1").thenApply(client->{
			logger.debug("fdc3 client connected, endpointId: {}", client.getClientIdentity().getEndpointId());
			
			client.register("RECEIVE-CONTEXT", (payload, senderIdentity) -> {
				logger.debug("received context: {}", payload);
				Context context = FinBeanUtils.fromJsonObject(payload.asJsonObject().getJsonObject("context"), Context.class);
				for (ContextListener listener : this.contextListeners) {
					listener.onContext(context);
				}
				return null;
			});

			client.register("RECEIVE-INTENT", (payload, senderIdentity) -> {
				logger.debug("received intent: {}", payload);
				JsonObject payloadObj = payload.asJsonObject();
				String intent = payloadObj.getString("intent");
				List<IntentListener> listeners = this.intentListenersMap.get(intent);
				JsonValue result = null;
				if (listeners != null) {
					Context context = FinBeanUtils.fromJsonObject(payloadObj.getJsonObject("context"), Context.class);
					for (int i = 0; i < listeners.size(); i++) {
						JsonValue listenerResult = listeners.get(i).onIntent(context);
						if (i == 0) {
							result = listenerResult;
						}
					}
				}
				return result;
			});

			client.register("CHANNEL-RECEIVE-CONTEXT", (payload, senderIdentity) -> {
				logger.debug("received channel context: {}", payload);
				return null;
			});

			client.register("event", (payload, senderIdentity) -> {
				logger.debug("fdc3 event: {}", payload);
				return null;
			});
			return client;
		});
	}
	
	public CompletionStage<Boolean> addContextListener(ContextListener listener) {
		return this.channelClientFuture.thenCompose(channel -> {
			return channel.dispatch("ADD-CONTEXT-LISTENER");
		}).thenApply(result  -> {
			return this.contextListeners.add(listener);
		});
	}

	public CompletionStage<Boolean> addEventListener(EventListener listener) {
		return this.channelClientFuture.thenApply(result -> {
			return this.eventListeners.add(listener);
		});
	}

	public CompletionStage<Boolean> addIntentListener(String intent, IntentListener listener) {
		JsonObject payload = Json.createObjectBuilder().add("intent", intent).build();
		return this.channelClientFuture.thenCompose(channel -> {
			return channel.dispatch("ADD-INTENT-LISTENER", payload);
		}).thenApply(result -> {
			List<IntentListener> listeners = this.intentListenersMap.computeIfAbsent(intent,
					k -> new CopyOnWriteArrayList<>());
			return listeners.add(listener);
		});
	}
	
	public CompletionStage<Boolean> removeContextListener(ContextListener listener) {
		return this.channelClientFuture.thenCompose(channel -> {
			return channel.dispatch("REMOVE-CONTEXT-LISTENER");
		}).thenApply(result -> {
			return this.contextListeners.remove(listener);
		});
	}

	public CompletionStage<Boolean> removeIntentListener(String intent, IntentListener listener) {
		JsonObject payload = Json.createObjectBuilder().add("intent", intent).build();
		return this.channelClientFuture.thenCompose(channel -> {
			return channel.dispatch("REMOVE-INTENT-LISTENER", payload);
		}).thenApply(result -> {
			List<IntentListener> listeners = this.intentListenersMap.get(intent);
			if (listeners != null) {
				return listeners.remove(listener);
			}
			else {
				return false;
			}
		});
	}

	public CompletionStage<Boolean> removeEventListener(EventListener listener) {
		return this.channelClientFuture.thenApply(result -> {
			return this.eventListeners.remove(listener);
		});
	}

	public CompletionStage<IntentResolution> raiseIntent(String intent, Context context, String target) {
		return this.channelClientFuture.thenCompose(channelClient->{
			JsonObjectBuilder builder = Json.createObjectBuilder().add("intent", intent)
					.add("context", FinBeanUtils.toJsonObject(context));
			if (target != null) {
				builder.add("target", target);
			}
			return channelClient.dispatch("RAISE-INTENT", builder.build()).thenApply(result->{
				logger.debug("intent resolution: {}", result);
				return FinBeanUtils.fromJsonObject(result.asJsonObject(), IntentResolution.class);
			});
		});
	}

	public CompletionStage<Void> open(String appName, Context context) {
		return this.channelClientFuture.thenCompose(channelClient->{
			JsonObjectBuilder builder = Json.createObjectBuilder().add("name", appName);
			if (context != null) {
				builder.add("context", FinBeanUtils.toJsonObject(context));
			}
			return channelClient.dispatch("OPEN", builder.build()).thenAccept(result->{
			});
		});
	}

	public CompletionStage<Void> broadcast(Context context) {
		return this.channelClientFuture.thenCompose(client -> {
			return client
					.dispatch("BROADCAST",
							Json.createObjectBuilder().add("context", FinBeanUtils.toJsonObject(context)).build())
					.thenAccept(result -> {
					});
		});
	}

	public CompletionStage<AppIntent> findIntent(String intent) {
		return this.findIntent(intent, null);
	}
	
	public CompletionStage<AppIntent> findIntent(String intent, Context context) {
		return this.channelClientFuture.thenCompose(client -> {
			JsonObjectBuilder builder = Json.createObjectBuilder().add("intent", intent);
			if (context != null) {
				builder.add("context", FinBeanUtils.toJsonObject(context));
			}
			return client.dispatch("FIND-INTENT", builder.build()).thenApply(result -> {
				return FinBeanUtils.fromJsonObject(result.asJsonObject(), AppIntent.class);
			});
		});
	}

	public CompletionStage<List<AppIntent>> findIntentsByContext(Context context) {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("FIND-INTENTS-BY-CONTEXT", Json.createObjectBuilder().add("context", FinBeanUtils.toJsonObject(context)).build()).thenApply(result -> {
				JsonArray resultJson = result.asJsonArray();
				int resultCnt = resultJson.size();
				List<AppIntent> intents = new ArrayList<>(resultCnt);
				for (int i = 0; i < resultCnt; i++) {
					intents.add(FinBeanUtils.fromJsonObject(resultJson.get(i).asJsonObject(), AppIntent.class));
				}
				return intents;
			});
		});
	}
	
	//need to fix this.
	static ContextChannel createChannelFromJson(JsonObject resultJson) {
		String channelType = resultJson.getString("type");
		if ("default".equals(channelType)) {
			return new DefaultChannel();
		}
		else if ("system".equals(channelType)) {
			return new SystemChannel();
		}
		else if ("app".equals(channelType)) {
			return new AppChannel();
		}
		else {
			return new ContextChannel();
		}
	}


	public CompletionStage<ContextChannel> getCurrentChannel() {
		return this.getCurrentChannel(null);
	}
	
	public CompletionStage<ContextChannel> getCurrentChannel(Identity identity) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (identity != null) {
			builder.add("identity", FinBeanUtils.toJsonObject(identity));
		}
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("GET-CURRENT-CHANNEL", builder.build()).thenApply(result -> {
				return createChannelFromJson(result.asJsonObject());
			});
		});
	}

	public CompletionStage<ContextChannel> getChannelById(String id) {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("GET-CHANNEL-BY-ID", Json.createObjectBuilder().add("id", id).build()).thenApply(result -> {
				return createChannelFromJson(result.asJsonObject());
			});
		});
	}

	public CompletionStage<ContextChannel> getOrCreateAppChannel(String name) {
		return this.channelClientFuture.thenCompose(client -> {
			return client.dispatch("GET-OR-CREATE-APP-CHANNEL", Json.createObjectBuilder().add("name", name).build()).thenApply(result -> {
				return createChannelFromJson(result.asJsonObject());
			});
		});
	}
	
	public CompletionStage<Void> dispose() {
		return this.channelClientFuture.thenCompose(client->{
			return client.disconnect();
		});
	}
	
	public CompletionStage<Void> join(ContextChannel channel) {
		return this.channelClientFuture.thenCompose(client->{
			return client.dispatch("CHANNEL-JOIN", Json.createObjectBuilder().add("id", channel.getId()).build()).thenAccept(result->{
			});
		});
	}


}
