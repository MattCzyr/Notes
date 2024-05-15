package com.chaosthedude.notes.event;

import java.lang.reflect.Field;

import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.gui.PinnedNoteLayer;
import com.chaosthedude.notes.gui.SelectNoteScreen;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mod.EventBusSubscriber(modid = Notes.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NotesEvents {
	
	private static final Minecraft CLIENT = Minecraft.getInstance();
	
	private static final Field LAYERS = ObfuscationReflectionHelper.findField(Gui.class, "layers");
	
	public  static final KeyMapping OPEN_NOTES = new KeyMapping("key.openNotes", GLFW.GLFW_KEY_N, "key.category.notes");
	
	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event) {
		Minecraft mc = Minecraft.getInstance();
		try {
			LayeredDraw layers = (LayeredDraw) LAYERS.get(mc.gui);
			layers.add(new PinnedNoteLayer());
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to add Notes GUI layer");
		}
	}
	
	@SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(OPEN_NOTES);
    }
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (OPEN_NOTES.isDown()) {
			CLIENT.setScreen(new SelectNoteScreen(CLIENT.screen));
		}
	}

}
