package com.chaosthedude.notes.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiListNotesEntry implements GuiNotesListExtended.IGuiListEntry {

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
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, Tessellator tessellator, int mouseX, int mouseY, boolean isSelected) {
		mc.fontRenderer.drawString(note.getTitle(), x + 1, y + 1, 0xffffff);
		mc.fontRenderer.drawString(note.getScope().format(), x + 4 + mc.fontRenderer.getStringWidth(note.getTitle()), y + 1, 0x808080);
		mc.fontRenderer.drawString(note.getPreview(MathHelper.floor_double(listWidth * 0.9)), x + 1, y + mc.fontRenderer.FONT_HEIGHT + 3, 0x808080);
		mc.fontRenderer.drawString(note.getLastModifiedString(), x + 1, y + mc.fontRenderer.FONT_HEIGHT + 14, 0x808080);
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
		mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
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
		}, "Delete this note?", note.getTitle(), 0));
	}

}
