package com.chaosthedude.notes.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class NotesConfirmScreen extends ConfirmScreen {
	
	private BooleanConsumer callbackFunction;
	private Component confirmButtonLabel;
	private Component cancelButtonLabel;

	public NotesConfirmScreen(BooleanConsumer callbackFunction, Component confirmButtonLabel, Component cancelButtonLabel) {
		super(callbackFunction, confirmButtonLabel, cancelButtonLabel);
		this.callbackFunction = callbackFunction;
		this.confirmButtonLabel = confirmButtonLabel;
		this.cancelButtonLabel = cancelButtonLabel;
	}

	@Override
	protected void init() {
		super.init();
		clearWidgets();
		addRenderableWidget(new NotesButton(this.width / 2 - 155, height / 6 + 96, 150, 20, confirmButtonLabel, (p_213002_1_) -> {
			callbackFunction.accept(true);
		}));
		addRenderableWidget(new NotesButton(this.width / 2 - 155 + 160, height / 6 + 96, 150, 20, cancelButtonLabel, (p_213001_1_) -> {
			callbackFunction.accept(false);
		}));
	}

}
