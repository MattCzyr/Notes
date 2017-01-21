package com.chaosthedude.notes.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public abstract class GuiNotesListExtended extends GuiNotesSlot {

	public GuiNotesListExtended(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
		super(mc, width, height, top, bottom, slotHeight);
	}

	@Override
	protected void elementClicked(int index, boolean flag, int mouseX, int mouseY) {
	}

	@Override
	protected boolean isSelected(int index) {
		return false;
	}

	@Override
	protected void drawBackground() {
	}

	@Override
	protected void drawSlot(int index, int x, int y, int slotHeight, Tessellator tessellator, int mouseX, int mouseY) {
		getListEntry(index).drawEntry(index, x, y, getListWidth(), slotHeight, tessellator, mouseX, mouseY, getIndexAtPos(mouseX, mouseY) == index);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (isWithinYBounds(mouseY)) {
			int l = getIndexAtPos(mouseX, mouseY);
			if (l >= 0) {
				int i1 = left + width / 2 - getListWidth() / 2 + 2;
				int j1 = top + 4 - getAmountScrolled() + l * slotHeight + headerPadding;
				int k1 = mouseX - i1;
				int l1 = mouseY - j1;

				if (getListEntry(l).mousePressed(l, mouseX, mouseY, mouseButton, k1, l1)) {
					setVisible(false);
					return true;
				}
			}
		}

		return false;
	}

	public boolean func_148181_b(int mouseX, int mouseY, int mouseButton) {
		for (int i = 0; i < getSize(); i++) {
			int x = left + width / 2 - getListWidth() / 2 + 2;
			int y = top + 4 - getAmountScrolled() + i * slotHeight + headerPadding;
			int k1 = mouseX - x;
			int l1 = mouseY - y;
			getListEntry(i).mouseReleased(i, mouseX, mouseY, mouseButton, k1, l1);
		}

		setVisible(true);
		return false;
	}

	public abstract GuiListNotesEntry getListEntry(int index);

	@SideOnly(Side.CLIENT)
	public interface IGuiListEntry {
		void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, Tessellator tessellator, int mouseX, int mouseY, boolean isSelected);

		boolean mousePressed(int index, int mouseX, int mouseY, int mouseButton, int x, int y);

		void mouseReleased(int index, int mouseX, int mouseY, int mouseButton, int x, int y);
	}
}