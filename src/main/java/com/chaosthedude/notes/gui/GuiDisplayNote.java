package com.chaosthedude.notes.gui;

import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.StringUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
	public void tick() {
		prevButton.enabled = page > 0;
		nextButton.enabled = page < pages.size() - 1;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, note.getTitle(), width / 2 + 60, 15, -1);
		displayNote();

		super.render(mouseX, mouseY, partialTicks);
	}

	public void displayNote() {
		fontRenderer.drawSplitString(pages.get(page), 160, 40, width - 200, 0xFFFFFF);
	}

	private void setupButtons() {
		editButton = addButton(new GuiNotesButton(0, 10, 40, 110, 20, I18n.format("notes.edit")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(new GuiEditNote(GuiDisplayNote.this.parentScreen, note));
			}
		});
		deleteButton = addButton(new GuiNotesButton(1, 10, 65, 110, 20, I18n.format("notes.delete")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				deleteNote();
			}
		});
		pinButton = addButton(new GuiNotesButton(2, 10, 90, 110, 20, isPinned() ? I18n.format("notes.unpin") : I18n.format("notes.pin")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				togglePin();
				if (isPinned()) {
					mc.displayGuiScreen(null);
				}
			}
		});
		doneButton = addButton(new GuiNotesButton(3, 10, height - 30, 110, 20, I18n.format("gui.done")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(parentScreen);
			}
		});

		prevButton = addButton(new GuiNotesButton(4, 130, height - 30, 20, 20, I18n.format("<")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				if (page > 0) {
					page--;
				}
			}
		});
		nextButton = addButton(new GuiNotesButton(5, width - 30, height - 30, 20, 20, I18n.format(">")) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				if (page < pages.size() - 1) {
					page++;
				}
			}
		});
	}

	private void setupPages() {
		if (note != null) {
			final List<String> lines = ConfigHandler.CLIENT.wrapNote.get() ? fontRenderer.listFormattedStringToWidth(note.getFilteredText(), width - 200) : StringUtils.wrapToWidth(note.getFilteredText(), width - 200);
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
			@Override
			public void confirmResult(boolean result, int id) {
				if (result) {
					GuiDisplayNote.this.mc.displayGuiScreen(new GuiScreenWorking());
					note.delete();
				}

				GuiDisplayNote.this.mc.displayGuiScreen(parentScreen);
			}
		}, I18n.format("notes.confirmDelete"), note.getTitle(), 0));
	}

}