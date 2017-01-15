package com.chaosthedude.notes.config;

import java.io.File;

import com.chaosthedude.notes.Notes;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler {

	public static Configuration config;

	public static String dateFormat = "M/d/yy h:mm a";
	public static boolean useInGameEditor = true;
	public static boolean useInGameViewer = true;
	public static double pinnedWidthScale = 0.2;
	public static double pinnedHeightScale = 1.0;
	public static boolean wrapNote = true;

	public static void loadConfig(File configFile) {
		config = new Configuration(configFile);

		config.load();
		init();

		MinecraftForge.EVENT_BUS.register(new ChangeListener());
	}

	public static void init() {
		String comment;

		comment = "The date format used in timestamps.";
		dateFormat = loadString("notes.dateFormat", comment, dateFormat);

		comment = "If false, the system's default text editor will be used to edit notes.";
		useInGameEditor = loadBool("notes.inGameEditor", comment, useInGameEditor);

		comment = "If false, the system's default text viewer will be used to open notes.";
		useInGameViewer = loadBool("notes.inGameViewer", comment, useInGameViewer);

		comment = "The maximum width of a pinned note relative to the screen's width.";
		pinnedWidthScale = loadDouble("notes.pinnedWidthScale", comment, pinnedWidthScale);

		comment = "The maximum percentage of the screen's display height that a pinned note can take up.";
		pinnedHeightScale = loadDouble("notes.pinnedHeightScale", comment, pinnedHeightScale);

		comment = "Whether or not displayed notes will be word wrapped.";
		wrapNote = loadBool("notes.wrapNote", comment, wrapNote);

		if (config.hasChanged()) {
			config.save();
		}
	}

	public static String loadString(String name, String comment, String def) {
		final Property prop = config.get(Configuration.CATEGORY_GENERAL, name, def);
		prop.setComment(comment);

		return prop.getString();
	}

	public static int loadInt(String name, String comment, int def) {
		final Property prop = config.get(Configuration.CATEGORY_GENERAL, name, def);
		prop.setComment(comment);
		int val = prop.getInt(def);
		if (val <= 0) {
			val = def;
			prop.set(def);
		}

		return val;
	}

	public static double loadDouble(String name, String comment, double def) {
		final Property prop = config.get(Configuration.CATEGORY_GENERAL, name, def);
		prop.setComment(comment);
		double val = prop.getDouble(def);
		if (val <= 0) {
			val = def;
			prop.set(def);
		}

		return val;
	}

	public static boolean loadBool(String name, String comment, boolean def) {
		final Property prop = config.get(Configuration.CATEGORY_GENERAL, name, def);
		prop.setComment(comment);
		return prop.getBoolean(def);
	}

	public static class ChangeListener {
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if (eventArgs.getModID().equals(Notes.MODID)) {
				init();
			}
		}
	}

}