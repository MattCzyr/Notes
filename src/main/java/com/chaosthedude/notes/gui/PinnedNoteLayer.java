package com.chaosthedude.notes.gui;

import java.util.List;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.util.RenderUtils;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PinnedNoteLayer implements LayeredDraw.Layer {
	
	private static final Minecraft CLIENT = Minecraft.getInstance();
	
	@Override
	public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
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
				guiGraphics.fill(renderX - 5, renderY - 5, renderX + renderWidth + 5, renderY + renderHeight + 5, opacity << 24);
				
				// Render note
				RenderUtils.renderSplitString(guiGraphics, lines, renderX, renderY, 0xffffff);
			}
		}
	}

}
