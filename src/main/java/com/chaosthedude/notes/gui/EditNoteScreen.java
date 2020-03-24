package com.chaosthedude.notes.gui;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.note.Scope;
import com.chaosthedude.notes.util.StringUtils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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

	public EditNoteScreen(Screen parentScreen, @Nullable Note note) {
		super(new StringTextComponent(note != null ? I18n.format("notes.editNote") : I18n.format("notes.newNote")));
		this.parentScreen = parentScreen;
		if (note != null) {
			this.note = note;
		} else {
			this.note = new Note("New Note", "", Scope.getCurrentScope());
		}

		scope = Scope.getCurrentScope();
		pinned = this.note.isPinned();
	}

	@Override
	public void init() {
		minecraft.keyboardListener.enableRepeatEvents(true);

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
	public void onClose() {
		minecraft.keyboardListener.enableRepeatEvents(false);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int par2, int par3) {
		super.keyPressed(keyCode, par2, par3);
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			minecraft.displayGuiScreen(parentScreen);
			return true;
		} else if (keyCode == GLFW.GLFW_KEY_TAB && noteTitleField.isFocused()) {
			noteTitleField.setFocused(false);
			noteTextField.setFocused(true);
			return true;
		}
		
		updateNote();
		return false;
	}
	
	@Override
	public boolean keyReleased(int keyCode, int par2, int par3) {
		updateNote();
		return super.keyReleased(keyCode, par2, par3);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		drawCenteredString(font, title.getFormattedText(), width / 2 + 60, 15, 0xffffff);

		noteTitleField.drawTextBox();
		noteTextField.render(mouseX, mouseY, partialTicks);

		drawCenteredString(font, I18n.format("notes.saveAs", note.getUncollidingSaveName(note.getTitle())), width / 2 + 55, 65, 0x808080);

		super.render(mouseX, mouseY, partialTicks);
	}

	private void setupButtons() {
		saveButton = addButton(new NotesButton(10, 40, 110, 20, I18n.format("notes.save"), (onPress) -> {
			updateNote();
			note.save();
			minecraft.displayGuiScreen(new DisplayNoteScreen(parentScreen, note));
			if (pinned) {
				Notes.pinnedNote = note;
			}
		}));
		globalButton = addButton(new NotesButton(10, 65, 110, 20, I18n.format("notes.global") + ": " + (note.getScope() == Scope.GLOBAL ? I18n.format("notes.on") : I18n.format("notes.off")), (onPress) -> {
			if (scope == Scope.GLOBAL) {
				scope = Scope.getCurrentScope();
			} else {
				scope = Scope.GLOBAL;
			}

			globalButton.setMessage(I18n.format("notes.global") + (scope == Scope.GLOBAL ? ": " + I18n.format("notes.on") : ": " + I18n.format("notes.off")));
			updateNote();
		}));
		insertBiomeButton = addButton(new NotesButton(10, 90, 110, 20, I18n.format("notes.biome"), (onPress) -> {
			insertBiome();
		}));
		insertChunkButton = addButton(new NotesButton(10, 115, 110, 20, I18n.format("notes.chunk"), (onPress) -> {
			insertChunk();
		}));
		insertCoordsButton = addButton(new NotesButton(10, 140, 110, 20, I18n.format("notes.coordinates"), (onPress) -> {
			insertCoords();
		}));
		cancelButton = addButton(new NotesButton(10, height - 30, 110, 20, I18n.format("gui.cancel"), (onPress) -> {
			minecraft.displayGuiScreen(parentScreen);
		}));

		insertBiomeButton.active = false;
		insertChunkButton.active = false;
		insertCoordsButton.active = false;
	}

	private void setupTextFields() {
		noteTitleField = new NotesTitleField(9, font, 130, 40, width - 140, 20);
		noteTitleField.setText(note.getTitle());
		noteTitleField.setFocused(true);
		children.add(noteTitleField);

		noteTextField = new NotesTextField(font, 130, 85, width - 140, height - 95, 5);
		noteTextField.setText(note.getFilteredText());
		children.add(noteTextField);
	}

	private void updateNote() {
		note.setTitle(noteTitleField.getText());
		note.setText(noteTextField.getText());
		note.setScope(scope);
	}

	private void insertBiome() {
		noteTextField.insert(StringUtils.fixBiomeName(minecraft.world.getBiome(new BlockPos(minecraft.player.getPosX(), minecraft.player.getPosY(), minecraft.player.getPosZ()))));
	}

	private void insertChunk() {
		noteTextField.insert((int) minecraft.player.chunkCoordX + ", " + (int) minecraft.player.chunkCoordY + ", " + (int) minecraft.player.chunkCoordZ);
	}

	private void insertCoords() {
		noteTextField.insert((int) minecraft.player.getPosX() + ", " + (int) minecraft.player.getPosY() + ", " + (int) minecraft.player.getPosZ());
	}

}
