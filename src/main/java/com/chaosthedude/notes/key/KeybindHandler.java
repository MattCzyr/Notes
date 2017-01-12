package com.chaosthedude.notes.key;

import org.lwjgl.input.Keyboard;

import com.chaosthedude.notes.gui.GuiSelectNote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

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
