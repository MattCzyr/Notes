package com.chaosthedude.notes.gui;

import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;
import com.chaosthedude.notes.util.RenderUtils;
import com.chaosthedude.notes.util.StringUtils;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
		super(Component.literal(note.getTitle()));
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
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(stack);
		drawCenteredString(stack, font, title.getString(), width / 2 + 60, 15, -1);
		displayNote(stack);

		super.render(stack, mouseX, mouseY, partialTicks);
	}

	public void displayNote(PoseStack stack) {
		RenderUtils.renderSplitString(stack, pages.get(page), 160, 40, width - 200, 0xFFFFFF);
	}

	private void setupButtons() {
		editButton = addRenderableWidget(new NotesButton(10, 40, 110, 20, Component.translatable("notes.edit"), (onPress) -> {
			minecraft.setScreen(new EditNoteScreen(DisplayNoteScreen.this.parentScreen, note));
		}));
		deleteButton = addRenderableWidget(new NotesButton(10, 65, 110, 20, Component.translatable("notes.delete"), (onPress) -> {
			deleteNote();
		}));
		pinButton = addRenderableWidget(new NotesButton(10, 90, 110, 20, isPinned() ? Component.translatable("notes.unpin") : Component.translatable("notes.pin"), (onPress) -> {
			togglePin();
			if (isPinned()) {
				minecraft.setScreen(null);
			}
		}));
		doneButton = addRenderableWidget(new NotesButton(10, height - 30, 110, 20, Component.translatable("gui.done"), (onPress) -> {
			minecraft.setScreen(parentScreen);
		}));

		prevButton = addRenderableWidget(new NotesButton(130, height - 30, 20, 20, Component.translatable("<"), (onPress) -> {
			if (page > 0) {
				page--;
			}
		}));
		nextButton = addRenderableWidget(new NotesButton(width - 30, height - 30, 20, 20, Component.translatable(">"), (onPress) -> {
			if (page < pages.size() - 1) {
				page++;
			}
		}));
	}

	private void setupPages() {
		if (note != null) {
			final List<String> lines = ConfigHandler.CLIENT.wrapNote.get() ? RenderUtils.trimStringToWidth(note.getFilteredText(), width - 200) : StringUtils.wrapToWidth(note.getFilteredText(), width - 200);
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
			pinButton.setMessage(Component.translatable("notes.pin"));
		} else {
			Notes.pinnedNote = note;
			pinButton.setMessage(Component.translatable("notes.unpin"));
		}
	}

	private void deleteNote() {
		minecraft.setScreen(new NotesConfirmScreen((result) -> {
			if (result) {
				note.delete();
			}

			DisplayNoteScreen.this.minecraft.setScreen(parentScreen);
		}, Component.translatable("notes.confirmDelete"), Component.literal(note.getTitle())));
	}

}