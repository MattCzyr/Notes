package com.chaosthedude.notes.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectNote extends GuiScreen {

	private GuiScreen prevScreen;
	private GuiNotesButton newButton;
	private GuiNotesButton selectButton;
	private GuiNotesButton editButton;
	private GuiNotesButton copyButton;
	private GuiNotesButton deleteButton;
	private GuiNotesButton pinButton;
	private GuiNotesButton cancelButton;
	private GuiListNotes selectionList;

	public GuiSelectNote(GuiScreen prevScreen) {
		this.prevScreen = prevScreen;
	}

	@Override
	public void initGui() {
		selectionList = new GuiListNotes(this, mc, width + 110, height, 40, height - 64, 36);
		setupButtons();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		selectionList.handleMouseInput();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			final GuiListNotesEntry notesEntry = selectionList.getSelectedNote();
			if (button == deleteButton) {
				if (notesEntry != null) {
					notesEntry.deleteNote();
				}
			} else if (button == selectButton) {
				if (notesEntry != null) {
					notesEntry.loadNote();
				}
			} else if (button == newButton) {
				mc.displayGuiScreen(new GuiEditNote(this, null));
			} else if (button == editButton) {
				if (notesEntry != null) {
					notesEntry.editNote();
				}
			} else if (button == copyButton) {
				notesEntry.copyNote();
			} else if (button == pinButton) {
				notesEntry.togglePin();
			} else if (button == cancelButton) {
				mc.displayGuiScreen(prevScreen);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		selectionList.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(fontRendererObj, I18n.format("notes.selectNote"), width / 2 + 60, 15, 0xffffff);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void updateScreen() {
		if (selectionList.getSelectedNote() != null) {
			pinButton.displayString = selectionList.getSelectedNote().isPinned() ? I18n.format("notes.unpin") : I18n.format("notes.pin");
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		selectionList.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		selectionList.mouseReleased(mouseX, mouseY, state);
	}

	public void selectNote(GuiListNotesEntry entry) {
		final boolean enable = entry != null;
		selectButton.enabled = enable;
		deleteButton.enabled = enable;
		editButton.enabled = enable;
		copyButton.enabled = enable;
		pinButton.enabled = enable;
	}

	private void setupButtons() {
		buttonList.clear();

		newButton = addButton(new GuiNotesButton(0, 10, 40, 110, 20, I18n.format("notes.new")));
		selectButton = addButton(new GuiNotesButton(1, 10, 65, 110, 20, I18n.format("notes.select")));
		editButton = addButton(new GuiNotesButton(2, 10, 90, 110, 20, I18n.format("notes.edit")));
		copyButton = addButton(new GuiNotesButton(3, 10, 115, 110, 20, I18n.format("notes.copy")));
		deleteButton = addButton(new GuiNotesButton(4, 10, 140, 110, 20, I18n.format("notes.delete")));
		pinButton = addButton(new GuiNotesButton(5, 10, 165, 110, 20, I18n.format("notes.pin")));
		cancelButton = addButton(new GuiNotesButton(6, 10, height - 30, 110, 20, I18n.format("gui.cancel")));

		selectButton.enabled = false;
		deleteButton.enabled = false;
		editButton.enabled = false;
		copyButton.enabled = false;
		pinButton.enabled = false;
	}

}
