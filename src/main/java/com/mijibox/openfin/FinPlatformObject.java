package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.ApplySnapshotOptions;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.Snapshot;
import com.mijibox.openfin.bean.ViewOptions;
import com.mijibox.openfin.bean.WindowOptions;

public class FinPlatformObject extends FinInstanceObject {
	private final static Logger logger = LoggerFactory.getLogger(FinPlatformObject.class);

	private FinApplicationObject appObj;
	private CompletionStage<FinChannelClient> channelClient;

	FinPlatformObject(FinApplicationObject appObj) {
		super(appObj.finConnection, appObj.getIdentity());
		this.appObj = appObj;
		String channelName = "custom-frame-" + this.appObj.getIdentity().getUuid();
		this.channelClient = finConnection._channel.connect(channelName);
	}

	public CompletionStage<Boolean> addEventListener(String eventType, FinEventListener listener) {
		return this.appObj.addEventListener(eventType, listener);
	}
	
	public CompletionStage<Boolean> removeEventListener(String eventType, FinEventListener listener) {
		return this.appObj.removeEventListener(eventType, listener);
	}
	
	/**
	 * Creates a new view and attaches it to a specified target window.
	 * @param viewOpts View creation options
	 * @param target The window to which the new view is to be attached. If no target, create a view in a new window.
	 * @return new CompletionStage for the platform that had the view created.
	 */
	public CompletionStage<FinViewObject> createView(ViewOptions viewOpts, Identity target) {
		return this.channelClient.thenCompose(client->{
			JsonObjectBuilder builder = Json.createObjectBuilder().add("opts", FinBeanUtils.toJsonObject(viewOpts));
			if (target != null) {
				builder.add("target", FinBeanUtils.toJsonObject(target));
			}
			return client.dispatch("create-view", builder.build());
		}).thenApply(result -> {
			JsonObject viewIdentityJson = result.asJsonObject().getJsonObject("identity");
			return new FinViewObject(this.finConnection, FinBeanUtils.fromJsonObject(viewIdentityJson, Identity.class));
		});
	}
	
	/**
	 * Creates a new Window.
	 * @param winOpts Window creation options
	 * @return new CompletionStage for the platform that had the window created.
	 */
	public CompletionStage<FinWindowObject> createWindow(WindowOptions winOpts) {
		return this.channelClient.thenCompose(client->{
			return client.dispatch("create-view-container", FinBeanUtils.toJsonObject(winOpts));
		}).thenCompose(result->{
			Identity winIdentity = FinBeanUtils.fromJsonObject(result.asJsonObject().getJsonObject("identity"), Identity.class);
			return this.finConnection._window.wrap(winIdentity);
		});
	}

	/**
	 * Closes current platform, all its windows, and their views.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> quit() {
//		return this.channelClient.thenCompose(client->{
//			return client.dispatch("quit").thenAccept(result->{
//			});
//		});
		return this.appObj.quit(true);
	}
	
	/**
	 * Closes a specified view in a target window.
	 * @param view The view to be closed.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> closeView(FinViewObject view) {
		JsonObject payload = Json.createObjectBuilder().add("view", FinBeanUtils.toJsonObject(view.getIdentity())).build();
		return this.channelClient.thenCompose(client->{
			return client.dispatch("close-view", payload).thenAccept(result->{
			});
		});
	}
	
	/**
	 * Returns a snapshot of the platform in its current state. Can be used to
	 * restore an application to a previous state.
	 * 
	 * @return A new CompletionStage for the snapshot.
	 */
	public CompletionStage<Snapshot> getSnapshot() {
		return this.channelClient.thenCompose(client->{
			return client.dispatch("get-snapshot").thenApply(result->{
				return FinBeanUtils.fromJsonObject(result.asJsonObject(), Snapshot.class);
			});
		});
	}
	
	/**
	 * Adds a snapshot to a running Platform. Can optionally close existing windows and overwrite current platform state with that of a snapshot.
	 * @param requestedSnapshot Snapshot URL or file path.
	 * @param options Optional parameters to be used when applying the snapshot.
	 * @return A new CompletionStage for the Platform instance.
	 */
	public CompletionStage<FinPlatformObject> applySnapshot(String requestedSnapshot, ApplySnapshotOptions options) {
		return this.finConnection.sendMessage("get-application-manifest", Json.createObjectBuilder().add("manifestUrl", requestedSnapshot).build()).thenApply(ack->{
			return ack.getData().asJsonObject();
		}).thenCompose(snapshot->{
			JsonObjectBuilder builder = Json.createObjectBuilder().add("snapshot", snapshot);
			if (options != null) {
				builder.add("options", FinBeanUtils.toJsonObject(options));
			}
			return this.channelClient.thenCompose(client->{
				return client.dispatch("apply-snapshot", builder.build()).thenApply(result->{
					return this;
				});
			});
		});
	}
	
	/**
	 * Adds a snapshot to a running Platform. Can optionally close existing windows and overwrite current platform state with that of a snapshot.
	 * @param snapshot Snapshot data.
	 * @param options Optional parameters to be used when applying the snapshot.
	 * @return A new CompletionStage for the Platform instance.
	 */
	public CompletionStage<FinPlatformObject> applySnapshot(Snapshot snapshot, ApplySnapshotOptions options) {
		JsonObjectBuilder builder = Json.createObjectBuilder().add("snapshot", FinBeanUtils.toJsonObject(snapshot));
		if (options != null) {
			builder.add("options", FinBeanUtils.toJsonObject(options));
		}
		return this.channelClient.thenCompose(client->{
			return client.dispatch("apply-snapshot", builder.build()).thenApply(result->{
				return this;
			});
		});
	}
	

	CompletionStage<FinChannelClient> getChannelClient() {
		return this.channelClient;
	}
	
}
