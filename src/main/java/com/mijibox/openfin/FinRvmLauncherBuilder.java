package com.mijibox.openfin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.sun.jna.platform.win32.Advapi32Util;
import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;
import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

public class FinRvmLauncherBuilder extends AbstractFinLauncherBuilder {

	private Path rvmInstallDirectory;
	private List<String> rvmOptions;

	public FinRvmLauncherBuilder() {
		this.rvmOptions = new ArrayList<>();
	}

	public FinRvmLauncherBuilder rvmInstallDirectory(Path rvmInstallDirectory) {
		this.rvmInstallDirectory = rvmInstallDirectory;
		return this;
	}
	
	Path getRvmInstallDirectory() {
		if (rvmInstallDirectory == null) {
			rvmInstallDirectory = super.getOpenFinDirectory();
		}
		return rvmInstallDirectory;
	}

	public FinRvmLauncherBuilder addRvmOptions(String rvmOption) {
		this.rvmOptions.add(rvmOption);
		return this;
	}

	List<String> getRvmOptions() {
		return this.rvmOptions;
	}

	@Override
	public FinLauncher build() {
		return new FinRvmLauncher(this);
	}
	
	public FinRvmLauncherBuilder doNotLaunch() {
		this.addRvmOptions("--do-not-launch");
		return this;
	}

	public FinRvmLauncherBuilder noUi() {
		this.addRvmOptions("--no-ui");
		return this;
	}

	public FinRvmLauncherBuilder onlySelfInstall() {
		this.addRvmOptions("--only-self-install");
		return this;
	}

	public FinRvmLauncherBuilder enableDiagnostics() {
		this.addRvmOptions("--diagnostics");
		return this;
	}
}
