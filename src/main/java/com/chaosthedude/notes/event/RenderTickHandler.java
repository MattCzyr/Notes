package com.chaosthedude.notes.event;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickHandler {

	private static final Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase == Phase.END && !mc.options.hideGui && (mc.screen == null || mc.screen instanceof ChatScreen)) {
			if (Notes.pinnedNote != null && Notes.pinnedNote.isValidScope()) {
				Notes.pinnedNote.update();

				final String text = Notes.pinnedNote.getFilteredText();
				final int maxWidth = Mth.floor(mc.getWindow().getGuiScaledWidth() * ConfigHandler.CLIENT.pinnedWidthScale.get());
				final int maxHeight = Mth.floor(mc.getWindow().getGuiScaledHeight() * ConfigHandler.CLIENT.pinnedHeightScale.get());
				final int renderWidth = RenderUtils.getSplitStringWidth(text, maxWidth);
				final int renderHeight = RenderUtils.getSplitStringHeight(text, maxWidth);
				final int width = mc.getWindow().getGuiScaledWidth() - renderWidth;
				final int height = (mc.getWindow().getGuiScaledHeight() / 2) - (renderHeight / 2);

				final int fixedRenderWidth = RenderUtils.getRenderWidth(ConfigHandler.CLIENT.pinnedNotePosition.get(), renderWidth);
				final int fixedRenderHeight = RenderUtils.getRenderHeight(ConfigHandler.CLIENT.pinnedNotePosition.get(), renderHeight);

				final double opacity = mc.options.chatOpacity().get() * 0.9F + 0.1F;
				final int color = (int) (255.0F * opacity);
				
				final PoseStack stack = new PoseStack();

				GuiComponent.fill(stack, fixedRenderWidth - 10, fixedRenderHeight - 5, fixedRenderWidth + renderWidth, fixedRenderHeight + renderHeight + 5, color / 2 << 24);
				RenderUtils.renderSplitString(stack, text, fixedRenderWidth - 5, fixedRenderHeight, maxWidth, 0xffffff);
			}
		}
	}

}
