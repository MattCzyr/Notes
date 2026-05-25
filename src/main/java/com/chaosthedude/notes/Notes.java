package com.chaosthedude.notes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.config.NotesConfig;
import com.chaosthedude.notes.gui.SelectNoteScreen;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.FileUtils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Notes implements ClientModInitializer {

	public static final String MODID = "notes";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static Note pinnedNote;
	private static KeyMapping openNotes;

	@Override
	public void onInitializeClient() {
		NotesConfig.load();

		openNotes = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.openNotes", GLFW.GLFW_KEY_N, new KeyMapping.Category(Identifier.fromNamespaceAndPath(MODID, "keys"))));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openNotes.isDown()) {
				client.setScreen(new SelectNoteScreen(client.screen));
			}
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			loadPinnedNote();
		});
	}

	public static void savePinnedNote() {
		File stateFile = new File(FileUtils.getRootSaveDirectory(), "pinned.txt");
		try {
			if (pinnedNote == null) {
				stateFile.delete();
			} else {
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(stateFile))) {
					writer.write(pinnedNote.getSaveFile().getCanonicalPath());
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to save pinned note state", e);
		}
	}

	public static void loadPinnedNote() {
		pinnedNote = null;
		File stateFile = new File(FileUtils.getRootSaveDirectory(), "pinned.txt");
		if (!stateFile.exists()) return;
		try (BufferedReader reader = new BufferedReader(new FileReader(stateFile))) {
			String path = reader.readLine();
			if (path != null && !path.trim().isEmpty()) {
				File noteFile = new File(path.trim());
				if (noteFile.exists()) {
					pinnedNote = new Note(noteFile);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load pinned note state", e);
		}
	}

}