package com.chaosthedude.notes.util;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.sun.prism.impl.VertexBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

public class RenderUtils {

	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final FontRenderer fontRenderer = mc.fontRenderer;

	public static void drawSplitStringOnHUD(String str, int x, int y, int wrapWidth) {
		renderSplitString(str, x, y, wrapWidth, true);
	}

	public static void renderSplitString(String string, int x, int y, int wrapWidth, boolean addShadow) {
		for (Object o : fontRenderer.listFormattedStringToWidth(string, wrapWidth)) {
			fontRenderer.drawString((String) o, x, y, 0xffffff, addShadow);
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
		int temp;
		if (left < right) {
			temp = left;
			left = right;
			right = temp;
		}

		if (top < bottom) {
			temp = top;
			top = bottom;
			bottom = temp;
		}

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		final Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(f, f1, f2, f3);

		tessellator.startDrawingQuads();
		tessellator.addVertex((double) left, (double) bottom, 0.0D);
		tessellator.addVertex((double) right, (double) bottom, 0.0D);
		tessellator.addVertex((double) right, (double) top, 0.0D);
		tessellator.addVertex((double) left, (double) top, 0.0D);
		tessellator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

}
