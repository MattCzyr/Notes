package com.chaosthedude.notes.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class NotesListEntry extends ObjectSelectionList.Entry<NotesListEntry> {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private final Minecraft mc;
	private final SelectNoteScreen parentScreen;
	private final Note note;
	private final NotesList notesList;

	public NotesListEntry(NotesList notesList, Note note) {
		this.notesList = notesList;
		this.note = note;
		parentScreen = notesList.getParentScreen();
		mc = Minecraft.getInstance();
	}
	
	@Override
	public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
		guiGraphics.drawString(mc.font, note.getTitle(), getX() + 1, getY() + 1, 0xffffffff);
		guiGraphics.drawString(mc.font, note.getScope().format(), getX() + 4 + mc.font.width(note.getTitle()), getY() + 1, 0xff808080);
		if (note.isPinned()) {
			guiGraphics.drawString(mc.font, I18n.get("notes.pinned"), getX() + 4 + mc.font.width(note.getTitle()) + mc.font.width(note.getScope().format()) + 4, getY() + 1, 0xffffffff);
		}
		guiGraphics.drawString(mc.font, note.getTitle(), getX() + 1, getY() + 1, 0xffffffff);
		guiGraphics.drawString(mc.font, note.getPreview(Mth.floor(notesList.getRowWidth() * 0.9)), getX() + 1, getY() + mc.font.lineHeight + 3, 0xff808080);
		guiGraphics.drawString(mc.font, note.getLastModifiedString(), getX() + 1, getY() + mc.font.lineHeight + 14, 0xff808080);
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		notesList.selectNote(this);
		if (doubleClick) {
			loadNote();
		}
		return true;
	}

	public void editNote() {
		if (ConfigHandler.CLIENT.useInGameEditor.get() || !note.tryOpenExternal()) {
			mc.setScreen(new EditNoteScreen(parentScreen, note));
		}
	}

	public void copyNote() {
		note.copy();
		notesList.refreshList();
	}

	public void loadNote() {
		mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if (ConfigHandler.CLIENT.useInGameViewer.get() || !note.tryOpenExternal()) {
			mc.setScreen(new DisplayNoteScreen(parentScreen, note));
		}
	}

	public void togglePin() {
		if (isPinned()) {
			Notes.pinnedNote = null;
		} else {
			Notes.pinnedNote = note;
			mc.setScreen(null);
		}
	}

	public boolean isPinned() {
		return note.equals(Notes.pinnedNote);
	}
	
	public Note getNote() {
		return note;
	}

	public void deleteNote() {
		mc.setScreen(new NotesConfirmScreen((result) -> {
			if (result) {
				note.delete();
			}

			NotesListEntry.this.mc.setScreen(NotesListEntry.this.parentScreen);
		}, Component.translatable("notes.confirmDelete"), Component.literal(note.getTitle())));
	}

	@Override
	public Component getNarration() {
		return Component.literal(note.getTitle());
	}

}
