package com.chaosthedude.notes;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.event.RenderTickHandler;
import com.chaosthedude.notes.key.KeybindHandler;
import com.chaosthedude.notes.note.Note;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(Notes.MODID)
public class Notes {

	public static final String MODID = "notes";
	public static final String NAME = "Notes";
	public static final String VERSION = "1.2.3";

	public static final Logger logger = LogManager.getLogger(MODID);

	public static Note pinnedNote;
	
	public Notes() {
		DistExecutor.runWhenOn(Dist.CLIENT, () ->  () -> {
			MinecraftForge.EVENT_BUS.register(new KeybindHandler());
			MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
		});
		
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
	}

}
