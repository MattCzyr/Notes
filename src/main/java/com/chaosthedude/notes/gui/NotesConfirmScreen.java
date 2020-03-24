package com.chaosthedude.notes.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.util.text.ITextComponent;

public class NotesConfirmScreen extends ConfirmScreen {

	public NotesConfirmScreen(BooleanConsumer consumer, ITextComponent par2, ITextComponent par3) {
		super(consumer, par2, par3);
	}

	@Override
	protected void init() {
		super.init();
		buttons.clear();
		addButton(new NotesButton(this.width / 2 - 155, height / 6 + 96, 150, 20, confirmButtonText, (p_213002_1_) -> {
			callbackFunction.accept(true);
		}));
		addButton(new NotesButton(this.width / 2 - 155 + 160, height / 6 + 96, 150, 20, cancelButtonText, (p_213001_1_) -> {
			callbackFunction.accept(false);
		}));
	}

}
