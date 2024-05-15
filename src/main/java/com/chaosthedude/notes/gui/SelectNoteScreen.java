package com.chaosthedude.notes.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SelectNoteScreen extends Screen {

	private Screen prevScreen;
	private NotesButton newButton;
	private NotesButton selectButton;
	private NotesButton editButton;
	private NotesButton copyButton;
	private NotesButton deleteButton;
	private NotesButton pinButton;
	private NotesButton cancelButton;
	private NotesList selectionList;

	public SelectNoteScreen(Screen prevScreen) {
		super(Text.translatable("notes.selectNote"));
		this.prevScreen = prevScreen;
	}

	@Override
	public void init() {
		setupButtons();
		selectionList = new NotesList(this, client, width + 110, height, 40, 36);
		addDrawableChild(selectionList);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		super.render(context, mouseX, mouseY, partialTicks);
		context.drawCenteredTextWithShadow(textRenderer, I18n.translate("notes.selectNote"), width / 2 + 60, 15, 0xffffff);
	}

	@Override
	public void tick() {
		if (selectionList.getSelectedOrNull() != null) {
			pinButton.setMessage(selectionList.getSelectedOrNull().isPinned() ? Text.translatable("notes.unpin") : Text.translatable("notes.pin"));
		}
	}

	public void selectNote(NotesListEntry entry) {
		final boolean enable = entry != null;
		selectButton.active = enable;
		deleteButton.active = enable;
		editButton.active = enable;
		copyButton.active = enable;
		pinButton.active = enable;
	}

	private void setupButtons() {
		newButton = addDrawableChild(new NotesButton(10, 40, 110, 20, Text.translatable("notes.new"), (onPress) -> {
			client.setScreen(new EditNoteScreen(SelectNoteScreen.this, null));
		}));
		selectButton = addDrawableChild(new NotesButton(10, 75, 110, 20, Text.translatable("notes.select"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelectedOrNull();
			if (notesEntry != null) {
				notesEntry.loadNote();
			}
		}));
		editButton = addDrawableChild(new NotesButton(10, 100, 110, 20, Text.translatable("notes.edit"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelectedOrNull();
			if (notesEntry != null) {
				notesEntry.editNote();
			}
		}));
		copyButton = addDrawableChild(new NotesButton(10, 125, 110, 20, Text.translatable("notes.copy"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelectedOrNull();
			notesEntry.copyNote();
		}));
		deleteButton = addDrawableChild(new NotesButton(10, 150, 110, 20, Text.translatable("notes.delete"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelectedOrNull();
			if (notesEntry != null) {
				notesEntry.deleteNote();
			}
		}));
		pinButton = addDrawableChild(new NotesButton(10, 175, 110, 20, Text.translatable("notes.pin"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelectedOrNull();
			notesEntry.togglePin();
		}));
		cancelButton = addDrawableChild(new NotesButton(10, height - 30, 110, 20, Text.translatable("gui.cancel"), (onPress) -> {
			client.setScreen(prevScreen);
		}));

		selectButton.active = false;
		deleteButton.active = false;
		editButton.active = false;
		copyButton.active = false;
		pinButton.active = false;
	}

}
