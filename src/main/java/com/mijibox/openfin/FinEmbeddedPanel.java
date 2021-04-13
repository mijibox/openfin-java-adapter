package com.mijibox.openfin;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Panel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mijibox.openfin.bean.Bounds;
import com.mijibox.openfin.bean.FinBeanUtils;
import com.mijibox.openfin.bean.Identity;
import com.mijibox.openfin.bean.ResizeRegion;
import com.mijibox.openfin.bean.WindowBounds;
import com.mijibox.openfin.bean.WindowOptions;
import com.mijibox.openfin.bean.WindowUpdatableOptions;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;

public class FinEmbeddedPanel extends Panel {
	private final static Logger logger = LoggerFactory.getLogger(FinEmbeddedPanel.class);

	private FinRuntime fin;
	private Identity windowIdentity;
	private Canvas canvas;
	private WindowBounds originalBounds;
	private FinWindowObject winObj;
	private WindowOptions originalWinOpt;
	private HWND openFinHwnd;
	private int originalWinStyle;
	private HWND previousParent;

	
	public FinEmbeddedPanel() {
		this.canvas = new Canvas();
		this.setLayout(new BorderLayout());
		this.add(this.canvas, BorderLayout.CENTER);
		this.canvas.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (winObj != null) {
//					setEmbeddedWindowBounds(0, 0, getWidth(), getHeight());
					User32.INSTANCE.MoveWindow(openFinHwnd, 0, 0,  getWidth(), getHeight(), true);
				}
			}
		});
	}

	private HWND getHwnd(long hwnd) {
		return new WinDef.HWND(Pointer.createConstant(hwnd));
	}

	private CompletionStage<Void> setEmbeddedWindowBounds(int x, int y, int width, int height) {
		return this.winObj.setBounds(new Bounds(x, y, width, height));
	}

	public CompletionStage<Void> embed(FinRuntime fin, Identity targetIdentity) {
		this.fin = fin;
		if (Platform.isWindows()) {
			long canvasId = Native.getComponentID(this.canvas);
			return fin.Window.wrap(targetIdentity).thenCompose(winObj -> {
				this.winObj = winObj;
				logger.debug("will embed: {} into {}", targetIdentity, canvasId);
				// get original bounds
				return winObj.getBounds();
			}).thenCompose(b -> {
				this.originalBounds = b;
				// get original options
				return this.winObj.getOptions();
			}).thenCompose(winOpts -> {
				this.originalWinOpt = winOpts;
				WindowUpdatableOptions newOpts = new WindowUpdatableOptions();
				ResizeRegion resizeRegion = new ResizeRegion();
				resizeRegion.setSize(0);
				resizeRegion.setBottomRightCorner(0);
				newOpts.setResizeRegion(resizeRegion);
				newOpts.setFrame(false);
				// set options when embedded
				return this.winObj.updateOptions(newOpts);
			}).thenCompose(v -> {
				// get native window id
				return this.winObj.getNativeId();
			}).thenCompose(nativeId -> {
				long openFinWinId = Long.decode(nativeId);
				this.openFinHwnd = this.getHwnd(openFinWinId);
				this.originalWinStyle = User32.INSTANCE.GetWindowLong(openFinHwnd, User32.GWL_EXSTYLE);
				User32.INSTANCE.ShowWindow(openFinHwnd, User32.SW_HIDE);
				int embeddedStyle = this.originalWinStyle & ~(User32.WS_POPUPWINDOW);
				embeddedStyle = embeddedStyle | User32.WS_CHILD;
				User32.INSTANCE.SetWindowLong(openFinHwnd, User32.GWL_EXSTYLE, embeddedStyle);
				this.previousParent = User32.INSTANCE.SetParent(openFinHwnd, this.getHwnd(canvasId));
				// match the window location and size as the canvas
				return setEmbeddedWindowBounds(0, 0, this.getWidth(), this.getHeight());
			}).thenCompose(rEmbed -> {
				// tell openfin runtime that we have embedded the window.
				JsonObject payload = Json.createObjectBuilder(FinBeanUtils.toJsonObject(targetIdentity))
						.add("parentHwnd", Long.toHexString(canvasId)).build();
				return fin.getConnection().sendMessage("window-embedded", payload).thenAccept(ack -> {
					if (!ack.isSuccess()) {
						throw new RuntimeException("error window-embedded, reason: " + ack.getReason());
					}
				});
			}).thenCompose(a -> {
				// if not already shown, make it visible.
				return this.winObj.show();
			}).thenAccept(a -> {

			});
		}
		else {
			return CompletableFuture.failedStage(new RuntimeException("Not implemented on this platform"));
		}
	}

	public void showEmbeddedWindowVisible(boolean visible) {
		SwingUtilities.invokeLater(() -> {
			this.canvas.setVisible(visible);
		});
	}

}
