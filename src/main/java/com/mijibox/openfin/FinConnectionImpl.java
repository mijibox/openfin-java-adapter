package com.mijibox.openfin;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.Ack;
import com.mijibox.openfin.bean.FinBeanUtils;

public class FinConnectionImpl implements FinRuntimeConnection, Listener {
	private final static Logger logger = LoggerFactory.getLogger(FinConnectionImpl.class);

	private StringBuilder receivedMessage;
	private AtomicInteger messageId;
	private ConcurrentHashMap<Integer, CompletableFuture<Ack>> ackMap;
	private int port;
	private WebSocket webSocket;
	private String connectionUuid;
	private CompletableFuture<FinConnectionImpl> authFuture;
	private List<FinRuntimeConnectionListener> finConnectionListeners;
	private boolean connected;
	private String licenseKey;
	private String configUrl;
	private CopyOnWriteArrayList<MessageListener> ofMessageListeners;
	private ReentrantLock websocketLock;
	private Boolean nonPersistent;
	
	Executor executor;

	FinApplication _application;
	FinChannel _channel;
	FinClipboard _clipboard;
	FinGlobalHotkey _globalHotkey;
	FinInterApplicationBus _interApplicationBus;
	FinLayout _layout;
	FinPlatform _platform;
	FinSystem _system;
	FinView _view;
	FinWindow _window;
	FinSubscriptionManager _subscriptionManager;

	FinConnectionImpl(String connectionUuid, int port, String licenseKey, String configUrl, Executor executor, Boolean nonPersistent) {
		this.connectionUuid = connectionUuid;
		this.port = port;
		this.licenseKey = licenseKey;
		this.configUrl = configUrl;
		this.receivedMessage = new StringBuilder();
		this.ackMap = new ConcurrentHashMap<>();
		this.messageId = new AtomicInteger(0);
		this.authFuture = new CompletableFuture<>();
		this.nonPersistent = nonPersistent;;
		this.executor = executor;
		this.finConnectionListeners = new ArrayList<>();
		this.ofMessageListeners = new CopyOnWriteArrayList<>();
		this.websocketLock = new ReentrantLock();
	}

	private void initApiObjects() {
		// sequence of these objects does matter
		this._subscriptionManager = new FinSubscriptionManager(this);
		this._interApplicationBus = new FinInterApplicationBus(this);
		this._channel = new FinChannel(this);
		this._application = new FinApplication(this);
		this._clipboard = new FinClipboard(this);
		this._globalHotkey = new FinGlobalHotkey(this);
		this._layout = new FinLayout(this);
		this._platform = new FinPlatform(this);
		this._system = new FinSystem(this);
		this._view = new FinView(this);
		this._window = new FinWindow(this);
	}

	@Override
	public String getUuid() {
		return this.connectionUuid;
	}

	@Override
	public boolean isConnected() {
		return this.connected;
	}

	CompletionStage<FinConnectionImpl> connect() {
		try {
			String endpointURI = "ws://localhost:" + this.port + "/";
			logger.debug("connecting to {}", endpointURI);
			HttpClient httpClient = HttpClient.newBuilder().executor(this.executor).build();
			httpClient.newWebSocketBuilder().buildAsync(new URI(endpointURI), this);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		finally {
		}
		return this.authFuture;
	}

	@Override
	public CompletionStage<Void> disconnect() {
		return this.webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "normal closure").thenAccept(ws->{
			
		});
	}

	private String getPackageVersion() {
		String v = "N/A";
		try {
			URL manifest = this.getClass().getClassLoader()
					.getResource("META-INF/maven/co.openfin/openfin-java-adapter/pom.properties");
			Properties prop = new Properties();
			InputStream inputStream = manifest.openStream();
			prop.load(inputStream);
			v = prop.getProperty("version");
			inputStream.close();
		}
		catch (Exception e) {
		}
		finally {
		}
		return v;
	}

