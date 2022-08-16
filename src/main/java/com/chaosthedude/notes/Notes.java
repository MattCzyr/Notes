package com.chaosthedude.notes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.config.NotesConfig;
import com.chaosthedude.notes.gui.SelectNoteScreen;
import com.chaosthedude.notes.note.Note;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

public class Notes implements ClientModInitializer {

	public static final String MODID = "notes";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static Note pinnedNote;

	private static KeyBinding openNotes;

	@Override
	public void onInitializeClient() {
		NotesConfig.load();
		
		openNotes = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.openNotes", GLFW.GLFW_KEY_N, "key.category.notes"));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
		    while (openNotes.wasPressed()) {
		    	client.setScreen(new SelectNoteScreen(client.currentScreen));
		    }
		});
	}

}
