package com.chaosthedude.notes.gui;

import javax.annotation.Nullable;

import org.lwjgl.glfw.GLFW;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.note.Scope;
import com.chaosthedude.notes.util.StringUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEditNote extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiNotesButton saveButton;
	private GuiNotesButton globalButton;
	private GuiNotesButton insertBiomeButton;
	private GuiNotesButton insertChunkButton;
	private GuiNotesButton insertCoordsButton;
	private GuiNotesButton cancelButton;
	private GuiNoteTitleField noteTitleField;
	private GuiNoteTextField noteTextField;
	private String saveDirName;
	private Note note;
	private String guiTitle;
	private Scope scope;
	private boolean pinned;

	public GuiEditNote(GuiScreen parentScreen, @Nullable Note note) {
		this.parentScreen = parentScreen;
		if (note != null) {
			this.note = note;
			guiTitle = I18n.format("notes.editNote");
		} else {
			this.note = new Note("New Note", "", Scope.getCurrentScope());
			guiTitle = I18n.format("notes.newNote");
		}

		scope = Scope.getCurrentScope();
		pinned = this.note.isPinned();
	}

	@Override
	public void initGui() {
		mc.keyboardListener.enableRepeatEvents(true);

		setupTextFields();
		setupButtons();
	}

	@Override
	public void tick() {
		noteTitleField.tick();
		noteTextField.tick();

		insertBiomeButton.enabled = insertChunkButton.enabled = insertCoordsButton.enabled = noteTextField.isFocused();
	}

	@Override
	public void onGuiClosed() {
		mc.keyboardListener.enableRepeatEvents(false);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int par2, int par3) {
		super.keyPressed(keyCode, par2, par3);
		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			mc.displayGuiScreen(parentScreen);
			return true;
		} else if (keyCode == GLFW.GLFW_KEY_TAB && noteTitleField.isFocused()) {
			noteTitleField.setFocused(false);
			noteTextField.setFocused(true);
			return true;
		}/* else if (noteTitleField.isFocused()) {
			noteTitleField.keyPressed(keyCode, par2, par3);
			return true;
		} else if (noteTextField.isFocused()) {
			noteTextField.keyTyped(keyCode, par2, par3);
			return true;
		}*/
		
		updateNote();
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, guiTitle, width / 2 + 60, 15, 0xffffff);

		noteTitleField.drawTextBox();
		noteTextField.drawScreen(mouseX, mouseY, partialTicks);

		drawCenteredString(fontRenderer, I18n.format("notes.saveAs", note.getUncollidingSaveName(note.getTitle())), width / 2 + 55, 65, 0x808080);

		super.render(mouseX, mouseY, partialTicks);
	}

	private void setupButtons() {
		//buttonList.clear();

		saveButton = addButton(new GuiNotesButton(0, 10, 40, 110, 20, I18n.format("notes.save")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				updateNote();
				note.save();
				mc.displayGuiScreen(new GuiDisplayNote(parentScreen, note));
				if (pinned) {
					Notes.pinnedNote = note;
				}
			}
		});
		globalButton = addButton(new GuiNotesButton(1, 10, 65, 110, 20, I18n.format("notes.global") + ": " + (note.getScope() == Scope.GLOBAL ? I18n.format("notes.on") : I18n.format("notes.off"))) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				if (scope == Scope.GLOBAL) {
					scope = Scope.getCurrentScope();
				} else {
					scope = Scope.GLOBAL;
				}

				globalButton.displayString = I18n.format("notes.global") + (scope == Scope.GLOBAL ? ": " + I18n.format("notes.on") : ": " + I18n.format("notes.off"));
				updateNote();
			}
		});
		insertBiomeButton = addButton(new GuiNotesButton(2, 10, 90, 110, 20, I18n.format("notes.biome")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				insertBiome();
			}
		});
		insertChunkButton = addButton(new GuiNotesButton(3, 10, 115, 110, 20, I18n.format("notes.chunk")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				insertChunk();
			}
		});
		insertCoordsButton = addButton(new GuiNotesButton(4, 10, 140, 110, 20, I18n.format("notes.coordinates")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				insertCoords();
			}
		});
		cancelButton = addButton(new GuiNotesButton(5, 10, height - 30, 110, 20, I18n.format("gui.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(parentScreen);
			}
		});

		insertBiomeButton.enabled = false;
		insertChunkButton.enabled = false;
		insertCoordsButton.enabled = false;
	}

	private void setupTextFields() {
		noteTitleField = new GuiNoteTitleField(9, fontRenderer, 130, 40, width - 140, 20);
		noteTitleField.setText(note.getTitle());
		noteTitleField.setFocused(true);
		children.add(noteTitleField);

		noteTextField = new GuiNoteTextField(fontRenderer, 130, 85, width - 140, 136, 5);
		noteTextField.setText(note.getFilteredText());
		children.add(noteTextField);
	}

	private void updateNote() {
		note.setTitle(noteTitleField.getText());
		note.setText(noteTextField.getText());
		note.setScope(scope);
	}

	private void insertBiome() {
		noteTextField.insert(StringUtils.fixBiomeName(mc.world.getBiome(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ))));
	}

	private void insertChunk() {
		noteTextField.insert((int) mc.player.chunkCoordX + ", " + (int) mc.player.chunkCoordY + ", " + (int) mc.player.chunkCoordZ);
	}

	private void insertCoords() {
		noteTextField.insert((int) mc.player.posX + ", " + (int) mc.player.posY + ", " + (int) mc.player.posZ);
	}

}
