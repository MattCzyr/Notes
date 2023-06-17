package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.note.Scope;
import com.chaosthedude.notes.util.StringUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
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
		super(Text.literal(note != null ? I18n.translate("notes.editNote") : I18n.translate("notes.newNote")));
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
		noteTitleField.tick();
		noteTextField.tick();

		insertBiomeButton.active = insertChunkButton.active = insertCoordsButton.active = noteTextField.isFocused();
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		boolean ret = super.mouseClicked(x, y, button);
		if (setTextFieldFocused) {
			// Change focus back to the text field after clicking the biome, coords, or chunk button
			noteTextField.setFocused(true);
			setFocused(noteTextField);
			setTextFieldFocused = false;
		}
		return ret;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int par2, int par3) {
		boolean ret = super.keyPressed(keyCode, par2, par3);
		updateNote();
		return ret;
	}
	
	@Override
	public boolean keyReleased(int keyCode, int par2, int par3) {
		boolean ret = super.keyReleased(keyCode, par2, par3);
		updateNote();
		return ret;
	}
	
	@Override
	public void setFocused(Element element) {
		super.setFocused(element);
		if (element != noteTextField && noteTextField != null) {
			noteTextField.setFocused(false);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		renderBackground(context);
		context.drawCenteredTextWithShadow(textRenderer, title.getString(), width / 2 + 60, 15, 0xffffff);
		context.drawCenteredTextWithShadow(textRenderer, I18n.translate("notes.saveAs", note.getUncollidingSaveName(note.getTitle())), width / 2 + 55, 65, 0x808080);
		super.render(context, mouseX, mouseY, partialTicks);
	}

	private void setupButtons() {
		saveButton = addDrawableChild(new NotesButton(10, 40, 110, 20, Text.translatable("notes.save"), (onPress) -> {
			updateNote();
			note.save();
			client.setScreen(new DisplayNoteScreen(parentScreen, note));
			if (pinned) {
				Notes.pinnedNote = note;
			}
		}));
		globalButton = addDrawableChild(new NotesButton(10, 65, 110, 20, Text.translatable("notes.global").append(Text.literal(": ").append(note.getScope() == Scope.GLOBAL ? Text.translatable("notes.on") : Text.translatable("notes.off"))), (onPress) -> {
			if (scope == Scope.GLOBAL) {
				scope = Scope.getCurrentScope();
			} else {
				scope = Scope.GLOBAL;
			}

			globalButton.setMessage(Text.literal(I18n.translate("notes.global") + (scope == Scope.GLOBAL ? ": " + I18n.translate("notes.on") : ": " + I18n.translate("notes.off"))));
			updateNote();
		}));
		insertBiomeButton = addDrawableChild(new NotesButton(10, 100, 110, 20, Text.translatable("notes.biome"), (onPress) -> {
			insertBiome();
			setTextFieldFocused = true;
		}));
		insertChunkButton = addDrawableChild(new NotesButton(10, 125, 110, 20, Text.translatable("notes.chunk"), (onPress) -> {
			insertChunk();
			setTextFieldFocused = true;
		}));
		insertCoordsButton = addDrawableChild(new NotesButton(10, 150, 110, 20, Text.translatable("notes.coordinates"), (onPress) -> {
			insertCoords();
			setTextFieldFocused = true;
		}));
		cancelButton = addDrawableChild(new NotesButton(10, height - 30, 110, 20, Text.translatable("gui.cancel"), (onPress) -> {
			client.setScreen(parentScreen);
		}));

		insertBiomeButton.active = false;
		insertChunkButton.active = false;
		insertCoordsButton.active = false;
	}

	private void setupTextFields() {
		noteTitleField = addDrawableChild(new NotesTitleField(textRenderer, 130, 40, width - 140, 20, Text.literal("")));
		noteTitleField.setText(note.getTitle());
		addDrawableChild(noteTitleField);
		noteTitleField.setFocused(true);
		setFocused(noteTitleField);

		noteTextField = addDrawableChild(new NotesTextField(textRenderer, 130, 85, width - 140, height - 95, 5));
		noteTextField.setText(note.getFilteredText());
		addDrawableChild(noteTextField);
	}

	private void updateNote() {
		note.setTitle(noteTitleField.getText());
		note.setText(noteTextField.getText());
		note.setScope(scope);
	}

	private void insertBiome() {
		noteTextField.insert(StringUtils.fixBiomeName(client.world, client.world.getBiome(client.player.getBlockPos()).value()));
	}

	private void insertChunk() {
		noteTextField.insert((int) client.player.getChunkPos().x + ", " + (int) client.player.getChunkPos().z);
	}

	private void insertCoords() {
		noteTextField.insert((int) client.player.getBlockX() + ", " + (int) client.player.getBlockY() + ", " + (int) client.player.getBlockZ());
	}

}
