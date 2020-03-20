package com.chaosthedude.notes.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
		setupButtons();
		selectionList = new GuiListNotes(this, mc, width + 110, height, 40, height - 64, 36);
		children.add(selectionList);
	}
	
	@Override
	public boolean mouseScrolled(double amount) {
		super.mouseScrolled(amount);
		return selectionList.mouseScrolled(amount);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		selectionList.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(fontRenderer, I18n.format("notes.selectNote"), width / 2 + 60, 15, 0xffffff);
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		if (selectionList.getSelectedNote() != null) {
			pinButton.displayString = selectionList.getSelectedNote().isPinned() ? I18n.format("notes.unpin") : I18n.format("notes.pin");
		}
	}

	/*@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		selectionList.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		selectionList.mouseReleased(mouseX, mouseY, state);
	}*/

	public void selectNote(GuiListNotesEntry entry) {
		final boolean enable = entry != null;
		selectButton.enabled = enable;
		deleteButton.enabled = enable;
		editButton.enabled = enable;
		copyButton.enabled = enable;
		pinButton.enabled = enable;
	}

	private void setupButtons() {
		newButton = addButton(new GuiNotesButton(0, 10, 40, 110, 20, I18n.format("notes.new")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiEditNote(GuiSelectNote.this, null));
			}
		});
		selectButton = addButton(new GuiNotesButton(1, 10, 65, 110, 20, I18n.format("notes.select")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				GuiListNotesEntry notesEntry = GuiSelectNote.this.selectionList.getSelectedNote();
				if (notesEntry != null) {
					notesEntry.loadNote();
				}
			}
		});
		editButton = addButton(new GuiNotesButton(2, 10, 90, 110, 20, I18n.format("notes.edit")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				GuiListNotesEntry notesEntry = GuiSelectNote.this.selectionList.getSelectedNote();
				if (notesEntry != null) {
					notesEntry.editNote();
				}
			}
		});
		copyButton = addButton(new GuiNotesButton(3, 10, 115, 110, 20, I18n.format("notes.copy")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				GuiListNotesEntry notesEntry = GuiSelectNote.this.selectionList.getSelectedNote();
				notesEntry.copyNote();
			}
		});
		deleteButton = addButton(new GuiNotesButton(4, 10, 140, 110, 20, I18n.format("notes.delete")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				GuiListNotesEntry notesEntry = GuiSelectNote.this.selectionList.getSelectedNote();
				if (notesEntry != null) {
					notesEntry.deleteNote();
				}
			}
		});
		pinButton = addButton(new GuiNotesButton(5, 10, 165, 110, 20, I18n.format("notes.pin")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				GuiListNotesEntry notesEntry = GuiSelectNote.this.selectionList.getSelectedNote();
				notesEntry.togglePin();
			}
		});
		cancelButton = addButton(new GuiNotesButton(6, 10, height - 30, 110, 20, I18n.format("gui.cancel")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(prevScreen);
			}
		});

		selectButton.enabled = false;
		deleteButton.enabled = false;
		editButton.enabled = false;
		copyButton.enabled = false;
		pinButton.enabled = false;
	}

}
