package com.chaosthedude.notes;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.notes.config.NotesConfig;
import com.chaosthedude.notes.gui.SelectNoteScreen;
import com.chaosthedude.notes.note.Note;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Notes implements ClientModInitializer {

	public static final String MODID = "notes";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static Note pinnedNote;

	private static KeyMapping openNotes;

	private static final int KEY_N = 17; // InputConstants keyboard key code for 'n'

	@Override
	public void onInitializeClient() {
		NotesConfig.load();
		
		openNotes = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.openNotes", KEY_N, new KeyMapping.Category(Identifier.fromNamespaceAndPath(MODID, "keys"))));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
		    while (openNotes.isDown()) {
		    	client.gui.setScreen(new SelectNoteScreen(client.gui.screen()));
		    }
		});
	}

}
