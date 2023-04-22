package com.chaosthedude.notes.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.chaosthedude.notes.Notes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class NotesConfig {
	
	private static Path configFilePath;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public static String dateFormat = "M/d/yy h:mm a";
	public static boolean useInGameEditor = true;
	public static boolean useInGameViewer = true;
	public static String pinnedNotePosition = "center_right";
	public static double pinnedWidthScale = 0.2;
	public static double pinnedHeightScale = 1.0;
	public static boolean wrapNote = true;
	
	public static void load() {
		Reader reader;
		if(getFilePath().toFile().exists()) {
			try {
				reader = Files.newBufferedReader(getFilePath());
				
				Data data = gson.fromJson(reader, Data.class);
				
				dateFormat = data.dateFormat;
				useInGameEditor = data.useInGameEditor;
				useInGameViewer = data.useInGameViewer;
				pinnedNotePosition = data.pinnedNotePosition;
				pinnedWidthScale = data.pinnedWidthScale;
				pinnedHeightScale = data.pinnedHeightScale;
				wrapNote = data.wrapNote;
				
				reader.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		save();
	}
	
	public static void save() {
		try {
			Writer writer = Files.newBufferedWriter(getFilePath());
			Data data = new Data(dateFormat, useInGameEditor, useInGameViewer, pinnedNotePosition, pinnedWidthScale, pinnedHeightScale, wrapNote);
			gson.toJson(data, writer);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Path getFilePath() {
		if(configFilePath == null) {
			configFilePath = FabricLoader.getInstance().getConfigDir().resolve(Notes.MODID + ".json");
		}
		return configFilePath;
	}
	
	private static class Data {
		private final String dateFormatComment = "The date format used in timestamps. Uses Java SimpleDateFormat conventions.";
		private final String dateFormat;

		private final String useInGameEditorComment = "Determines whether the in-game editor or the system's default text editor will be used to edit notes. If the system editor is not available, the in-game editor will be used.";
		private final boolean useInGameEditor;

		private final String useInGameViewerComment = "Determines whether the in-game viewer or the system's default text viewer will be used to view notes. If the system viewer is not available, the in-game viewer will be used.";
		private final boolean useInGameViewer;

		private final String pinnedNotePositionComment = "The HUD position of a pinned note. Values: top_left, top_right, center_left, center_right, bottom_left, bottom_right";
		private final String pinnedNotePosition;

		private final String pinnedWidthScaleComment = "The maximum width of a pinned note relative to the screen's width.";
		private final double pinnedWidthScale;

		private final String pinnedHeightScaleComment = "The maximum percentage of the screen's display height that a pinned note can take up.";
		private final double pinnedHeightScale;

		private final String wrapNoteComment = "Determines whether displayed notes will be word wrapped.";
		private final boolean wrapNote;
		
		private Data() {
			dateFormat = "M/d/yy h:mm a";
			useInGameEditor = true;
			useInGameViewer = true;
			pinnedNotePosition = "center_right";
			pinnedWidthScale = 0.2;
			pinnedHeightScale = 1.0;
			wrapNote = true;
		}
		
		private Data(String dateFormat, boolean useInGameEditor, boolean useInGameViewer, String pinnedNotePosition, double pinnedWidthScale, double pinnedHeightScale, boolean wrapNote) {
			this.dateFormat = dateFormat;
			this.useInGameEditor = useInGameEditor;
			this.useInGameViewer = useInGameViewer;
			this.pinnedNotePosition = pinnedNotePosition;
			this.pinnedWidthScale = pinnedWidthScale;
			this.pinnedHeightScale = pinnedHeightScale;
			this.wrapNote = wrapNote;
		}
	}

}