package com.chaosthedude.notes.util;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtils {

	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final FontRenderer fontRenderer = mc.fontRendererObj;

	public static void drawSplitStringOnHUD(String str, int x, int y, int wrapWidth) {
		renderSplitString(str, x, y, wrapWidth, true);
	}

	public static void renderSplitString(String string, int x, int y, int wrapWidth, boolean addShadow) {
		for (String s : fontRenderer.listFormattedStringToWidth(string, wrapWidth)) {
			fontRenderer.drawString(s, x, y, 0xffffff, addShadow);
			y += fontRenderer.FONT_HEIGHT;
		}
	}

	public static int getSplitStringWidth(String string, int wrapWidth) {
		final List<String> lines = fontRenderer.listFormattedStringToWidth(string, wrapWidth);
		int width = 0;
		for (String line : lines) {
			final int stringWidth = fontRenderer.getStringWidth(line);
			if (stringWidth > width) {
				width = stringWidth;
			}
		}

		return width;
	}

	public static int getSplitStringHeight(String string, int wrapWidth) {
		return fontRenderer.FONT_HEIGHT * fontRenderer.listFormattedStringToWidth(string, wrapWidth).size();
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
		final VertexBuffer buffer = tessellator.getBuffer();

		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(red, green, blue, alpha);

		buffer.begin(7, DefaultVertexFormats.POSITION);
		buffer.pos((double) left, (double) bottom, 0.0D).endVertex();
		buffer.pos((double) right, (double) bottom, 0.0D).endVertex();
		buffer.pos((double) right, (double) top, 0.0D).endVertex();
		buffer.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static int getRenderWidth(String position, int width, ScaledResolution res) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("center_left") || positionLower.equals("bottom_left")) {
			return 10;
		}

		return res.getScaledWidth() - width;
	}

	public static int getRenderHeight(String position, int height, ScaledResolution res) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("top_right")) {
			return 5;
		} else if (positionLower.equals("bottom_left") || positionLower.equals("bottom_right")) {
			return res.getScaledHeight() - height - 5;
		}

		return (res.getScaledHeight() / 2) - (height / 2);
	}

}
