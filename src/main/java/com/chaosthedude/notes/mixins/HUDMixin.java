package com.chaosthedude.notes.mixins;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class HUDMixin {
		
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "TAIL"))
	private void renderCompassInfo(MatrixStack matrixStack, float tickDelta, CallbackInfo info) {
		if (!client.options.hudHidden && (client.currentScreen == null || client.currentScreen instanceof ChatScreen)) {
			if (Notes.pinnedNote != null && Notes.pinnedNote.isValidScope()) {
				Notes.pinnedNote.update();
	
				final String text = Notes.pinnedNote.getFilteredText();
				final int maxWidth = MathHelper.floor(client.getWindow().getScaledWidth() * NotesConfig.pinnedWidthScale);
				final int maxHeight = MathHelper.floor(client.getWindow().getScaledHeight() * NotesConfig.pinnedHeightScale);
				final int renderWidth = RenderUtils.getSplitStringWidth(text, maxWidth);
				final int renderHeight = RenderUtils.getSplitStringHeight(text, maxHeight);
	
				final int fixedRenderWidth = RenderUtils.getRenderWidth(NotesConfig.pinnedNotePosition, renderWidth);
				final int fixedRenderHeight = RenderUtils.getRenderHeight(NotesConfig.pinnedNotePosition, renderHeight);
	
				final double opacity = client.options.getTextBackgroundOpacity().getValue();
				final int color = (int) (255.0F * opacity);
	
				Screen.fill(matrixStack, fixedRenderWidth - 10, fixedRenderHeight - 5, fixedRenderWidth + renderWidth, fixedRenderHeight + renderHeight + 5, color << 24);
				RenderUtils.renderSplitString(matrixStack, text, fixedRenderWidth - 5, fixedRenderHeight, maxWidth, 0xffffff);
			}
		}
	}

}
