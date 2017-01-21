package com.chaosthedude.notes.event;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.util.RenderUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

public class RenderTickHandler {

	private static final Minecraft mc = Minecraft.getMinecraft();

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase == Phase.END && !mc.gameSettings.hideGUI && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat)) {
			if (Notes.pinnedNote != null && Notes.pinnedNote.isValidScope()) {
				Notes.pinnedNote.update();

				final ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
				final String text = Notes.pinnedNote.getFilteredText();
				final int maxWidth = MathHelper.floor_double(res.getScaledWidth() * ConfigHandler.pinnedWidthScale);
				final int maxHeight = MathHelper.floor_double(res.getScaledHeight() * ConfigHandler.pinnedHeightScale);
				final int renderWidth = RenderUtils.getSplitStringWidth(text, maxWidth);
				final int renderHeight = RenderUtils.getSplitStringHeight(text, maxWidth);
				final int width = res.getScaledWidth() - renderWidth;
				final int height = (res.getScaledHeight() / 2) - (renderHeight / 2);

				final float f = mc.gameSettings.chatOpacity * 0.9F + 0.1F;
				final int color = (int) (255.0F * f);

				RenderUtils.drawRect(width - 10, height - 5, res.getScaledWidth(), height + renderHeight + 5, color / 2 << 24);
				RenderUtils.drawSplitStringOnHUD(text, width - 5, height, maxWidth);
			}
		}
	}

}
