package com.chaosthedude.notes.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiListNotesEntry extends GuiListExtended.IGuiListEntry<GuiListNotesEntry> {

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
		mc = Minecraft.getInstance();
	}

	@Override
	public void drawEntry(int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
		mc.fontRenderer.drawString(note.getTitle(), getX() + 1, getY() + 1, 0xffffff);
		mc.fontRenderer.drawString(note.getScope().format(), getX() + 4 + mc.fontRenderer.getStringWidth(note.getTitle()), getY() + 1, 0x808080);
		if (note.isPinned()) {
			mc.fontRenderer.drawString(I18n.format("notes.pinned"), getX() + 4 + mc.fontRenderer.getStringWidth(note.getTitle()) + mc.fontRenderer.getStringWidth(note.getScope().format()) + 4, getY() + 1, 0xffffff);
		}
		mc.fontRenderer.drawString(note.getTitle(), getX() + 1, getY() + 1, 0xffffff);
		mc.fontRenderer.drawString(note.getPreview(MathHelper.floor(listWidth * 0.9)), getX() + 1, getY() + mc.fontRenderer.FONT_HEIGHT + 3, 0x808080);
		mc.fontRenderer.drawString(note.getLastModifiedString(), getX() + 1, getY() + mc.fontRenderer.FONT_HEIGHT + 14, 0x808080);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		notesList.selectNote(getIndex());
		if (Util.milliTime() - lastClickTime < 250L) {
			loadNote();
			return true;
		}

		lastClickTime = Util.milliTime();
		return false;
	}

	public void editNote() {
		if (ConfigHandler.CLIENT.useInGameEditor.get() || !note.tryOpenExternal()) {
			mc.displayGuiScreen(new GuiEditNote(guiNotes, note));
		}
	}

	public void copyNote() {
		note.copy();
		notesList.refreshList();
	}

	public void loadNote() {
		mc.getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if (ConfigHandler.CLIENT.useInGameViewer.get() || !note.tryOpenExternal()) {
			mc.displayGuiScreen(new GuiDisplayNote(guiNotes, note));
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
			public void confirmResult(boolean result, int id) {
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
