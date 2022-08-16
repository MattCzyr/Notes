package com.chaosthedude.notes.util;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class RenderUtils {

	private static final Minecraft mc = Minecraft.getInstance();
	private static final Font font = mc.font;
	
	public static List<String> trimStringToWidth(String str, int maxWidth) {
		List<String> trimmedStrings = new ArrayList<String>();
		for (FormattedText text : font.getSplitter().splitLines(str, maxWidth, Style.EMPTY)) {
			trimmedStrings.add(text.getString());
		}
		return trimmedStrings;
	}

	public static void renderSplitString(PoseStack stack, String string, int x, int y, int wrapWidth, int color) {
		for (String s : trimStringToWidth(string, wrapWidth)) {
			font.drawShadow(stack, s, x, y, color);
			y += font.lineHeight;
		}
	}

	public static int getSplitStringWidth(String string, int wrapWidth) {
		final List<String> lines = trimStringToWidth(string, wrapWidth);
		int width = 0;
		for (String line : lines) {
			final int stringWidth = font.width(line);
			if (stringWidth > width) {
				width = stringWidth;
			}
		}

		return width;
	}

	public static int getSplitStringHeight(String string, int wrapWidth) {
		return font.lineHeight * trimStringToWidth(string, wrapWidth).size();
	}

	public static int getRenderWidth(String position, int width) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("center_left") || positionLower.equals("bottom_left")) {
			return 10;
		}

		return mc.getWindow().getGuiScaledWidth() - width;
	}

	public static int getRenderHeight(String position, int height) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("top_right")) {
			return 5;
		} else if (positionLower.equals("bottom_left") || positionLower.equals("bottom_right")) {
			return mc.getWindow().getGuiScaledHeight() - height - 5;
		}

		return (mc.getWindow().getGuiScaledHeight() / 2) - (height / 2);
	}

}
