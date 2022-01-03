package com.chaosthedude.notes.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import net.minecraft.client.Minecraft;

public class FileUtils {

	private static final Minecraft mc = Minecraft.getInstance();

	public static File getRootSaveDirectory() {
		final File saveDir = new File(mc.gameDirectory, "notes");
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}

		return saveDir;
	}

	public static boolean isNote(File file) {
		return getFileExtension(file).equals("txt");
	}

	public static String getFileName(File file) {
		return FilenameUtils.getBaseName(file.getAbsolutePath());
	}

	public static String getFileExtension(File file) {
		return FilenameUtils.getExtension(file.getAbsolutePath());
	}

}
