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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
	private CompletableFuture<FinConnectionImpl> connectionFuture;
	private List<FinRuntimeConnectionListener> finConnectionListeners;
	private boolean connected;
	private String licenseKey;
	private String configUrl;
	private CopyOnWriteArrayList<MessageListener> ofMessageListeners;
	private Boolean nonPersistent;
	private AtomicBoolean running;
	private ArrayBlockingQueue<JsonObject> msgOutQueue;
	private ArrayBlockingQueue<String> msgInQueue;
	private CompletableFuture<Void> disconnectFuture;
	private int msgEnqueueTimeout;
	
	FinApplication _application;
	FinChannel _channel;
	FinClipboard _clipboard;
	FinGlobalHotkey _globalHotkey;
	FinInterApplicationBus _interApplicationBus;
	FinInterop _interop;
	FinLayout _layout;
	FinPlatform _platform;
	FinSystem _system;
	FinView _view;
	FinWindow _window;
	FinSubscriptionManager _subscriptionManager;
	
	Executor executor;


	FinConnectionImpl(String connectionUuid, int port, String licenseKey, String configUrl, Executor executor, Boolean nonPersistent) {
		this.connectionUuid = connectionUuid;
		this.port = port;
		this.licenseKey = licenseKey;
		this.configUrl = configUrl;
		this.executor = executor;
		this.receivedMessage = new StringBuilder();
		this.ackMap = new ConcurrentHashMap<>();
		this.messageId = new AtomicInteger(0);
		this.connectionFuture = new CompletableFuture<>();
		this.nonPersistent = nonPersistent;;
		this.finConnectionListeners = new ArrayList<>();
		this.ofMessageListeners = new CopyOnWriteArrayList<>();
		String strMsgEnqueueTimeout = System.getProperty("com.mijibox.openfin.msg_enqueue_timeout", "10");
		this.msgEnqueueTimeout = Integer.parseInt(strMsgEnqueueTimeout);
	}
	
	private void initMessageQueues() {
		this.running = new AtomicBoolean(true);
		String msgInQueueSize = System.getProperty("com.mijibox.openfin.msg_in.queue_size", "100");
		String msgOutQueueSize = System.getProperty("com.mijibox.openfin.msg_out.queue_size", "100");
		this.msgInQueue = new ArrayBlockingQueue<>(Integer.parseInt(msgInQueueSize));
		this.msgOutQueue = new ArrayBlockingQueue<>(Integer.parseInt(msgOutQueueSize));
		//incoming message process loop
		Thread tIn = new Thread(()->{
			logger.debug("{}: incoming message loop started", this.connectionUuid);
			while (running.get() || !this.msgInQueue.isEmpty()) {
				try {
					String msg = this.msgInQueue.poll(500, TimeUnit.MILLISECONDS);
					if (msg != null) {
						this.processMessage(msg);
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally {
				}
			}
			logger.debug("{}: incoming message loop ended", this.connectionUuid);
		}, this.connectionUuid + ":msgIn");
		tIn.start();
		//outgoing message process loop
		Thread tOut = new Thread(()->{
			logger.debug("{}: outgoing message loop started", this.connectionUuid);
			while (running.get() || !this.msgOutQueue.isEmpty()) {
				try {
					JsonObject msg = this.msgOutQueue.poll(500, TimeUnit.MILLISECONDS);
					if (msg != null) {
						String msgString = msg.toString();
						logger.debug("sending: {}", msgString);
						this.webSocket.sendText(msgString, true).join();
					}
				}
				catch(Exception e) {
					logger.error("error procesing outgoing message", e);
				}
				finally {
				}
			}
			logger.debug("{}: outgoing message loop ended", this.connectionUuid);
			this.webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "normal closure").thenAccept(ws->{
				this.disconnectFuture.complete(null);
			});
		}, this.connectionUuid + ":msgOut");
		tOut.start();
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
		this._interop = new FinInterop(this);
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
			this.initMessageQueues();
			
			String endpointURI = "ws://localhost:" + this.port + "/";
			logger.debug("connecting to {}", endpointURI);
			HttpClient httpClient = HttpClient.newBuilder().build();
			httpClient.newWebSocketBuilder()
					.buildAsync(new URI(endpointURI), this)
					.exceptionally(e->{
						logger.error("error connecting to websocket server", e);
						this.connectionFuture.completeExceptionally(e);
						return null;
					});
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		finally {
		}
		return this.connectionFuture;
	}

	@Override
	public CompletionStage<Void> disconnect() {
		this.running.set(false);
		this.disconnectFuture = new CompletableFuture<Void>();
		return this.disconnectFuture;
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
		logger.debug("websocket connected");
		this.connected = true;
		webSocket.request(1);
		this.webSocket = webSocket;
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
		this.msgOutQueue.add(authPayload);
	}

	@Override
	public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
		receivedMessage.append(data);
		webSocket.request(1);
		if (last) {
			try {
				String msg = receivedMessage.toString(); 
				boolean b = this.msgInQueue.offer(msg, this.msgEnqueueTimeout, TimeUnit.SECONDS);
				if (!b) {
					logger.error("incoming message queue full, message dropped: {}", msg);
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			receivedMessage.setLength(0);
		}
		return null;
	}

	@Override
	public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
		logger.debug("websocket closed, statusCode: {}, reason: {}", statusCode, reason);
		this.running.set(false);
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
		this.running.set(false);
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

	public CompletionStage<Ack> sendMessage(String action) {
		return this.sendMessage(action, JsonValue.EMPTY_JSON_OBJECT);
	}

	public CompletionStage<Ack> sendMessage(String action, JsonObject payload) {
		if (this.connected && this.running.get()) {
			CompletableFuture<Ack> ackFuture = new CompletableFuture<>();
			try {
				int msgId = this.messageId.getAndIncrement();
				this.ackMap.put(msgId, ackFuture);
				JsonObjectBuilder json = Json.createObjectBuilder();
				JsonObject msgJson = json.add("action", action).add("messageId", msgId).add("payload", payload).build();
					boolean b = this.msgOutQueue.offer(msgJson, this.msgEnqueueTimeout, TimeUnit.SECONDS);
					if (b) {
						return ackFuture;
					}
					else {
						//queue full
						return CompletableFuture.failedStage(new RuntimeException("error sending message, outging message queue full"));
					}
			}
			catch (Exception e) {
				return CompletableFuture.failedStage(new RuntimeException("error sending message", e));
			}
		}
		else {
			return CompletableFuture.failedStage(new RuntimeException("not connected"));
		}
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
				this.connectionFuture.complete(this);
			}
			else {
				this.connectionFuture.completeExceptionally(new RuntimeException(payload.getString("reason")));
			}
		}
		else if ("ack".equals(action)) {
			int correlationId = receivedJson.getInt("correlationId");
			CompletableFuture<Ack> ackFuture = this.ackMap.remove(correlationId);
			if (ackFuture == null) {
				logger.error("missing ackFuture, correlationId={}", correlationId);
			}
			else {
				ackFuture.complete(FinBeanUtils.fromJsonObject(payload, Ack.class));
			}
		}
		else {
			for (MessageListener l : this.ofMessageListeners) {
				try {
					l.onMessage(action, payload);
				}
				catch (Exception ex) {
					logger.error("error invoking message listener", ex);
				}
			}
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
	public boolean addIncomingMessageListener(MessageListener listener) {
		return this.ofMessageListeners.add(listener);
	}

	@Override
	public boolean removeIncomingMessageListener(MessageListener listener) {
		return this.ofMessageListeners.remove(listener);
	}

}
