package com.chaosthedude.notes.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiListNotesEntry implements GuiListExtended.IGuiListEntry {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private final Minecraft mc;
	private final GuiSelectNote guiNotes;
	private final Note note;
	private final GuiListNotes notesList;
	private long lastClickTime;

	public GuiListNotesEntry(GuiListNotes notesList, Note note) {
		this.notesList = notesList;
		this.note = note;
		guiNotes = notesList.getGuiNotes();
		mc = Minecraft.getMinecraft();
	}

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
		mc.fontRenderer.drawString(note.getTitle(), x + 1, y + 1, 0xffffff);
		mc.fontRenderer.drawString(note.getScope().format(), x + 4 + mc.fontRenderer.getStringWidth(note.getTitle()), y + 1, 0x808080);
		mc.fontRenderer.drawString(note.getPreview(MathHelper.floor(listWidth * 0.9)), x + 1, y + mc.fontRenderer.FONT_HEIGHT + 3, 0x808080);
		mc.fontRenderer.drawString(note.getLastModifiedString(), x + 1, y + mc.fontRenderer.FONT_HEIGHT + 14, 0x808080);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
		notesList.selectNote(slotIndex);
		if (Minecraft.getSystemTime() - lastClickTime < 250L) {
			loadNote();
			return true;
		}

		lastClickTime = Minecraft.getSystemTime();
		return false;
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
	}

	@Override
	public void updatePosition(int par1, int par2, int par3, float par4) {
	}

	public void editNote() {
		if (ConfigHandler.useInGameEditor) {
			mc.displayGuiScreen(new GuiEditNote(guiNotes, note));
		} else {
			note.openExternal();
		}
	}

	public void copyNote() {
		note.copy();
		notesList.refreshList();
	}

	public void loadNote() {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if (ConfigHandler.useInGameViewer) {
			mc.displayGuiScreen(new GuiDisplayNote(guiNotes, note));
		} else {
			note.openExternal();
		}
	}

	public void togglePin() {
		if (isPinned()) {
			Notes.pinnedNote = null;
		} else {
			Notes.pinnedNote = note;
			mc.displayGuiScreen(null);
		}
	}

	public boolean isPinned() {
		return note.equals(Notes.pinnedNote);
	}

	public void deleteNote() {
		mc.displayGuiScreen(new GuiNotesYesNo(new GuiYesNoCallback() {
			@Override
			public void confirmClicked(boolean result, int id) {
				if (result) {
					GuiListNotesEntry.this.mc.displayGuiScreen(new GuiScreenWorking());
					note.delete();
					GuiListNotesEntry.this.notesList.refreshList();
				}

				GuiListNotesEntry.this.mc.displayGuiScreen(GuiListNotesEntry.this.guiNotes);
			}
		}, I18n.format("notes.confirmDelete"), note.getTitle(), 0));
	}

}
