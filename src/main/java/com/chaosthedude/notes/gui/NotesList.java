package com.chaosthedude.notes.gui;

import java.util.Objects;

import com.chaosthedude.notes.note.Note;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class NotesList extends EntryListWidget<NotesListEntry> {

	private final SelectNoteScreen parentScreen;
	private boolean pseudoRenderSelection = true;

	public NotesList(SelectNoteScreen notesScreen, MinecraftClient mc, int width, int height, int top, int bottom, int slotHeight) {
		super(mc, width, height, top, bottom, slotHeight);
		this.parentScreen = notesScreen;
		refreshList();
	}
	
	@Override
	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}

	@Override
	protected boolean isSelectedEntry(int index) {
		return index >= 0 && index < children().size() ? children().get(index).equals(getSelectedOrNull()) : false;
	}
	
	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		renderList(stack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderList(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		int i = getEntryCount();
		for (int j = 0; j < i; ++j) {
			int k = getRowTop(j);
			int l = getRowBottom(j);
			if (l >= top && k <= bottom) {
				int j1 = this.itemHeight - 4;
				NotesListEntry e = this.getEntry(j);
				int k1 = getRowWidth();
				if (pseudoRenderSelection && isSelectedEntry(j)) {
					final int insideLeft = left + width / 2 - getRowWidth() / 2 + 2;
					Screen.fill(stack, insideLeft - 4, k - 4, insideLeft + getRowWidth() + 4, k + itemHeight, 255 / 2 << 24);
				}

				int j2 = this.getRowLeft();
				e.render(stack, j, k, j2, k1, j1, mouseX, mouseY, isMouseOver((double) mouseX, (double) mouseY) && Objects .equals(getEntryAtPosition((double) mouseX, (double) mouseY), e), partialTicks);
			}
		}
	}
	
	@Override
	public void setRenderSelection(boolean value) {
		super.setRenderSelection(value);
		pseudoRenderSelection = value;
	}

	@Override
	protected void renderBackground(MatrixStack stack) {
		parentScreen.renderBackground(stack);
	}
	
	protected int getRowBottom(int index) {
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

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		
	}

}
