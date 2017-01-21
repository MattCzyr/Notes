package com.chaosthedude.notes.key;

import org.lwjgl.input.Keyboard;

import com.chaosthedude.notes.gui.GuiSelectNote;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class KeybindHandler {

	private static KeyBinding openNotes = new KeyBinding("key.openNotes", Keyboard.KEY_N, "key.category.notes");

	private static final Minecraft mc = Minecraft.getMinecraft();

	public KeybindHandler() {
		ClientRegistry.registerKeyBinding(openNotes);
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (openNotes.isPressed()) {
			mc.displayGuiScreen(new GuiSelectNote(mc.currentScreen));
		}
	}

}
