package com.chaosthedude.notes.gui;

import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.NotesConfig;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.RenderUtils;
import com.chaosthedude.notes.util.StringUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DisplayNoteScreen extends Screen {

	private final Screen parentScreen;
	private NotesButton doneButton;
	private NotesButton pinButton;
	private NotesButton editButton;
	private NotesButton deleteButton;
	private NotesButton prevButton;
	private NotesButton nextButton;
	private Note note;
	private int page;
	private List<String> pages;

	public DisplayNoteScreen(Screen parentScreen, Note note) {
		super(Text.literal(note.getTitle()));
		this.parentScreen = parentScreen;
		this.note = note;

		page = 0;
		pages = new ArrayList<String>();
		pages.add("");
	}

	@Override
	public void init() {
		setupButtons();
		setupPages();
	}

	@Override
	public void tick() {
		prevButton.active = page > 0;
		nextButton.active = page < pages.size() - 1;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		context.drawCenteredTextWithShadow(textRenderer, title.getString(), width / 2 + 60, 15, -1);
		displayNote(context);

		super.render(context, mouseX, mouseY, partialTicks);
	}

	public void displayNote(DrawContext context) {
		List<String> lines = RenderUtils.splitStringToWidth(pages.get(page), width - 200);
		RenderUtils.renderSplitString(context, lines, 160, 40, 0xFFFFFF);
	}

	private void setupButtons() {
		editButton = addDrawableChild(new NotesButton(10, 40, 110, 20, Text.translatable("notes.edit"), (onPress) -> {
			client.setScreen(new EditNoteScreen(DisplayNoteScreen.this.parentScreen, note));
		}));
		deleteButton = addDrawableChild(new NotesButton(10, 65, 110, 20, Text.translatable("notes.delete"), (onPress) -> {
			deleteNote();
		}));
		pinButton = addDrawableChild(new NotesButton(10, 90, 110, 20, isPinned() ? Text.translatable("notes.unpin") : Text.translatable("notes.pin"), (onPress) -> {
			togglePin();
			if (isPinned()) {
				client.setScreen(null);
			}
		}));
		doneButton = addDrawableChild(new NotesButton(10, height - 30, 110, 20, Text.translatable("gui.done"), (onPress) -> {
			client.setScreen(parentScreen);
		}));

		prevButton = addDrawableChild(new NotesButton(130, height - 30, 20, 20, Text.translatable("<"), (onPress) -> {
			if (page > 0) {
				page--;
			}
		}));
		nextButton = addDrawableChild(new NotesButton(width - 30, height - 30, 20, 20, Text.translatable(">"), (onPress) -> {
			if (page < pages.size() - 1) {
				page++;
			}
		}));
	}

	private void setupPages() {
		if (note != null) {
			final List<String> lines = NotesConfig.wrapNote ? RenderUtils.splitStringToWidth(note.getFilteredText(), width - 200) : StringUtils.wrapToWidth(note.getFilteredText(), width - 200);
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
			pinButton.setMessage(Text.translatable("notes.pin"));
		} else {
			Notes.pinnedNote = note;
			pinButton.setMessage(Text.translatable("notes.unpin"));
		}
	}

	private void deleteNote() {
		client.setScreen(new NotesConfirmScreen((result) -> {
			if (result) {
				note.delete();
			}

			DisplayNoteScreen.this.client.setScreen(parentScreen);
		}, Text.translatable("notes.confirmDelete"), Text.literal(note.getTitle())));
	}

}