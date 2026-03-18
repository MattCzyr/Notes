package com.chaosthedude.notes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class StringUtils {

	public static final char[] FILTER_CHARS = new char[] { '\r', '\f' };
	private static final Minecraft CLIENT = Minecraft.getInstance();
	private static final Font FONT = CLIENT.font;

	public static List<String> wrapToWidth(String str, int wrapWidth) {
		final List<String> strings = new ArrayList<String>();
		String temp = "";
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\n' || FONT.width(temp + String.valueOf(c)) >= wrapWidth) {
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

	public static String filter(String s) {
		String filtered = s.replace(String.valueOf('\t'), "    ");
		for (char c : FILTER_CHARS) {
			filtered = filtered.replace(String.valueOf(c), "");
		}

		return filtered;
	}

	public static String filterFileName(String s) {
		String filtered = s;
		for (char c : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
			filtered = filtered.replace(String.valueOf(c), "~");
		}

		return filtered;
	}

	public static String fixBiomeName(Level level, Biome biome) {
		final Optional<Identifier> optionalID = getKeyForBiome(level, biome);
		if (optionalID.isPresent()) {
			final String original = I18n.get(Util.makeDescriptionId("biome", optionalID.get()));
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
		
		return "";
	}
	
	private static Optional<? extends Registry<Biome>> getBiomeRegistry(Level level) {
		return level.registryAccess().lookup(Registries.BIOME);
	}

	private static Optional<Identifier> getKeyForBiome(Level level, Biome biome) {
		return getBiomeRegistry(level).isPresent() ? Optional.of(getBiomeRegistry(level).get().getKey(biome)) : Optional.empty();
	}

}
