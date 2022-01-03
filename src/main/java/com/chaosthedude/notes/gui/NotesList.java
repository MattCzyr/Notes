package com.chaosthedude.notes.gui;

import java.util.Objects;

import com.chaosthedude.notes.note.Note;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NotesList extends ObjectSelectionList<NotesListEntry> {

	private final SelectNoteScreen parentScreen;
	private boolean pseudoRenderSelection = true;

	public NotesList(SelectNoteScreen notesScreen, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
		super(mc, width, height, top, bottom, slotHeight);
		this.parentScreen = notesScreen;
		refreshList();
	}
	
	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
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
	public void render(PoseStack stack, int par1, int par2, float par3) {
		int i = getScrollbarPosition();
		int k = getRowLeft();
		int l = y0 + 4 - (int) getScrollAmount();
		renderList(stack, k, l, par1, par2, par3);
	}

	@Override
	protected void renderList(PoseStack stack, int par1, int par2, int par3, int par4, float par5) {
		int i = getItemCount();
		for (int j = 0; j < i; ++j) {
			int k = getRowTop(j);
			int l = getRowBottom(j);
			if (l >= y0 && k <= y1) {
				int j1 = this.itemHeight - 4;
				NotesListEntry e = this.getEntry(j);
				int k1 = getRowWidth();
				if (pseudoRenderSelection && isSelectedItem(j)) {
					final int insideLeft = x0 + width / 2 - getRowWidth() / 2 + 2;
					GuiComponent.fill(stack, insideLeft - 4, k - 4, insideLeft + getRowWidth() + 4, k + itemHeight, 255 / 2 << 24);
				}

				int j2 = this.getRowLeft();
				e.render(stack, j, k, j2, k1, j1, par3, par4, this.isMouseOver((double) par3, (double) par4) && Objects .equals(this.getEntryAtPosition((double) par3, (double) par4), e), par5);
			}
		}
	}
	
	@Override
	public void setRenderSelection(boolean value) {
		super.setRenderSelection(value);
		pseudoRenderSelection = value;
	}

	@Override
	protected void renderBackground(PoseStack stack) {
		parentScreen.renderBackground(stack);
	}
	
	private int getRowBottom(int index) {
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
