package com.chaosthedude.notes.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
		super(Component.translatable("notes.selectNote"));
		this.prevScreen = prevScreen;
	}

	@Override
	public void init() {
		setupButtons();
		selectionList = new NotesList(this, minecraft, width + 110, height, 40, height - 64, 36);
		addRenderableWidget(selectionList);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		selectionList.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawCenteredString(font, I18n.get("notes.selectNote"), width / 2 + 60, 15, 0xffffff);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		if (selectionList.getSelected() != null) {
			pinButton.setMessage(selectionList.getSelected().isPinned() ? Component.translatable("notes.unpin") : Component.translatable("notes.pin"));
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
		newButton = addRenderableWidget(new NotesButton(10, 40, 110, 20, Component.translatable("notes.new"), (onPress) -> {
			minecraft.setScreen(new EditNoteScreen(SelectNoteScreen.this, null));
		}));
		selectButton = addRenderableWidget(new NotesButton(10, 75, 110, 20, Component.translatable("notes.select"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			if (notesEntry != null) {
				notesEntry.loadNote();
			}
		}));
		editButton = addRenderableWidget(new NotesButton(10, 100, 110, 20, Component.translatable("notes.edit"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			if (notesEntry != null) {
				notesEntry.editNote();
			}
		}));
		copyButton = addRenderableWidget(new NotesButton(10, 125, 110, 20, Component.translatable("notes.copy"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			notesEntry.copyNote();
		}));
		deleteButton = addRenderableWidget(new NotesButton(10, 150, 110, 20, Component.translatable("notes.delete"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			if (notesEntry != null) {
				notesEntry.deleteNote();
			}
		}));
		pinButton = addRenderableWidget(new NotesButton(10, 175, 110, 20, Component.translatable("notes.pin"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			notesEntry.togglePin();
		}));
		cancelButton = addRenderableWidget(new NotesButton(10, height - 30, 110, 20, Component.translatable("gui.cancel"), (onPress) -> {
			minecraft.setScreen(prevScreen);
		}));

		selectButton.active = false;
		deleteButton.active = false;
		editButton.active = false;
		copyButton.active = false;
		pinButton.active = false;
	}

}
