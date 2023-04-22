package com.chaosthedude.notes.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {
	
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static final Client CLIENT = new Client(CLIENT_BUILDER);
	
	public static final ForgeConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();
	
	public static class Client {
		public final ForgeConfigSpec.ConfigValue<String> dateFormat;
		public final ForgeConfigSpec.BooleanValue useInGameEditor;
		public final ForgeConfigSpec.BooleanValue useInGameViewer;
		public final ForgeConfigSpec.ConfigValue<String> pinnedNotePosition;
		public final ForgeConfigSpec.DoubleValue pinnedWidthScale;
		public final ForgeConfigSpec.DoubleValue pinnedHeightScale;
		public final ForgeConfigSpec.BooleanValue wrapNote;
		
		Client(ForgeConfigSpec.Builder builder) {
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