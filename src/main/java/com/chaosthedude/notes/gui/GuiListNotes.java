package com.chaosthedude.notes.gui;

import java.util.Objects;

import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiListNotes extends ExtendedList<GuiListNotesEntry> {

	private final GuiSelectNote guiNotes;

	public GuiListNotes(GuiSelectNote guiNotes, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
		super(mc, width, height, top, bottom, slotHeight);
		this.guiNotes = guiNotes;
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
	public void render(int par1, int par2, float par3) {
		int i = getScrollbarPosition();
		int k = getRowLeft();
		int l = y0 + 4 - (int) getScrollAmount();
		renderList(k, l, par1, par2, par3);
	}

	@Override
	protected void renderList(int par1, int par2, int par3, int par4, float par5) {
		int i = getItemCount();
		for (int j = 0; j < i; ++j) {
			int k = getRowTop(j);
			int l = getRowBottom(j);
			if (l >= y0 && k <= y1) {
				int j1 = this.itemHeight - 4;
				GuiListNotesEntry e = this.getEntry(j);
				int k1 = getRowWidth();
				if (renderSelection && isSelectedItem(j)) {
					final int insideLeft = x0 + width / 2 - getRowWidth() / 2 + 2;
					RenderUtils.drawRect(insideLeft - 4, k - 4, insideLeft + getRowWidth() + 4, k + itemHeight, 255 / 2 << 24);
				}

				int j2 = this.getRowLeft();
				e.render(j, k, j2, k1, j1, par3, par4, this.isMouseOver((double) par3, (double) par4) && Objects .equals(this.getEntryAtPosition((double) par3, (double) par4), e), par5);
			}
		}

	}

	@Override
	protected void renderBackground() {
		guiNotes.renderBackground();
	}
	
	private int getRowBottom(int p_getRowBottom_1_) {
		return this.getRowTop(p_getRowBottom_1_) + this.itemHeight;
	}

	public void refreshList() {
		clearEntries();
		for (Note note : Note.getCurrentNotes()) {
			addEntry(new GuiListNotesEntry(this, note));
		}
	}

	public void selectNote(GuiListNotesEntry entry) {
		setSelected(entry);
		guiNotes.selectNote(entry);
	}

	public GuiSelectNote getGuiNotes() {
		return guiNotes;
	}

}
