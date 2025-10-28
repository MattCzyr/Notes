package com.chaosthedude.notes.gui;

import java.util.Objects;

import com.chaosthedude.notes.note.Note;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;

@Environment(EnvType.CLIENT)
public class NotesList extends EntryListWidget<NotesListEntry> {

	private final SelectNoteScreen parentScreen;

	public NotesList(SelectNoteScreen notesScreen, MinecraftClient mc, int width, int height, int top, int bottom) {
		super(mc, width, height, top, bottom);
		this.parentScreen = notesScreen;
		refreshList();
	}
	
	@Override
	protected int getScrollbarX() {
		return super.getScrollbarX() + 20;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 50;
	}
	
	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		renderList(context, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderList(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		context.fill(getRowLeft() - 4, getY(), getRowLeft() + getRowWidth() + 4, getY() + getHeight() + 4, 255 / 2 << 24);
		
		enableScissor(context);
		for (int j = 0; j < getEntryCount(); ++j) {
			if (getRowBottom(j) >= getY() && getRowTop(j) <= getBottom()) {
				NotesListEntry e = children().get(j);
				if (e == getSelectedOrNull()) {
					context.fill(getRowLeft() - 4, getRowTop(j) - 4, getRowLeft() + getRowWidth() + 4, getRowTop(j) + itemHeight, 255 / 2 << 24);
				}
				e.render(context, mouseX, mouseY, e == getHoveredEntry(), partialTicks);
			}
		}
		context.disableScissor();
	}
	
	@Override
	protected void enableScissor(DrawContext context) {
		context.enableScissor(getX(), getY(), getRight(), getBottom());
	}
	
	@Override
	public int getRowBottom(int index) {
		return getRowTop(index) + itemHeight;
	}

	public void refreshList() {
		clearEntries();
		for (Note note : Note.getCurrentNotes()) {
			addEntry(new NotesListEntry(this, note));
		}
	}

	public void selectNote(NotesListEntry entry) {
		setSelected(entry);
		parentScreen.selectNote(entry);
	}

	public SelectNoteScreen getParentScreen() {
		return parentScreen;
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

}
