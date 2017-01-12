package com.chaosthedude.notes.note;

import java.io.File;

import javax.annotation.Nullable;

import com.chaosthedude.notes.util.FileUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class Scope {

	private static final Minecraft mc = Minecraft.getMinecraft();

	public static final Scope GLOBAL = new Scope("notes.scope.global", "global") {
		@Override
		public boolean isActive() {
			return true;
		}
	};

	public static final Scope LOCAL = new Scope("notes.scope.local", "local") {
		@Override
		public File getCurrentSaveDirectory() {
			final File saveDirFile = new File(getRootSaveDirectory(), getWorldName());
			if (!saveDirFile.exists()) {
				saveDirFile.mkdirs();
			}

			return saveDirFile;
		}

		@Override
		public boolean isActive() {
			return isLocal();
		}
	};

	public static final Scope REMOTE = new Scope("notes.scope.remote", "remote") {
		@Override
		public File getCurrentSaveDirectory() {
			final File saveDirFile = new File(getRootSaveDirectory(), getServerIP());
			if (!saveDirFile.exists()) {
				saveDirFile.mkdirs();
			}

			return saveDirFile;
		}

		@Override
		public boolean isActive() {
			return isLocal();
		}
	};

	private String unlocName;
	private String saveDir;

	public Scope(String unlocName, String saveDir) {
		this.unlocName = unlocName;
		this.saveDir = saveDir;
	}

	public String localize() {
		return I18n.format(unlocName);
	}

	public String format() {
		return "(" + localize() + ")";
	}

	public File getCurrentSaveDirectory() {
		return getRootSaveDirectory();
	}

	public File getRootSaveDirectory() {
		final File saveDirFile = new File(FileUtils.getRootSaveDirectory(), saveDir);
		if (!saveDirFile.exists()) {
			saveDirFile.mkdirs();
		}

		return saveDirFile;
	}

	public boolean isActive() {
		return false;
	}

	@Nullable
	public static Scope getCurrentScope() {
		if (isLocal()) {
			return LOCAL;
		} else if (isRemote()) {
			return REMOTE;
		}

		return null;
	}

	@Nullable
	public static Scope getCurrentScopeOrGlobal(boolean global) {
		return global ? GLOBAL : getCurrentScope();
	}

	@Nullable
	public static Scope getScopeFromParentFile(File parentFile) {
		if (parentFile.equals(GLOBAL.getRootSaveDirectory())) {
			return GLOBAL;
		} else if (parentFile.getParentFile().equals(LOCAL.getRootSaveDirectory())) {
			return LOCAL;
		} else if (parentFile.getParentFile().equals(REMOTE.getRootSaveDirectory())) {
			return REMOTE;
		}

		return null;
	}

	public static boolean isLocal() {
		return mc.isSingleplayer();
	}

	public static boolean isRemote() {
		return mc.getCurrentServerData() != null;
	}

	@Nullable
	public static String getServerIP() {
		if (isRemote()) {
			return mc.getCurrentServerData().serverIP;
		}

		return null;
	}

	@Nullable
	public static String getWorldName() {
		if (isLocal()) {
			return mc.getIntegratedServer().getFolderName();
		}

		return null;
	}

}
