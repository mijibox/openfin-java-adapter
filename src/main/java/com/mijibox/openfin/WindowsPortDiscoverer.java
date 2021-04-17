package com.mijibox.openfin;

import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class WindowsPortDiscoverer {

	private final static Logger logger = LoggerFactory.getLogger(WindowsPortDiscoverer.class);
	private Executor executor;

	public WindowsPortDiscoverer(Executor executor) {
		this.executor = executor;
	}

	public CompletionStage<JsonObject> getPortInfo(String pipeName) {
		return CompletableFuture.supplyAsync(() -> {
			String namedPipeName = "\\\\.\\pipe\\chrome." + pipeName;
			logger.debug("creating named pipe: {}", namedPipeName);
			HANDLE hNamedPipe = Kernel32.INSTANCE.CreateNamedPipe(namedPipeName, WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
					WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
					1, // nMaxInstances,
					Byte.MAX_VALUE, // nOutBufferSize,
					Byte.MAX_VALUE, // nInBufferSize,
					1000, // nDefaultTimeOut,
					null); // lpSecurityAttributes

			if (Kernel32.INSTANCE.ConnectNamedPipe(hNamedPipe, null)) {
				logger.debug("connected to named pipe {}", namedPipeName);
			}
			else {
				logger.debug("error connecting to named pipe {}", namedPipeName);
			}

			byte[] buffer = new byte[4096];
			IntByReference lpNumberOfBytesRead = new IntByReference(0);
			Kernel32.INSTANCE.ReadFile(hNamedPipe, buffer, buffer.length, lpNumberOfBytesRead, null);

			ByteBuffer bb = ByteBuffer.wrap(buffer);
			bb.putInt(20, Kernel32.INSTANCE.GetCurrentProcessId());
			IntByReference lpNumberOfBytesWrite = new IntByReference(0);
			Kernel32.INSTANCE.WriteFile(hNamedPipe, buffer, buffer.length, lpNumberOfBytesWrite, null);
			Kernel32.INSTANCE.ReadFile(hNamedPipe, buffer, buffer.length, lpNumberOfBytesRead, null);
			logger.debug("read port info from named pipe, size:{}", lpNumberOfBytesRead.getValue());
			String portInfoJson = new String(buffer, 24, lpNumberOfBytesRead.getValue() - 24);
			logger.debug("portInfo: {}", portInfoJson);
			JsonReader jsonReader = Json.createReader(new StringReader(portInfoJson));
			Kernel32.INSTANCE.CloseHandle(hNamedPipe);
			return jsonReader.readObject();
		}, this.executor);
	}
}