package com.mijibox.openfin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.mijibox.openfin.bean.ApplicationOptions;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;

public class FinApplication extends FinApiObject {

	FinApplication(FinConnectionImpl finConnection) {
		super(finConnection);
	}

	/**
	 * Creates and starts a new Application.
	 * @param appOpts Options used to create the application.
	 * @return new CompletionStage for the new application instance.
	 */
	public CompletionStage<FinApplicationObject> start(ApplicationOptions appOpts) {
		return this.create(appOpts).thenCompose(appObj -> {
			return this.run(appObj, null);
		});
	}

	CompletionStage<FinApplicationObject> create(ApplicationOptions appOpts) {
		Identity appIdentity = new Identity(appOpts.getUuid(), appOpts.getUuid());
		return this.finConnection.sendMessage("create-application", FinBeanUtils.toJsonObject(appOpts)).thenApply(ack -> {
			if (ack.isSuccess()) {
				return new FinApplicationObject(this.finConnection, appIdentity);
			}
			else {
				throw new RuntimeException("error creating application, reason: " + ack.getReason());
			}
		});
	}

	CompletionStage<FinApplicationObject> run(FinApplicationObject appObj, String manifestUrl) {
		JsonObjectBuilder builder = Json.createObjectBuilder(FinBeanUtils.toJsonObject(appObj.getIdentity()));
		if (manifestUrl != null) {
			builder.add("manifestUrl", manifestUrl);
		}
		return this.finConnection.sendMessage("run-application", builder.build())
				.thenApply(ack -> {
					if (ack.isSuccess()) {
						return appObj;
					}
					else {
						throw new RuntimeException("error running application, reason: " + ack.getReason());
					}
				});
	}
	
	CompletionStage<FinApplicationObject> createFromManifest(String manifestUrl) {
		return this.finConnection.sendMessage("get-application-manifest",
				Json.createObjectBuilder().add("manifestUrl", manifestUrl).build()).thenApply(ack -> {
					if (ack.isSuccess()) {
						JsonObject appManifest = (JsonObject) ack.getData();
						String uuid;
						if (appManifest.containsKey("platform")) {
							uuid = appManifest.getJsonObject("platform").getString("uuid");
						}
						else {
							uuid = appManifest.getJsonObject("startup_app").getString("uuid");
						}
						return new FinApplicationObject(this.finConnection, new Identity(uuid, uuid));
					}
					else {
						throw new RuntimeException("error createFromManifest, reason: " + ack.getReason());
					}
				});
	}

	/**
	 * Retrieves application's manifest and returns a running instance of the application.
	 * @param manifestUrl The URL of the application's manifest.
	 * @return new CompletionStage for the new application instance.
	 */
	public CompletionStage<FinApplicationObject> startFromManifest(String manifestUrl) {
		return this.createFromManifest(manifestUrl).thenCompose(appObj->{
			return this.run(appObj, manifestUrl);
		});
	}

	/**
	 * Asynchronously returns an Application object that represents an existing application.
	 * @param identity
	 * @return new CompletionStage for the application instance.
	 */
	public CompletionStage<FinApplicationObject> wrap(Identity identity) {
		FinApplicationObject appObj = new FinApplicationObject(this.finConnection, identity);
		return CompletableFuture.completedStage(appObj);
	}
}
