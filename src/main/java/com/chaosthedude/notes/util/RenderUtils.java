package com.chaosthedude.notes.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

public class RenderUtils {

	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static List<String> splitStringToWidth(String str, int maxWidth) {
		List<String> splitStrings = new ArrayList<String>();
		for (StringVisitable text : CLIENT.textRenderer.getTextHandler().wrapLines(str, maxWidth, Style.EMPTY)) {
			splitStrings.add(text.getString());
		}
		return splitStrings;
	}

	// Intermediary so we can check if any lines were removed
	public static List<String> splitStringToHeight(List<String> widthSplitStrings, int maxHeight) {
		List<String> splitStrings = new ArrayList<String>();
		int height = 0;
		for (String line : widthSplitStrings) {
			// Check if we have room for this line before adding it
			height += CLIENT.textRenderer.fontHeight;
			if (height > maxHeight) {
				break;
			}
			splitStrings.add(line);
		}
		return splitStrings;
	}

	public static List<String> splitStringToWidthAndHeight(String str, int maxWidth, int maxHeight) {
		return splitStringToHeight(splitStringToWidth(str, maxWidth), maxHeight);
	}

	public static void renderSplitString(MatrixStack stack, List<String> splitString, int x, int y, int color) {
		for (String s : splitString) {
			CLIENT.textRenderer.drawWithShadow(stack, s, x, y, color);
			y += CLIENT.textRenderer.fontHeight;
		}
	}

	public static int getSplitStringWidth(List<String> splitString, int wrapWidth) {
		int width = 0;
		for (String line : splitString) {
			final int stringWidth = CLIENT.textRenderer.getWidth(line);
			if (stringWidth > width) {
				width = stringWidth;
			}
		}

		return width;
	}

	public static int getSplitStringHeight(List<String> splitString, int wrapWidth) {
		return CLIENT.textRenderer.fontHeight * splitString.size();
	}

	// Returns the X position at which text with the given width should start rendering, assuming padding of 5 on each side
	public static int getPinnedNoteX(String position, int noteWidth) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("center_left")
				|| positionLower.equals("bottom_left")) {
			// Left side
			return 5;
		}

		// Right side
		return CLIENT.getWindow().getScaledWidth() - noteWidth - 5;
	}

	// Returns the Y position at which text with the given height should start rendering, assuming padding of 5 on each side
	public static int getPinnedNoteY(String position, int noteHeight) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("top_right")) {
			// Top
			return 5;
		} else if (positionLower.equals("bottom_left") || positionLower.equals("bottom_right")) {
			// Bottom
			return CLIENT.getWindow().getScaledHeight() - noteHeight - 5;
		}

		// Center
		return (CLIENT.getWindow().getScaledHeight() / 2) - (noteHeight / 2);
	}

	// Chops off characters as necessary and adds ellipses (...) to the end
	public static String addEllipses(String str, int maxWidth) {
		return CLIENT.textRenderer.trimToWidth(str, Math.max(0, maxWidth - CLIENT.textRenderer.getWidth("..."))) + "...";
	}

}
