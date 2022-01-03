package com.chaosthedude.notes.key;

import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.gui.SelectNoteScreen;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

public class KeybindHandler {

	private static KeyMapping openNotes = new KeyMapping("key.openNotes", GLFW.GLFW_KEY_N, "key.category.notes");

	private static final Minecraft mc = Minecraft.getInstance();

	public KeybindHandler() {
		ClientRegistry.registerKeyBinding(openNotes);
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (openNotes.isDown()) {
			mc.setScreen(new SelectNoteScreen(mc.screen));
		}
	}

}
