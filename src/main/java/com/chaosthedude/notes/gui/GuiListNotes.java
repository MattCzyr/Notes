package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiListNotes extends GuiListExtended<GuiListNotesEntry> {

	private final GuiSelectNote guiNotes;
	private int selectedIndex = -1;

	public GuiListNotes(GuiSelectNote guiNotes, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
		super(mc, width, height, top, bottom, slotHeight);
		this.guiNotes = guiNotes;
		refreshList();
	}

	@Override
	protected int getScrollBarX() {
		return super.getScrollBarX() + 20;
	}

	@Override
	public int getListWidth() {
		return super.getListWidth() + 50;
	}

	@Override
	protected boolean isSelected(int slotIndex) {
		return slotIndex == selectedIndex;
	}

	@Override
	public void drawScreen(int parMouseX, int parMouseY, float partialTicks) {
		if (visible) {
			drawBackground();
			int x = getScrollBarX();
			int j = x + 6;
			bindAmountScrolled();
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder buffer = tessellator.getBuffer();
			drawContainerBackground(tessellator);
			final int insideLeft = left + width / 2 - getListWidth() / 2 + 2;
			final int insideTop = top + 4 - (int) amountScrolled;
			if (hasListHeader) {
				drawListHeader(insideLeft, insideTop, tessellator);
			}

			drawSelectionBox(insideLeft, insideTop, parMouseX, parMouseY, partialTicks);
		}
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator) {
		guiNotes.drawDefaultBackground();
	}

	@Override
	protected void drawSelectionBox(int insideLeft, int insideTop, int mouseX, int mouseY, float partialTicks) {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBuffer();

		for (int i = 0; i < getSize(); i++) {
			int k = insideTop + i * slotHeight + headerPadding;
			int l = slotHeight - 4;

			if (k > bottom || k + l < top) {
				updateItemPos(i, insideLeft, k, partialTicks);
			}

			if (showSelectionBox && isSelected(i)) {
				RenderUtils.drawRect(insideLeft - 4, k - 4, insideLeft + getListWidth() + 4, k + slotHeight, 255 / 2 << 24);
			}

			drawSlot(i, insideLeft, k, l, mouseX, mouseY, partialTicks);
		}
	}
	
	public GuiListNotesEntry getListEntry(int index) {
		return getChildren().get(index);
	} 

	public GuiListNotesEntry getSelectedNote() {
		return selectedIndex >= 0 && selectedIndex < getSize() ? getListEntry(selectedIndex) : null;
	}

	public void refreshList() {
		clearEntries();
		for (Note note : Note.getCurrentNotes()) {
			addEntry(new GuiListNotesEntry(this, note));
		}
	}

	public void selectNote(int index) {
		selectedIndex = index;
		guiNotes.selectNote(getSelectedNote());
	}

	public GuiSelectNote getGuiNotes() {
		return guiNotes;
	}

}
