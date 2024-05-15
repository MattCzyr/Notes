package com.chaosthedude.notes.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigHandler {
	
	private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

	public static final Client CLIENT = new Client(CLIENT_BUILDER);
	
	public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();
	
	public static class Client {
		public final ModConfigSpec.ConfigValue<String> dateFormat;
		public final ModConfigSpec.BooleanValue useInGameEditor;
		public final ModConfigSpec.BooleanValue useInGameViewer;
		public final ModConfigSpec.ConfigValue<String> pinnedNotePosition;
		public final ModConfigSpec.DoubleValue pinnedWidthScale;
		public final ModConfigSpec.DoubleValue pinnedHeightScale;
		public final ModConfigSpec.BooleanValue wrapNote;
		
		Client(ModConfigSpec.Builder builder) {
			String desc;
			builder.push("Client");
			
			desc = "The date format used in timestamps. Uses Java SimpleDateFormat conventions.";
			dateFormat = builder.comment(desc).define("dateFormat", "M/d/yy h:mm a");
	
			desc = "Determines whether the in-game editor or the system's default text editor will be used to edit notes. If the system editor is not available, the in-game editor will be used.";
			useInGameEditor = builder.comment(desc).define("useInGameEditor", true);
	
			desc = "Determines whether the in-game viewer or the system's default text viewer will be used to view notes. If the system viewer is not available, the in-game viewer will be used.";
			useInGameViewer = builder.comment(desc).define("useInGameViewer", true);
	
			desc = "The HUD position of a pinned note. Values: top_left, top_right, center_left, center_right, bottom_left, bottom_right";
			pinnedNotePosition = builder.comment(desc).define("pinnedNotePosition", "center_right");
	
			desc = "The maximum width of a pinned note relative to the screen's width.";
			pinnedWidthScale = builder.comment(desc).defineInRange("pinnedWidthScale", 0.2, 0.05, 1.0);
	
			desc = "The maximum percentage of the screen's display height that a pinned note can take up.";
			pinnedHeightScale = builder.comment(desc).defineInRange("pinnedHeightScale", 1.0, 0.05, 1.0);
	
			desc = "Determines whether displayed notes will be word wrapped.";
			wrapNote = builder.comment(desc).define("wrapNote", true);
			
			builder.pop();
		}

	}

}