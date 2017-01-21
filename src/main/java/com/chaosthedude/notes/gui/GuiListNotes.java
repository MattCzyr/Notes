package com.chaosthedude.notes.gui;

import java.lang.reflect.Field;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.RenderUtils;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public class GuiListNotes extends GuiNotesListExtended {

	private Minecraft mc = Minecraft.getMinecraft();
	private final GuiSelectNote guiNotes;
	private final List<GuiListNotesEntry> entries = Lists.<GuiListNotesEntry> newArrayList();
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
	public GuiListNotesEntry getListEntry(int index) {
		return (GuiListNotesEntry) entries.get(index);
	}

	@Override
	protected int getSize() {
		return entries.size();
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator) {
		guiNotes.drawDefaultBackground();
	}

	@Override
	protected void drawSelectionBox(int insideLeft, int insideTop, int parMouseX, int parMouseY) {
		final int size = getSize();
		final Tessellator tessellator = Tessellator.instance;
		for (int j = 0; j < size; j++) {
			int k = insideTop + j * slotHeight + headerPadding;
			int l = slotHeight - 4;
			if (k <= bottom && k + l >= top) {
				if (isSelected(j)) {
					RenderUtils.drawRect(insideLeft - 4, k - 4, insideLeft + getListWidth() + 4, k + slotHeight, 255 / 2 << 24);
				}

				drawSlot(j, insideLeft, k, l, tessellator, parMouseX, parMouseY);
			}
		}
	}

	public GuiListNotesEntry getSelectedNote() {
		return selectedIndex >= 0 && selectedIndex < getSize() ? getListEntry(selectedIndex) : null;
	}

	public void refreshList() {
		entries.clear();
		for (Note note : Note.getCurrentNotes()) {
			entries.add(new GuiListNotesEntry(this, note));
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
