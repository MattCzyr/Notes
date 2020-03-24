package com.chaosthedude.notes.key;

import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.gui.SelectNoteScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeybindHandler {

	private static KeyBinding openNotes = new KeyBinding("key.openNotes", GLFW.GLFW_KEY_N, "key.category.notes");

	private static final Minecraft mc = Minecraft.getInstance();

	public KeybindHandler() {
		ClientRegistry.registerKeyBinding(openNotes);
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (openNotes.isPressed()) {
			mc.displayGuiScreen(new SelectNoteScreen(mc.currentScreen));
		}
	}

}
