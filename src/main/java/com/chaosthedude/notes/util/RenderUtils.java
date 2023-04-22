package com.chaosthedude.notes.util;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class RenderUtils {

	private static final Minecraft CLIENT = Minecraft.getInstance();
	
	public static List<String> splitStringToWidth(String str, int maxWidth) {
		List<String> splitStrings = new ArrayList<String>();
		for (FormattedText text : CLIENT.font.getSplitter().splitLines(str, maxWidth, Style.EMPTY)) {
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
			height += CLIENT.font.lineHeight;
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

	public static void renderSplitString(PoseStack stack, List<String> splitString, int x, int y, int color) {
		for (String s : splitString) {
			CLIENT.font.drawShadow(stack, s, x, y, color);
			y += CLIENT.font.lineHeight;
		}
	}

	public static int getSplitStringWidth(List<String> splitString) {
		int width = 0;
		for (String line : splitString) {
			final int stringWidth = CLIENT.font.width(line);
			if (stringWidth > width) {
				width = stringWidth;
			}
		}

		return width;
	}

	public static int getSplitStringHeight(List<String> splitString) {
		return CLIENT.font.lineHeight * splitString.size();
	}

	// Returns the X position at which text with the given width should start rendering, assuming padding of 5 on each side
	public static int getPinnedNoteX(String position, int noteWidth) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("center_left") || positionLower.equals("bottom_left")) {
			// Left side
			return 5;
		}

		// Right side
		return CLIENT.getWindow().getGuiScaledWidth() - noteWidth - 5;
	}

	// Returns the Y position at which text with the given height should start rendering, assuming padding of 5 on each side
	public static int getPinnedNoteY(String position, int noteHeight) {
		final String positionLower = position.toLowerCase();
		if (positionLower.equals("top_left") || positionLower.equals("top_right")) {
			// Top
			return 5;
		} else if (positionLower.equals("bottom_left") || positionLower.equals("bottom_right")) {
			// Bottom
			return CLIENT.getWindow().getGuiScaledHeight() - noteHeight - 5;
		}

		// Center
		return (CLIENT.getWindow().getGuiScaledHeight() / 2) - (noteHeight / 2);
	}
	
	// Chops off characters as necessary and adds ellipses (...) to the end
	public static String addEllipses(String str, int maxWidth) {
		return CLIENT.font.plainSubstrByWidth(str, Math.max(0, maxWidth - CLIENT.font.width("..."))) + "...";
	}

}
