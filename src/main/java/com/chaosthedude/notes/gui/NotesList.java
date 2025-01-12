package com.chaosthedude.notes.gui;

import java.util.Objects;

import com.chaosthedude.notes.note.Note;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NotesList extends ObjectSelectionList<NotesListEntry> {

	private final SelectNoteScreen parentScreen;

	public NotesList(SelectNoteScreen notesScreen, Minecraft mc, int width, int height, int top, int bottom) {
		super(mc, width, height, top, bottom);
		this.parentScreen = notesScreen;
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
	protected boolean isSelectedItem(int slotIndex) {
		return slotIndex >= 0 && slotIndex < children().size() ? children().get(slotIndex).equals(getSelected()) : false;
	}
	
	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		guiGraphics.fill(getRowLeft() - 4, getY(), getRowLeft() + getRowWidth() + 4, getY() + getHeight() + 4, 255 / 2 << 24);
		
		enableScissor(guiGraphics);
		int i = getItemCount();
		for (int j = 0; j < i; ++j) {
			int k = getRowTop(j);
			int l = getRowBottom(j);
			if (l >= getY() && k <= getBottom()) {
				int j1 = this.itemHeight - 4;
				NotesListEntry e = this.getEntry(j);
				int k1 = getRowWidth();
				if (isSelectedItem(j)) {
					final int insideLeft = getX() + width / 2 - getRowWidth() / 2 + 2;
					guiGraphics.fill(insideLeft - 4, k - 4, insideLeft + getRowWidth() + 4, k + itemHeight, 255 / 2 << 24);
				}

				int j2 = this.getRowLeft();
				e.render(guiGraphics, j, k, j2, k1, j1, mouseX, mouseY, this.isMouseOver((double) mouseX, (double) mouseY) && Objects.equals(getEntryAtPosition((double) mouseX, (double) mouseY), e), partialTicks);
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
