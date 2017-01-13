package com.chaosthedude.notes.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.StringUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDisplayNote extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiNotesButton doneButton;
	private GuiNotesButton pinButton;
	private GuiNotesButton editButton;
	private GuiNotesButton deleteButton;
	private GuiNotesButton prevButton;
	private GuiNotesButton nextButton;
	private Note note;
	private int page;
	private List<String> pages;

	public GuiDisplayNote(GuiScreen parentScreen, Note note) {
		this.parentScreen = parentScreen;
		this.note = note;

		page = 0;
		pages = new ArrayList<String>();
		pages.add("");
	}

	@Override
	public void initGui() {
		setupButtons();
		setupPages();
	}

	@Override
	public void updateScreen() {
		prevButton.enabled = page > 0;
		nextButton.enabled = page < pages.size() - 1;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button == editButton) {
				mc.displayGuiScreen(new GuiEditNote(parentScreen, note));
			} else if (button == deleteButton) {
				deleteNote();
			} else if (button == pinButton) {
				togglePin();
				if (isPinned()) {
					mc.displayGuiScreen(null);
				}
			} else if (button == doneButton) {
				mc.displayGuiScreen(parentScreen);
			} else if (button == prevButton) {
				if (page > 0) {
					page--;
				}
			} else if (button == nextButton) {
				if (page < pages.size() - 1) {
					page++;
				}
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, note.getTitle(), width / 2 + 60, 15, -1);
		displayNote();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void displayNote() {
		fontRendererObj.drawSplitString(pages.get(page), 160, 40, width - 200, 0xFFFFFF);
	}

	private void setupButtons() {
		buttonList.clear();
		editButton = addButton(new GuiNotesButton(0, 10, 40, 110, 20, I18n.format("notes.edit")));
		deleteButton = addButton(new GuiNotesButton(1, 10, 65, 110, 20, I18n.format("notes.delete")));
		pinButton = addButton(new GuiNotesButton(2, 10, 90, 110, 20, isPinned() ? I18n.format("notes.unpin") : I18n.format("notes.pin")));
		doneButton = addButton(new GuiNotesButton(3, 10, height - 30, 110, 20, I18n.format("gui.done")));

		prevButton = addButton(new GuiNotesButton(4, 130, height - 30, 20, 20, I18n.format("<")));
		nextButton = addButton(new GuiNotesButton(5, width - 30, height - 30, 20, 20, I18n.format(">")));
	}

	private void setupPages() {
		if (note != null) {
			final List<String> lines = ConfigHandler.wrapNote ? mc.fontRendererObj.listFormattedStringToWidth(note.getFilteredText(), width - 200) : StringUtils.wrapToWidth(note.getFilteredText(), width - 200);
			pages = new ArrayList<String>();
			int lineCount = 0;
			String page = "";
			for (String line : lines) {
				if (lineCount > 15) {
					pages.add(page);
					page = "";
					lineCount = 0;
				}

				page = page + line + "\n";
				lineCount++;
			}

			if (!page.isEmpty()) {
				pages.add(page);
			}
		}

		if (pages.isEmpty()) {
			pages.add("");
		}
	}

	private boolean isPinned() {
		return note.equals(Notes.pinnedNote);
	}

	private void togglePin() {
		if (isPinned()) {
			Notes.pinnedNote = null;
			pinButton.displayString = I18n.format("notes.pin");
		} else {
			Notes.pinnedNote = note;
			pinButton.displayString = I18n.format("notes.unpin");
		}
	}

	private void deleteNote() {
		mc.displayGuiScreen(new GuiNotesYesNo(new GuiYesNoCallback() {
			public void confirmClicked(boolean result, int id) {
				if (result) {
					GuiDisplayNote.this.mc.displayGuiScreen(new GuiScreenWorking());
					note.delete();
				}

				GuiDisplayNote.this.mc.displayGuiScreen(parentScreen);
			}
		}, "Delete this note?", note.getTitle(), 0));
	}

}