package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.NotesConfig;
import com.chaosthedude.notes.note.Note;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class NotesListEntry extends AlwaysSelectedEntryListWidget.Entry<NotesListEntry> {

	private final MinecraftClient client;
	private final SelectNoteScreen parentScreen;
	private final Note note;
	private final NotesList notesList;
	private long lastClickTime;

	public NotesListEntry(NotesList notesList, Note note) {
		this.notesList = notesList;
		this.note = note;
		parentScreen = notesList.getParentScreen();
		client = MinecraftClient.getInstance();
	}

	@Override
	public void render(DrawContext context, int par1, int par2, int par3, int par4, int par5, int par6, int par7, boolean par8, float par9) {
		context.drawText(client.textRenderer, note.getTitle(), par3 + 1, par2 + 1, 0xffffff, false);
		context.drawText(client.textRenderer, note.getScope().format(), par3 + 4 + client.textRenderer.getWidth(note.getTitle()), par2 + 1, 0x808080, false);
		if (note.isPinned()) {
			context.drawText(client.textRenderer, I18n.translate("notes.pinned"), par3 + 4 + client.textRenderer.getWidth(note.getTitle()) + client.textRenderer.getWidth(note.getScope().format()) + 4, par2 + 1, 0xffffff, false);
		}
		context.drawText(client.textRenderer, note.getTitle(), par3 + 1, par2 + 1, 0xffffff, false);
		context.drawText(client.textRenderer, note.getPreview(MathHelper.floor(notesList.getRowWidth() * 0.9)), par3 + 1, par2 + client.textRenderer.fontHeight + 3, 0x808080, false);
		context.drawText(client.textRenderer, note.getLastModifiedString(), par3 + 1, par2 + client.textRenderer.fontHeight + 14, 0x808080, false);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int button) {
		if (button == 0) {
			notesList.selectNote(this);
			if (Util.getMeasuringTimeMs() - lastClickTime < 250L) {
				loadNote();
				return true;
			}

			lastClickTime = Util.getMeasuringTimeMs();
		}
		return false;
	}

	public void editNote() {
		if (NotesConfig.useInGameEditor || !note.tryOpenExternal()) {
			client.setScreen(new EditNoteScreen(parentScreen, note));
		}
	}

	public void copyNote() {
		note.copy();
		notesList.refreshList();
	}

	public void loadNote() {
		client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if (NotesConfig.useInGameViewer || !note.tryOpenExternal()) {
			client.setScreen(new DisplayNoteScreen(parentScreen, note));
		}
	}

	public void togglePin() {
		if (isPinned()) {
			Notes.pinnedNote = null;
		} else {
			Notes.pinnedNote = note;
			client.setScreen(null);
		}
	}

	public boolean isPinned() {
		return note.equals(Notes.pinnedNote);
	}
	
	public Note getNote() {
		return note;
	}

	public void deleteNote() {
		client.setScreen(new NotesConfirmScreen((result) -> {
			if (result) {
				note.delete();
			}

			NotesListEntry.this.client.setScreen(NotesListEntry.this.parentScreen);
		}, Text.translatable("notes.confirmDelete"), Text.literal(note.getTitle())));
	}

	@Override
	public Text getNarration() {
		return Text.literal(note.getTitle());
	}

}
