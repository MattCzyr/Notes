package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class NotesListEntry extends ObjectSelectionList.Entry<NotesListEntry> {

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
	public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
		Component titleComponent = Component.literal(note.getTitle());
		Component scopeComponent = Component.literal(" (").withColor(0xff808080).append(Component.translatable(note.getScope().getUnlocalizedName()).withColor(0xff808080).append(Component.literal(") ").withColor(0xff808080)));
		Component pinnedComponent = note.isPinned() ? Component.translatable("notes.pinned") : Component.empty();
		
		guiGraphics.text(mc.font, Component.empty().append(titleComponent).append(scopeComponent).append(pinnedComponent), getX() + 5, getY() + 5, 0xffffffff);
		guiGraphics.text(mc.font, note.getPreview(notesList.getRowWidth() - 10), getX() + 5, getY() + 5 + (mc.font.lineHeight + 2), 0xff808080);
		guiGraphics.text(mc.font, note.getLastModifiedString(), getX() + 5, getY() + 5 + ((mc.font.lineHeight + 2) * 2), 0xff808080);
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
			mc.setScreen(new ViewNoteScreen(parentScreen, note));
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
