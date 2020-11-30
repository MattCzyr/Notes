package com.chaosthedude.notes.gui;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.note.Scope;
import com.chaosthedude.notes.util.StringUtils;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean ret = false;
		for(IGuiEventListener listener : getEventListeners()) {
			if (listener.mouseClicked(mouseX, mouseY, button)) {
				setListener(listener);
				if (button == 0) {
					setDragging(true);
				}
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(stack);
		drawCenteredString(stack, font, title.getString(), width / 2 + 60, 15, 0xffffff);

		noteTitleField.render(stack, mouseX, mouseY, partialTicks);
		noteTextField.render(stack, mouseX, mouseY, partialTicks);

		drawCenteredString(stack, font, I18n.format("notes.saveAs", note.getUncollidingSaveName(note.getTitle())), width / 2 + 55, 65, 0x808080);

		super.render(stack, mouseX, mouseY, partialTicks);
	}

	private void setupButtons() {
		saveButton = addButton(new NotesButton(10, 40, 110, 20, new TranslationTextComponent("notes.save"), (onPress) -> {
			updateNote();
			note.save();
			minecraft.displayGuiScreen(new DisplayNoteScreen(parentScreen, note));
			if (pinned) {
				Notes.pinnedNote = note;
			}
		}));
		globalButton = addButton(new NotesButton(10, 65, 110, 20, new StringTextComponent(I18n.format("notes.global") + ": " + (note.getScope() == Scope.GLOBAL ? I18n.format("notes.on") : I18n.format("notes.off"))), (onPress) -> {
			if (scope == Scope.GLOBAL) {
				scope = Scope.getCurrentScope();
			} else {
				scope = Scope.GLOBAL;
			}

			globalButton.setMessage(new StringTextComponent(I18n.format("notes.global") + (scope == Scope.GLOBAL ? ": " + I18n.format("notes.on") : ": " + I18n.format("notes.off"))));
			updateNote();
		}));
		insertBiomeButton = addButton(new NotesButton(10, 90, 110, 20, new TranslationTextComponent("notes.biome"), (onPress) -> {
			insertBiome();
		}));
		insertChunkButton = addButton(new NotesButton(10, 115, 110, 20, new TranslationTextComponent("notes.chunk"), (onPress) -> {
			insertChunk();
		}));
		insertCoordsButton = addButton(new NotesButton(10, 140, 110, 20, new TranslationTextComponent("notes.coordinates"), (onPress) -> {
			insertCoords();
		}));
		cancelButton = addButton(new NotesButton(10, height - 30, 110, 20, new TranslationTextComponent("gui.cancel"), (onPress) -> {
			minecraft.displayGuiScreen(parentScreen);
		}));

		insertBiomeButton.active = false;
		insertChunkButton.active = false;
		insertCoordsButton.active = false;
	}

	private void setupTextFields() {
		noteTitleField = new NotesTitleField(font, 130, 40, width - 140, 20, new StringTextComponent(""));
		noteTitleField.setText(note.getTitle());
		children.add(noteTitleField);
		noteTitleField.changeFocus(true);
		noteTitleField.setFocused(true);
		setListener(noteTitleField);

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
		noteTextField.insert(StringUtils.fixBiomeName(minecraft.world, minecraft.world.getBiome(minecraft.player.getPosition())));
	}

	private void insertChunk() {
		noteTextField.insert((int) minecraft.player.chunkCoordX + ", " + (int) minecraft.player.chunkCoordY + ", " + (int) minecraft.player.chunkCoordZ);
	}

	private void insertCoords() {
		noteTextField.insert((int) minecraft.player.getPosX() + ", " + (int) minecraft.player.getPosY() + ", " + (int) minecraft.player.getPosZ());
	}

}
