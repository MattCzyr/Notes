package com.chaosthedude.notes.util;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtils {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final FontRenderer font = mc.fontRenderer;

	public static void drawSplitStringOnHUD(String str, int x, int y, int wrapWidth) {
		renderSplitString(str, x, y, wrapWidth);
	}

	public static void renderSplitString(String string, int x, int y, int wrapWidth) {
		for (String s : font.listFormattedStringToWidth(string, wrapWidth)) {
			font.drawStringWithShadow(s, x, y, 0xffffff);
			y += font.FONT_HEIGHT;
		}
	}

	public static int getSplitStringWidth(String string, int wrapWidth) {
		final List<String> lines = font.listFormattedStringToWidth(string, wrapWidth);
		int width = 0;
		for (String line : lines) {
			final int stringWidth = font.getStringWidth(line);
			if (stringWidth > width) {
				width = stringWidth;
			}
		}

		return width;
	}

	public static int getSplitStringHeight(String string, int wrapWidth) {
		return font.FONT_HEIGHT * font.listFormattedStringToWidth(string, wrapWidth).size();
	}

	public static void drawRect(int left, int top, int right, int bottom, int color) {
		if (left < right) {
			int temp = left;
			left = right;
			right = temp;
		}

		if (top < bottom) {
			int temp = top;
			top = bottom;
			bottom = temp;
		}

		final float red = (float) (color >> 16 & 255) / 255.0F;
		final float green = (float) (color >> 8 & 255) / 255.0F;
		final float blue = (float) (color & 255) / 255.0F;
		final float alpha = (float) (color >> 24 & 255) / 255.0F;

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();

		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.color4f(red, green, blue, alpha);

		buffer.begin(7, DefaultVertexFormats.POSITION);
		buffer.pos((double) left, (double) bottom, 0.0D).endVertex();
		buffer.pos((double) right, (double) bottom, 0.0D).endVertex();
		buffer.pos((double) right, (double) top, 0.0D).endVertex();
		buffer.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public static int getRenderWidth(String position, int width) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("center_left") || positionLower.equals("bottom_left")) {
			return 10;
		}

		return mc.getMainWindow().getScaledWidth() - width;
	}

	public static int getRenderHeight(String position, int height) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("top_right")) {
			return 5;
		} else if (positionLower.equals("bottom_left") || positionLower.equals("bottom_right")) {
			return mc.getMainWindow().getScaledHeight() - height - 5;
		}

		return (mc.getMainWindow().getScaledHeight() / 2) - (height / 2);
	}

}
