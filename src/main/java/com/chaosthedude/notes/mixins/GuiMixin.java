package com.chaosthedude.notes.mixins;

import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.NotesConfig;
import com.chaosthedude.notes.util.RenderUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public class GuiMixin {
	
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "TAIL"))
	private void renderPinnedNote(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
		if (!minecraft.options.hideGui && (minecraft.screen == null || minecraft.screen instanceof ChatScreen)) {
			if (Notes.pinnedNote != null && Notes.pinnedNote.isValidScope()) {
				final int maxWidth = Mth.floor(minecraft.getWindow().getGuiScaledWidth() * NotesConfig.pinnedWidthScale);
				final int maxHeight = Mth.floor(minecraft.getWindow().getGuiScaledHeight() * NotesConfig.pinnedHeightScale);
	
				final String text = Notes.pinnedNote.getFilteredText();
				final List<String> widthSplitLines = RenderUtils.splitStringToWidth(text, maxWidth);
				final List<String> lines = RenderUtils.splitStringToHeight(widthSplitLines, maxHeight);

				// If any lines were removed, add ellipses
				if (widthSplitLines.size() > lines.size()) {
					String lastLine = lines.get(lines.size() - 1);
					lines.set(lines.size() - 1, RenderUtils.addEllipses(lastLine, maxWidth));
				}

				final int renderWidth = RenderUtils.getSplitStringWidth(lines, maxWidth);
				final int renderHeight = RenderUtils.getSplitStringHeight(lines, maxHeight);

				final int renderX = RenderUtils.getPinnedNoteX(NotesConfig.pinnedNotePosition, renderWidth);
				final int renderY = RenderUtils.getPinnedNoteY(NotesConfig.pinnedNotePosition, renderHeight);
	
				final int opacity = (int) (255.0F * minecraft.options.textBackgroundOpacity().get());
	
				// Render opaque background with padding of 5 on each side
				guiGraphics.fill(renderX - 5, renderY - 5, renderX + renderWidth + 5, renderY + renderHeight + 5, opacity << 24);

				// Render note
				RenderUtils.renderSplitString(guiGraphics, lines, renderX, renderY, 0xffffffff);
			}
		}
	}

}