	@Override
	public void onOpen(WebSocket webSocket) {
		this.connected = true;
		webSocket.request(1);
		this.webSocket = webSocket;
		logger.debug("websocket connected");
		JsonObject authPayload = Json.createObjectBuilder().add("action", "request-external-authorization")
				.add("payload", Json.createObjectBuilder().add("uuid", this.connectionUuid).add("type", "file-token")
						.add("licenseKey", this.licenseKey == null ? JsonValue.NULL : Json.createValue(this.licenseKey))
						.add("configUrl", this.configUrl == null ? JsonValue.NULL : Json.createValue(this.configUrl))
						.add("client",
								Json.createObjectBuilder().add("type", "java")
										.add("javaVendor", System.getProperty("java.vendor"))
										.add("javaVersion", System.getProperty("java.version"))
										.add("osName", System.getProperty("os.name"))
										.add("osVersion", System.getProperty("os.version"))
										.add("osArch", System.getProperty("os.arch"))
										.add("version", getPackageVersion()).build())
						.build())
				.build();
		this.sendWebSocketMessage(authPayload.toString());
	}

	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		receivedMessage.append(data);
		webSocket.request(1);
		if (last) {
			processMessage(receivedMessage.toString());
			receivedMessage.setLength(0);
		}
		return null;
	}

	@Override
	public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
		logger.debug("websocket closed, statusCode: {}, reason: {}", statusCode, reason);
		this.connected = false;
		for (FinRuntimeConnectionListener l : this.finConnectionListeners) {
			try {
				l.onClose(reason);
			}
			catch (Exception e) {
				logger.error("error invoking socket listener", e);
			}
		}
		return null;
	}

	@Override
	public void onError(WebSocket webSocket, Throwable error) {
		logger.debug("websocket error", error);
		this.connected = false;
		for (FinRuntimeConnectionListener l : this.finConnectionListeners) {
			try {
				l.onError(error);
			}
			catch (Exception e) {
				logger.error("error invoking socket listener", e);
			}
		}
	}

	/**
	 * only invoke when there will be a responding ack, otherwise use
	 * sendWebSocketMessage
	 * 
	 * @param action
	 *            action name
	 * @return response Ack from the action
	 */
	public CompletionStage<Ack> sendMessage(String action) {
		return this.sendMessage(action, JsonValue.EMPTY_JSON_OBJECT);
	}

	/**
	 * only invoke when there will be a responding ack, otherwise use
	 * sendWebSocketMessage
	 * 
	 * @param action
	 *            action name
	 * @param payload
	 *            action payload
	 * @return response Ack from the action
	 */
	public CompletionStage<Ack> sendMessage(String action, JsonObject payload) {
		return CompletableFuture.supplyAsync(() -> {
			return null;
		}, this.executor).thenCompose(v -> {
			if (this.connected) {
				int msgId = this.messageId.getAndIncrement();
				CompletableFuture<Ack> ackFuture = new CompletableFuture<>();
				this.ackMap.put(msgId, ackFuture);
				JsonObjectBuilder json = Json.createObjectBuilder();
				JsonObject msgJson = json.add("action", action).add("messageId", msgId).add("payload", payload).build();
				String msg = msgJson.toString();
				this.sendWebSocketMessage(msg);
				return ackFuture;
			}
			else {
				throw new RuntimeException("not connected");
			}
		});
	}

	private void sendWebSocketMessage(String msg) {
		logger.debug("sending: {}", msg);
		this.websocketLock.lock();
		this.webSocket.sendText(msg, true).thenAccept(ws -> {
			this.websocketLock.unlock();
		}).exceptionally(e -> {
			this.websocketLock.unlock();
			logger.error("error sending message over websocket", e);
			return null;
		});
	}

	private void processMessage(String message) {
		logger.debug("processMessage: {}", message);
		JsonReader jsonReader = Json.createReader(new StringReader(message));
		JsonObject receivedJson = jsonReader.readObject();
		String action = receivedJson.getString("action");
		JsonObject payload = receivedJson.getJsonObject("payload");
		if ("external-authorization-response".equals(action)) {
			String file = payload.getString("file");
			String token = payload.getString("token");
			try {
				Files.write(Paths.get(file), token.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
						StandardOpenOption.TRUNCATE_EXISTING);
				JsonObjectBuilder builder = Json.createObjectBuilder().add("uuid", this.connectionUuid)
						.add("type", "file-token");
				if (this.nonPersistent != null) {
					builder.add("nonPersistent", this.nonPersistent);
				}
				this.sendMessage("request-authorization", builder.build());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if ("authorization-response".equals(action)) {
			if (payload.getBoolean("success")) {
				this.initApiObjects();
				this.authFuture.complete(this);
			}
			else {
				this.authFuture.completeExceptionally(new RuntimeException(payload.getString("reason")));
			}

		}
		else if ("ack".equals(action)) {
			int correlationId = receivedJson.getInt("correlationId");
			CompletableFuture<Ack> ackFuture = this.ackMap.remove(correlationId);
			if (ackFuture == null) {
				logger.error("missing ackFuture, correlationId={}", correlationId);
			}
			else {
				
				ackFuture.completeAsync(()->{
					return FinBeanUtils.fromJsonObject(payload, Ack.class);
				}, this.executor);
			}
		}
		else {
			this.executor.execute(()->{
				for (MessageListener l : this.ofMessageListeners) {
					try {
						l.onMessage(action, payload);
					}
					catch (Exception ex) {
						logger.error("error invoking message listener", ex);
					}
				}
			});
		}
	}

	@Override
	public boolean addConnectionListener(FinRuntimeConnectionListener listener) {
		return this.finConnectionListeners.add(listener);
	}

	@Override
	public boolean  removeConnectionListener(FinRuntimeConnectionListener listener) {
		return this.finConnectionListeners.remove(listener);
	}

	@Override
	public boolean addMessageListener(MessageListener listener) {
		return this.ofMessageListeners.add(listener);
	}

	@Override
	public boolean removeMessageListener(MessageListener listener) {
		return this.ofMessageListeners.remove(listener);
	}

}
