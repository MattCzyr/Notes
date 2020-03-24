package com.chaosthedude.notes.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
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
		super(new StringTextComponent(I18n.format("notes.selectNote")));
		this.prevScreen = prevScreen;
	}

	@Override
	public void init() {
		setupButtons();
		selectionList = new NotesList(this, minecraft, width + 110, height, 40, height - 64, 36);
		children.add(selectionList);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		selectionList.render(mouseX, mouseY, partialTicks);
		drawCenteredString(font, I18n.format("notes.selectNote"), width / 2 + 60, 15, 0xffffff);
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		if (selectionList.getSelected() != null) {
			pinButton.setMessage(selectionList.getSelected().isPinned() ? I18n.format("notes.unpin") : I18n.format("notes.pin"));
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
		newButton = addButton(new NotesButton(10, 40, 110, 20, I18n.format("notes.new"), (onPress) -> {
			minecraft.displayGuiScreen(new EditNoteScreen(SelectNoteScreen.this, null));
		}));
		selectButton = addButton(new NotesButton(10, 65, 110, 20, I18n.format("notes.select"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			if (notesEntry != null) {
				notesEntry.loadNote();
			}
		}));
		editButton = addButton(new NotesButton(10, 90, 110, 20, I18n.format("notes.edit"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			if (notesEntry != null) {
				notesEntry.editNote();
			}
		}));
		copyButton = addButton(new NotesButton(10, 115, 110, 20, I18n.format("notes.copy"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			notesEntry.copyNote();
		}));
		deleteButton = addButton(new NotesButton(10, 140, 110, 20, I18n.format("notes.delete"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			if (notesEntry != null) {
				notesEntry.deleteNote();
			}
		}));
		pinButton = addButton(new NotesButton(10, 165, 110, 20, I18n.format("notes.pin"), (onPress) -> {
			NotesListEntry notesEntry = SelectNoteScreen.this.selectionList.getSelected();
			notesEntry.togglePin();
		}));
		cancelButton = addButton(new NotesButton(10, height - 30, 110, 20, I18n.format("gui.cancel"), (onPress) -> {
			minecraft.displayGuiScreen(prevScreen);
		}));

		selectButton.active = false;
		deleteButton.active = false;
		editButton.active = false;
		copyButton.active = false;
		pinButton.active = false;
	}

}
