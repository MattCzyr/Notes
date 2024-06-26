package com.chaosthedude.notes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.event.NotesEvents;
import com.chaosthedude.notes.note.Note;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Notes.MODID)
public class Notes {

	public static final String MODID = "notes";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static Note pinnedNote;
	
	public Notes(ModContainer modContainer) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			modContainer.getEventBus().addListener(NotesEvents::registerKeybinds);
			modContainer.getEventBus().addListener(NotesEvents::registerOverlay);
			NeoForge.EVENT_BUS.addListener(NotesEvents::onClientTick);
			modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
		}
	}

}
