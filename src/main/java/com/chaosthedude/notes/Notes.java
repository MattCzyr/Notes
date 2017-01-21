package com.chaosthedude.notes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.event.RenderTickHandler;
import com.chaosthedude.notes.key.KeybindHandler;
import com.chaosthedude.notes.note.Note;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Notes.MODID, name = Notes.NAME, version = Notes.VERSION, acceptedMinecraftVersions = "[1.7.10]")

public class Notes {

	public static final String MODID = "notes";
	public static final String NAME = "Notes";
	public static final String VERSION = "1.0.0";

	public static final Logger logger = LogManager.getLogger(MODID);

	public static Note pinnedNote;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new KeybindHandler());
		FMLCommonHandler.instance().bus().register(new RenderTickHandler());
		ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());
	}

}
