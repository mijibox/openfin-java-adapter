package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.LayoutConfig;

public class FinLayoutObject extends FinInstanceObject {
	private final static Logger logger = LoggerFactory.getLogger(FinLayoutObject.class);
	
	public enum PresetLayout {
		COLUMNS("columns"),
		GRID("grid"),
		ROWS("rows"),
		TABS("tabs");
		
		private String type;

		PresetLayout(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return this.type;
		}
	}

	private CompletionStage<FinPlatformObject> platformObj;
	
	FinLayoutObject(FinConnectionImpl finConnection, Identity identity) {
		super(finConnection, identity);
		platformObj = this.finConnection._platform.wrap(identity.getUuid());
	}
	

	/**
	 * Returns the configuration of the window's layout. Returns the same information that is returned for all windows in getSnapshot.
	 * @return New CompletionStage for the configuration of the window's layout.
	 */
	public CompletionStage<LayoutConfig> getConfig() {
		return this.platformObj.thenCompose(pObj -> {
			return pObj.getChannelClient().thenCompose(cClient -> {
				return cClient.dispatch("get-frame-snapshot",
						Json.createObjectBuilder().add("target", FinBeanUtils.toJsonObject(this.identity)).build())
						.thenApply(result -> {
							return FinBeanUtils.fromJsonObject(result.asJsonObject(), LayoutConfig.class);
						});
			});
		});
	}

	/**
	 * Replaces a Platform window's layout with a preset layout arrangement using the existing Views attached to the window. The preset options are "columns", "grid", "rows", or "tabs".
	 * @param type "columns", "grid", "rows", or "tabs".
	 * @return New CompletionStage when the command is delivered.
	 */
	public CompletionStage<Void> applyPreset(PresetLayout type) {
		JsonObject payload = Json.createObjectBuilder().add("target", FinBeanUtils.toJsonObject(this.identity))
				.add("opts", Json.createObjectBuilder().add("presetType", type.toString()).build()).build();

		return this.platformObj.thenCompose(pObj -> {
			return pObj.getChannelClient().thenCompose(cClient -> {
				return cClient.dispatch("apply-preset-layout", payload).thenAccept(result -> {
				});
			});
		});
	}

	//
	/**
	 * Replaces a Platform window's layout with a new layout. Any views that were in the old layout but not the new layout will be destroyed.
	 * @param newLayout New layout to implement in the target window. 
	 * @return New CompletionStage when the command is delivered.
	 */
	public CompletionStage<Void> replace(LayoutConfig newLayout) {
		JsonObject payload = Json.createObjectBuilder().add("target", FinBeanUtils.toJsonObject(this.identity))
				.add("opts", Json.createObjectBuilder().add("layout", FinBeanUtils.toJsonObject(newLayout)).build()).build();
		
		return this.platformObj.thenCompose(pObj -> {
			return pObj.getChannelClient().thenCompose(cClient -> {
				return cClient.dispatch("replace-layout", payload).thenAccept(result -> {
					logger.debug("replace-layout result: {}", result);
				});
			});
		});
	}

}
