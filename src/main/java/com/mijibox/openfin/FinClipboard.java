package com.mijibox.openfin;

import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

public class FinClipboard extends FinApiObject {

	/**
	 * clipboard type "clipboard"
	 */
	public final static String TYPE_CLIPBOARD = "clipboard";
	/**
	 * clipboard type "selection", only available on Linux.
	 */
	public final static String TYPE_SELECTION = "selection";

	FinClipboard(FinConnectionImpl finConnection) {
		super(finConnection);
	}

	/**
	 * Reads available formats.
	 * @return A new CompletionStage for all available formats.
	 */
	public CompletionStage<String[]> getAvailableFormats() {
		return this.getAvailableFormats(null);
	}

	/**
	 * Reads available formats for the clipboard type.
	 * @return A new CompletionStage for all available formats for the clipboard type.
	 */
	public CompletionStage<String[]> getAvailableFormats(String type) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (type != null) {
			builder.add("type", type);
		}
		return this.finConnection.sendMessage("clipboard-read-formats", builder.build()).thenApply(ack -> {
			if (ack.isSuccess()) {
				JsonArray formats = ack.getData().asJsonArray();
				String[] result = new String[formats.size()];
				for (int i = 0; i < formats.size(); i++) {
					result[0] = formats.getString(i);
				}
				return result;
			}
			else {
				throw new RuntimeException("error getAvailableFormats, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Reads the content in the clipboard as Html
	 * @return A new CompletionStage for the html content in the clipboard.
	 */
	public CompletionStage<String> readHtml() {
		return this.readHtml(null);
	}
	
	/**
	 * Reads the html content in the clipboard of specified type.
	 * @return A new CompletionStage for the html content in the clipboard.
	 */
	public CompletionStage<String> readHtml(String type) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (type != null) {
			builder.add("type", type);
		}
		return this.finConnection.sendMessage("clipboard-read-html", builder.build()).thenApply(ack -> {
			if (ack.isSuccess()) {
				return ((JsonString)ack.getData()).getString();
			}
			else {
				throw new RuntimeException("error readHtml, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Writes the html content to the clipboard. 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> writeHtml(String html) {
		return this.writeHtml(null, html);
	}
	
	/**
	 * Writes the html content to the clipboard of specified type.
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> writeHtml(String type, String html) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("data", html);
		if (type != null) {
			builder.add("type", type);
		}
		return this.finConnection.sendMessage("clipboard-write-html", builder.build()).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error writeHtml, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Reads the text content in the clipboard.
	 * @return A new CompletionStage for the text content in the clipboard.
	 */
	public CompletionStage<String> readText() {
		return this.readText(null);
	}
	
	/**
	 * Reads the text content in the clipboard of specified type.
	 * @return A new CompletionStage for the text content in the clipboard.
	 */
	public CompletionStage<String> readText(String type) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (type != null) {
			builder.add("type", type);
		}
		return this.finConnection.sendMessage("clipboard-read-text", builder.build()).thenApply(ack -> {
			if (ack.isSuccess()) {
				return ((JsonString)ack.getData()).getString();
			}
			else {
				throw new RuntimeException("error readText, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Writes the text content to the clipboard. 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> writeText(String text) {
		return this.writeText(null, text);
	}
	
	/**
	 * Writes the text content to the clipboard of specified type. 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> writeText(String type, String text) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("data", text);
		if (type != null) {
			builder.add("type", type);
		}
		return this.finConnection.sendMessage("clipboard-write-text", builder.build()).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error writeText, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Reads the RTF content in the clipboard.
	 * @return A new CompletionStage for the RTF content in the clipboard.
	 */
	public CompletionStage<String> readRtf() {
		return this.readRtf(null);
	}
	
	/**
	 * Reads the RTF content in the clipboard of specified type.
	 * @return A new CompletionStage for the RTF content in the clipboard.
	 */
	public CompletionStage<String> readRtf(String type) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (type != null) {
			builder.add("type", type);
		}
		return this.finConnection.sendMessage("clipboard-read-rtf", builder.build()).thenApply(ack -> {
			if (ack.isSuccess()) {
				return ((JsonString)ack.getData()).getString();
			}
			else {
				throw new RuntimeException("error readRtf, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Writes the RTF content to the clipboard. 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> writeRtf(String rtf) {
		return this.writeRtf(null, rtf);
	}
	
	/**
	 * Writes the RTF content to the clipboard of specified type. 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> writeRtf(String type, String rtf) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("data", rtf);
		if (type != null) {
			builder.add("type", type);
		}
		return this.finConnection.sendMessage("clipboard-write-rtf", builder.build()).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error writeRtf, reason: " + ack.getReason());
			}
		});
	}

	/**
	 * Writes text, html and RTF content to the clipboard of specified type. 
	 * @return A new CompletionStage for the task.
	 */
	public CompletionStage<Void> write(String type, String text, String html, String rtf) {
		JsonObjectBuilder payloadBuilder = Json.createObjectBuilder();
		JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
		if (text != null) {
			dataBuilder.add("text", text);
		}
		if (html != null) {
			dataBuilder.add("html", html);
		}
		if (rtf != null) {
			dataBuilder.add("rtf", rtf);
		}
		payloadBuilder.add("data", dataBuilder.build());
		if (type != null) {
			payloadBuilder.add("type", type);
		}
		return this.finConnection.sendMessage("clipboard-write", payloadBuilder.build()).thenAccept(ack -> {
			if (!ack.isSuccess()) {
				throw new RuntimeException("error writeRtf, reason: " + ack.getReason());
			}
		});
	}

}
