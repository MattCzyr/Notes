package com.chaosthedude.notes.event;

import java.util.List;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.gui.SelectNoteScreen;
import com.chaosthedude.notes.util.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Notes.MODID, value = Dist.CLIENT)
public class TickHandler {

	private static final Minecraft CLIENT = Minecraft.getInstance();
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (KeybindHandler.OPEN_NOTES.isDown()) {
			CLIENT.setScreen(new SelectNoteScreen(CLIENT.screen));
		}
	}

	@SubscribeEvent
	public void onRenderTick(RenderGuiEvent.Post event) {
		if (!CLIENT.options.hideGui && (CLIENT.screen == null || CLIENT.screen instanceof ChatScreen)) {
			if (Notes.pinnedNote != null && Notes.pinnedNote.isValidScope()) {
				Notes.pinnedNote.update();

				final int maxWidth = Mth.floor(CLIENT.getWindow().getGuiScaledWidth() * ConfigHandler.CLIENT.pinnedWidthScale.get());
				final int maxHeight = Mth.floor(CLIENT.getWindow().getGuiScaledHeight() * ConfigHandler.CLIENT.pinnedHeightScale.get());
				
				final String text = Notes.pinnedNote.getFilteredText();
				final List<String> widthSplitLines = RenderUtils.splitStringToWidth(text, maxWidth);
				final List<String> lines = RenderUtils.splitStringToHeight(widthSplitLines, maxHeight);
				
				// If any lines were removed, add ellipses
				if (widthSplitLines.size() > lines.size()) {
					String lastLine = lines.get(lines.size() - 1);
					lines.set(lines.size() - 1, RenderUtils.addEllipses(lastLine, maxWidth));
				}
				
				final int renderWidth = RenderUtils.getSplitStringWidth(lines);
				final int renderHeight = RenderUtils.getSplitStringHeight(lines);
				
				final int renderX = RenderUtils.getPinnedNoteX(ConfigHandler.CLIENT.pinnedNotePosition.get(), renderWidth);
				final int renderY = RenderUtils.getPinnedNoteY(ConfigHandler.CLIENT.pinnedNotePosition.get(), renderHeight);

				final int opacity = (int) (255.0F * CLIENT.options.textBackgroundOpacity().get());

				// Render opaque background with padding of 5 on each side
				event.getGuiGraphics().fill(renderX - 5, renderY - 5, renderX + renderWidth + 5, renderY + renderHeight + 5, opacity << 24);
				
				// Render note
				RenderUtils.renderSplitString(event.getGuiGraphics(), lines, renderX, renderY, 0xffffff);
			}
		}
	}

}
