package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class NotesList extends ObjectSelectionList<NotesListEntry> {

	private final SelectNoteScreen parentScreen;

	public NotesList(SelectNoteScreen notesScreen, Minecraft mc, int x, int y, int width, int height, int itemHeight) {
		super(mc, width, height, y, itemHeight);
		this.parentScreen = notesScreen;
        setX(x);
		refreshList();
	}
	
	@Override
	protected int scrollBarX() {
		return getRowLeft() + getRowWidth();
	}

	@Override
	public int getRowWidth() {
		return getWidth();
	}
	
	@Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        enableScissor(guiGraphics);
        extractListBackground(guiGraphics);
        extractListItems(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.disableScissor();
        extractScrollbar(guiGraphics, mouseX, mouseY);
    }
	
	@Override
	protected void extractListBackground(GuiGraphicsExtractor guiGraphics) {
		for (int i = 0; i < getItemCount(); ++i) {
			if (getRowBottom(i) >= getY() && getRowTop(i) <= getBottom()) {
				NotesListEntry entry = children().get(i);
				int fillColor = RenderUtils.getBackgroundColor(true, entry == getSelected());
				guiGraphics.fill(getRowLeft(), getRowTop(i), getRowLeft() + getRowWidth(), getRowTop(i) + defaultEntryHeight, fillColor);
			}
		}
	}
	
	@Override
	protected void extractSelection(GuiGraphicsExtractor guiGraphics, NotesListEntry entry, int backgroundColor) {
		// Selection is rendered in renderListBackground()
	}
	
	@Override
	protected void extractScrollbar(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
		if (scrollable()) {
			int backgroundFillColor = RenderUtils.getBackgroundColor(false, false);
			int scrollbarFillColor = RenderUtils.getBackgroundColor(true, true);
			guiGraphics.fill(scrollBarX(), getY(), scrollBarX() + 6, getBottom(), backgroundFillColor);
			guiGraphics.fill(scrollBarX(), scrollBarY(), scrollBarX() + 6, scrollBarY() + scrollerHeight(), scrollbarFillColor);
		}
	}

	public void refreshList() {
		clearEntries();
		for (Note note : Note.getCurrentNotes()) {
			NotesListEntry entry = new NotesListEntry(this, note);
			if (note.isPinned()) {
				addEntryToTop(entry);
			} else {
				addEntry(entry);
			}
		}
		setScrollAmount(0);
	}

	public void selectNote(NotesListEntry entry) {
		setSelected(entry);
		parentScreen.selectNote(entry);
	}

	public SelectNoteScreen getParentScreen() {
		return parentScreen;
	}

}
