package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.note.Scope;
import com.chaosthedude.notes.util.StringUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class EditNoteScreen extends Screen {

	private final Screen parentScreen;
	private NotesButton saveButton;
	private NotesButton globalButton;
	private NotesButton insertBiomeButton;
	private NotesButton insertChunkButton;
	private NotesButton insertCoordsButton;
	private NotesButton cancelButton;
	private NotesTitleField noteTitleField;
	private NotesTextField noteTextField;
	private String saveDirName;
	private Note note;
	private Scope scope;
	private boolean pinned;
	private boolean setTextFieldFocused;

	public EditNoteScreen(Screen parentScreen, Note note) {
		super(Component.literal(note != null ? I18n.get("notes.editNote") : I18n.get("notes.newNote")));
		this.parentScreen = parentScreen;
		if (note != null) {
			this.note = note;
		} else {
			this.note = new Note("New Note", "", Scope.getCurrentScope());
		}

		scope = Scope.getCurrentScope();
		pinned = this.note.isPinned();
		setTextFieldFocused = false;
	}

	@Override
	public void init() {
		setupTextFields();
		setupButtons();
	}

	@Override
	public void tick() {
		insertBiomeButton.active = insertChunkButton.active = insertCoordsButton.active = noteTextField.isFocused();
	}
	
	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		boolean ret = super.mouseClicked(event, doubleClick);
		if (setTextFieldFocused) {
			// Change focus back to the text field after clicking the biome, coords, or chunk button
			noteTextField.setFocused(true);
			setFocused(noteTextField);
			setTextFieldFocused = false;
		}
		return ret;
	}
	
	@Override
	public boolean keyPressed(KeyEvent event) {
		boolean ret = super.keyPressed(event);
		updateNote();
		return ret;
	}
	
	@Override
	public void setFocused(GuiEventListener listener) {
		super.setFocused(listener);
		if (listener != noteTextField && noteTextField != null) {
			noteTextField.setFocused(false);
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawCenteredString(font, title.getString(), width / 2 + 60, 15, 0xffffffff);
		guiGraphics.drawCenteredString(font, I18n.get("notes.saveAs", note.getUncollidingSaveName(note.getTitle())), width / 2 + 55, 65, 0xff808080);
	}

	private void setupButtons() {
		saveButton = addRenderableWidget(new NotesButton(10, 40, 110, 20, Component.translatable("notes.save"), (onPress) -> {
			updateNote();
			note.save();
			minecraft.setScreen(new DisplayNoteScreen(parentScreen, note));
			if (pinned) {
				Notes.pinnedNote = note;
			}
		}));
		globalButton = addRenderableWidget(new NotesButton(10, 65, 110, 20, Component.translatable("notes.global").append(Component.literal(": ").append(note.getScope() == Scope.GLOBAL ? Component.translatable("notes.on") : Component.translatable("notes.off"))), (onPress) -> {
			if (scope == Scope.GLOBAL) {
				scope = Scope.getCurrentScope();
			} else {
				scope = Scope.GLOBAL;
			}

			globalButton.setMessage(Component.literal(I18n.get("notes.global") + (scope == Scope.GLOBAL ? ": " + I18n.get("notes.on") : ": " + I18n.get("notes.off"))));
			updateNote();
		}));
		insertBiomeButton = addRenderableWidget(new NotesButton(10, 100, 110, 20, Component.translatable("notes.biome"), (onPress) -> {
			insertBiome();
			setTextFieldFocused = true;
		}));
		insertChunkButton = addRenderableWidget(new NotesButton(10, 125, 110, 20, Component.translatable("notes.chunk"), (onPress) -> {
			insertChunk();
			setTextFieldFocused = true;
		}));
		insertCoordsButton = addRenderableWidget(new NotesButton(10, 150, 110, 20, Component.translatable("notes.coordinates"), (onPress) -> {
			insertCoords();
			setTextFieldFocused = true;
		}));
		cancelButton = addRenderableWidget(new NotesButton(10, height - 30, 110, 20, Component.translatable("gui.cancel"), (onPress) -> {
			minecraft.setScreen(parentScreen);
		}));

		insertBiomeButton.active = false;
		insertChunkButton.active = false;
		insertCoordsButton.active = false;
	}

	private void setupTextFields() {
		noteTitleField = addRenderableWidget(new NotesTitleField(font, 130, 40, width - 140, 20, Component.literal("")));
		noteTitleField.setValue(note.getTitle());
		addRenderableWidget(noteTitleField);
		noteTitleField.setFocused(true);
		setFocused(noteTitleField);

		noteTextField = addRenderableWidget(new NotesTextField(font, 130, 85, width - 140, height - 95, 5));
		noteTextField.setText(note.getFilteredText());
		addRenderableWidget(noteTextField);
	}

	private void updateNote() {
		note.setTitle(noteTitleField.getValue());
		note.setText(noteTextField.getText());
		note.setScope(scope);
	}

	private void insertBiome() {
		noteTextField.insert(StringUtils.fixBiomeName(minecraft.level, minecraft.level.getBiome(minecraft.player.blockPosition()).value()));
	}

	private void insertChunk() {
		noteTextField.insert((int) minecraft.player.chunkPosition().x + ", " + (int) minecraft.player.chunkPosition().z);
	}

	private void insertCoords() {
		noteTextField.insert((int) minecraft.player.getBlockX() + ", " + (int) minecraft.player.getBlockY() + ", " + (int) minecraft.player.getBlockZ());
	}

}
