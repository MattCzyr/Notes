package com.chaosthedude.notes.util;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class RenderUtils {

	private static final Minecraft CLIENT = Minecraft.getInstance();
	
	public static List<String> trimStringToWidth(String str, int maxWidth) {
		List<String> trimmedStrings = new ArrayList<String>();
		for (FormattedText text : CLIENT.font.getSplitter().splitLines(str, maxWidth, Style.EMPTY)) {
			trimmedStrings.add(text.getString());
		}
		return trimmedStrings;
	}

	public static void renderSplitString(PoseStack stack, String string, int x, int y, int wrapWidth, int color) {
		for (String s : trimStringToWidth(string, wrapWidth)) {
			CLIENT.font.drawShadow(stack, s, x, y, color);
			y += CLIENT.font.lineHeight;
		}
	}
	
	public static void renderSplitStringWithMaxHeight(PoseStack stack, String string, int x, int y, int wrapWidth, int maxHeight, int color) {
		for (String s : trimStringToWidth(string, wrapWidth)) {
			CLIENT.font.drawShadow(stack, s, x, y, color);
			y += CLIENT.font.lineHeight;
		}
	}

	public static int getSplitStringWidth(String string, int wrapWidth) {
		final List<String> lines = trimStringToWidth(string, wrapWidth);
		int width = 0;
		for (String line : lines) {
			final int stringWidth = CLIENT.font.width(line);
			if (stringWidth > width) {
				width = stringWidth;
			}
		}

		return width;
	}

	public static int getSplitStringHeight(String string, int wrapWidth) {
		return CLIENT.font.lineHeight * trimStringToWidth(string, wrapWidth).size();
	}

	public static int getRenderWidth(String position, int width) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("center_left") || positionLower.equals("bottom_left")) {
			return 10;
		}

		return CLIENT.getWindow().getGuiScaledWidth() - width;
	}

	public static int getRenderHeight(String position, int height) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("top_right")) {
			return 5;
		} else if (positionLower.equals("bottom_left") || positionLower.equals("bottom_right")) {
			return CLIENT.getWindow().getGuiScaledHeight() - height - 5;
		}

		return (CLIENT.getWindow().getGuiScaledHeight() / 2) - (height / 2);
	}

}
