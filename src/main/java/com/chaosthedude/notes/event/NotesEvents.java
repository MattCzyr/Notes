package com.chaosthedude.notes.event;

import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.gui.PinnedNoteLayer;
import com.chaosthedude.notes.gui.SelectNoteScreen;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class NotesEvents {
	
	private static final Minecraft CLIENT = Minecraft.getInstance();
	
	public  static final KeyMapping OPEN_NOTES = new KeyMapping("key.openNotes", GLFW.GLFW_KEY_N, "key.category.notes");

	public static void registerOverlay(RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.BOSS_OVERLAY, ResourceLocation.fromNamespaceAndPath(Notes.MODID, "pinned_note"), new PinnedNoteLayer());
	}

    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(OPEN_NOTES);
    }

	public static void onClientTick(ClientTickEvent.Pre event) {
		if (OPEN_NOTES.isDown()) {
			CLIENT.setScreen(new SelectNoteScreen(CLIENT.screen));
		}
	}

}
