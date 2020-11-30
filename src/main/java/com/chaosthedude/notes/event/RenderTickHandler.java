package com.chaosthedude.notes.event;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.util.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickHandler {

	private static final Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase == Phase.END && !mc.gameSettings.hideGUI && (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen)) {
			if (Notes.pinnedNote != null && Notes.pinnedNote.isValidScope()) {
				Notes.pinnedNote.update();

				final String text = Notes.pinnedNote.getFilteredText();
				final int maxWidth = MathHelper.floor(mc.getMainWindow().getScaledWidth() * ConfigHandler.CLIENT.pinnedWidthScale.get());
				final int maxHeight = MathHelper.floor(mc.getMainWindow().getScaledHeight() * ConfigHandler.CLIENT.pinnedHeightScale.get());
				final int renderWidth = RenderUtils.getSplitStringWidth(text, maxWidth);
				final int renderHeight = RenderUtils.getSplitStringHeight(text, maxWidth);
				final int width = mc.getMainWindow().getScaledWidth() - renderWidth;
				final int height = (mc.getMainWindow().getScaledHeight() / 2) - (renderHeight / 2);

				final int fixedRenderWidth = RenderUtils.getRenderWidth(ConfigHandler.CLIENT.pinnedNotePosition.get(), renderWidth);
				final int fixedRenderHeight = RenderUtils.getRenderHeight(ConfigHandler.CLIENT.pinnedNotePosition.get(), renderHeight);

				final double opacity = mc.gameSettings.chatOpacity * 0.9F + 0.1F;
				final int color = (int) (255.0F * opacity);
				
				final MatrixStack stack = new MatrixStack();

				RenderUtils.drawRect(fixedRenderWidth - 10, fixedRenderHeight - 5, fixedRenderWidth + renderWidth, fixedRenderHeight + renderHeight + 5, color / 2 << 24);
				RenderUtils.renderSplitString(stack, text, fixedRenderWidth - 5, fixedRenderHeight, maxWidth, 0xffffff);
			}
		}
	}

}
