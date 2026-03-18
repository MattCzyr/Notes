package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.util.RenderUtils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

public class NotesTextField extends MultiLineEditBox {

	NotesTextField(Font font, int x, int y, int width, int height) {
		super(font, x, y, width, height, Component.empty(), Component.empty(), 0xffffffff, true, 0xffffffff, true, true);
	}

	@Override
	protected void renderBackground(GuiGraphics guiGraphics) {
		int fillColor = RenderUtils.getBackgroundColor(isActive(), false);
		guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), fillColor);
	}
	
	@Override
	protected void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (scrollbarVisible()) {
        	int backgroundFillColor = RenderUtils.getBackgroundColor(false, false);
			int scrollbarFillColor = RenderUtils.getBackgroundColor(true, true);
			guiGraphics.fill(scrollBarX(), getY(), scrollBarX() + 6, getBottom(), backgroundFillColor);
            guiGraphics.fill(scrollBarX(), scrollBarY(), scrollBarX() + 6, scrollBarY() + scrollerHeight(), scrollbarFillColor);
        }
    }
	
	public void insertText(String text) {
		textField.insertText(text);
	}

}
