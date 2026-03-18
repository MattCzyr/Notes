package com.chaosthedude.notes.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class NotesConfirmScreen extends ConfirmScreen {
	
	public NotesConfirmScreen(BooleanConsumer callbackFunction, Component title, Component subtitle) {
		super(callbackFunction, title, subtitle);
	}
	
	@Override
	protected void addButtons(LinearLayout layout) {
		yesButton = layout.addChild(new NotesButton(this.width / 2 - 155, height / 6 + 96, 150, 20, Component.translatable("gui.yes"), (button) -> {
			callback.accept(true);
		}));
		noButton = layout.addChild(new NotesButton(this.width / 2 - 155 + 160, height / 6 + 96, 150, 20, Component.translatable("gui.no"), (button) -> {
			callback.accept(false);
		}));
	}

}
