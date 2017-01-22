package com.chaosthedude.notes.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.biome.Biome;

public final class StringUtils {

	public static final char[] FILTER_CHARS = new char[] { '\r', '\f' };
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final FontRenderer fontRenderer = mc.fontRendererObj;

	public static String insertStringAt(String insert, String insertTo, int pos) {
		return insertTo.substring(0, pos) + insert + insertTo.substring(pos, insertTo.length());
	}

	public static List<String> wrapToWidth(String str, int wrapWidth) {
		final List<String> strings = new ArrayList<String>();
		String temp = "";
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\n' || fontRenderer.getStringWidth(temp + String.valueOf(c)) >= wrapWidth) {
				strings.add(temp);
				temp = "";
			}

			if (c != '\n') {
				temp = temp + String.valueOf(c);
			}
		}

		strings.add(temp);

		return strings;
	}

	public static List<WrappedString> wrapToWidthWithIndication(String str, int wrapWidth) {
		final List<WrappedString> strings = new ArrayList<WrappedString>();
		String temp = "";
		boolean wrapped = false;
		for (int i = 0; i < str.length(); i++) {
			final char c = str.charAt(i);
			if (c == '\n') {
				strings.add(new WrappedString(temp, wrapped));
				temp = "";
				wrapped = false;
			} else if (fontRenderer.getStringWidth(temp + String.valueOf(c)) >= wrapWidth) {
				strings.add(new WrappedString(temp, wrapped));
				temp = "";
				wrapped = true;
			}

			if (c != '\n') {
				temp = temp + String.valueOf(c);
			}
		}

		if (!temp.isEmpty()) {
			strings.add(new WrappedString(temp, wrapped));
		}

		return strings;
	}

	public static String filter(String s) {
		String filtered = s.replace(String.valueOf('\t'), "    ");
		for (char c : FILTER_CHARS) {
			filtered = filtered.replace(String.valueOf(c), "");
		}

		return filtered;
	}

	public static String filterFileName(String s) {
		String filtered = s;
		for (char c : ChatAllowedCharacters.ILLEGAL_FILE_CHARACTERS) {
			filtered = filtered.replace(String.valueOf(c), "~");
		}

		return filtered;
	}

	public static String trimStringNewline(String text) {
		while (text != null && text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}

		return text;
	}

	public static String fixBiomeName(Biome biome) {
		final String original = biome.getBiomeName();
		String fixed = "";
		char pre = ' ';
		for (int i = 0; i < original.length(); i++) {
			final char c = original.charAt(i);
			if (Character.isUpperCase(c) && Character.isLowerCase(pre) && Character.isAlphabetic(pre)) {
				fixed = fixed + " ";
			}
			fixed = fixed + String.valueOf(c);
			pre = c;
		}

		return fixed;
	}

}
