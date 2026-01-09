package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.note.Note;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class NotesList extends ObjectSelectionList<NotesListEntry> {

	private final SelectNoteScreen parentScreen;
	private int itemHeight;

	public NotesList(SelectNoteScreen notesScreen, Minecraft mc, int width, int height, int y, int itemHeight) {
		super(mc, width, height, y, itemHeight);
		this.parentScreen = notesScreen;
		this.itemHeight = itemHeight;
		refreshList();
	}
	
	@Override
	protected int scrollBarX() {
		return super.scrollBarX() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}
	
	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		guiGraphics.fill(getRowLeft() - 4, getY(), getRowLeft() + getRowWidth() + 4, getY() + getHeight() + 4, 255 / 2 << 24);
		
		enableScissor(guiGraphics);
		for (int i = 0; i < getItemCount(); ++i) {
			if (getRowBottom(i) >= getY() && getRowTop(i) <= getBottom()) {
				NotesListEntry e = children().get(i);
				if (e == getSelected()) {
					final int insideLeft = getX() + width / 2 - getRowWidth() / 2 + 2;
					guiGraphics.fill(insideLeft - 4, getRowTop(i) - 4, insideLeft + getRowWidth() + 4, getRowTop(i) + itemHeight, 255 / 2 << 24);
				}
				e.renderContent(guiGraphics, mouseX, mouseY, e == getHovered(), partialTicks);
			}
		}
		guiGraphics.disableScissor();
	}
	
	@Override
	protected void enableScissor(GuiGraphics guiGraphics) {
		guiGraphics.enableScissor(getX(), getY(), getRight(), getBottom());
	}
	
	@Override
	public int getRowBottom(int index) {
		return getRowTop(index) + itemHeight;
	}

	public void refreshList() {
		clearEntries();
		for (Note note : Note.getCurrentNotes()) {
			addEntry(new NotesListEntry(this, note));
		}
	}

	public void selectNote(NotesListEntry entry) {
		setSelected(entry);
		parentScreen.selectNote(entry);
	}

	public SelectNoteScreen getParentScreen() {
		return parentScreen;
	}

}
