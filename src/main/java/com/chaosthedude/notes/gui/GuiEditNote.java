package com.chaosthedude.notes.gui;

import java.io.IOException;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.note.Scope;
import com.chaosthedude.notes.util.StringUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);

		setupTextFields();
		setupButtons();
	}

	@Override
	public void updateScreen() {
		noteTitleField.updateCursorCounter();
		noteTextField.updateScreen();

		insertBiomeButton.enabled = insertChunkButton.enabled = insertCoordsButton.enabled = noteTextField.isFocused();
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button == saveButton) {
				updateNote();
				note.save();
				mc.displayGuiScreen(new GuiDisplayNote(parentScreen, note, Notes.displayWidth));
			} else if (button == insertBiomeButton) {
				insertBiome();
			} else if (button == insertChunkButton) {
				insertChunk();
			} else if (button == insertCoordsButton) {
				insertCoords();
			} else if (button == cancelButton) {
				mc.displayGuiScreen(parentScreen);
			} else if (button == globalButton) {
				if (scope == Scope.GLOBAL) {
					scope = Scope.getCurrentScope();
				} else {
					scope = Scope.GLOBAL;
				}

				globalButton.displayString = I18n.format("notes.global") + (scope == Scope.GLOBAL ? ": " + I18n.format("notes.on") : ": " + I18n.format("notes.off"));
				updateNote();
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(parentScreen);
		} else if (noteTitleField.isFocused()) {
			noteTitleField.textboxKeyTyped(typedChar, keyCode);
		} else if (noteTextField.isFocused()) {
			noteTextField.keyTyped(typedChar, keyCode);
		}

		updateNote();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		noteTitleField.mouseClicked(mouseX, mouseY, mouseButton);
		noteTextField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);

		noteTitleField.mouseReleased(mouseX, mouseY, state);
		noteTextField.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		noteTextField.handleMouseInput();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, guiTitle, width / 2 + 60, 15, 0xffffff);

		noteTitleField.drawTextBox();
		noteTextField.drawScreen(mouseX, mouseY, partialTicks);

		drawCenteredString(fontRendererObj, I18n.format("notes.saveAs", note.getUncollidingSaveName(note.getTitle())), width / 2 + 55, 65, 0x808080);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void setupButtons() {
		buttonList.clear();

		saveButton = addButton(new GuiNotesButton(0, 10, 40, 110, 20, I18n.format("notes.save")));
		globalButton = addButton(new GuiNotesButton(1, 10, 65, 110, 20, I18n.format("notes.global") + ": " + (note.getScope() == Scope.GLOBAL ? I18n.format("notes.on") : I18n.format("notes.off"))));
		insertBiomeButton = addButton(new GuiNotesButton(2, 10, 90, 110, 20, I18n.format("notes.biome")));
		insertChunkButton = addButton(new GuiNotesButton(3, 10, 115, 110, 20, I18n.format("notes.chunk")));
		insertCoordsButton = addButton(new GuiNotesButton(4, 10, 140, 110, 20, I18n.format("notes.coordinates")));
		cancelButton = addButton(new GuiNotesButton(5, 10, height - 30, 110, 20, I18n.format("gui.cancel")));

		insertBiomeButton.enabled = false;
		insertChunkButton.enabled = false;
		insertCoordsButton.enabled = false;
	}

	private void setupTextFields() {
		noteTitleField = new GuiNoteTitleField(9, fontRendererObj, (width + 110) / 2 - (Notes.displayWidth / 2), 40, Notes.displayWidth, 20);
		noteTitleField.setText(note.getTitle());
		noteTitleField.setFocused(true);

		noteTextField = new GuiNoteTextField(fontRendererObj, (width + 110) / 2 - (Notes.displayWidth / 2), 85, Notes.displayWidth, 136, 5);
		noteTextField.setText(note.getFilteredText());
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
