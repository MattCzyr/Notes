package com.chaosthedude.notes.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NotesListEntry extends ObjectSelectionList.Entry<NotesListEntry> {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private final Minecraft mc;
	private final SelectNoteScreen parentScreen;
	private final Note note;
	private final NotesList notesList;
	private long lastClickTime;

	public NotesListEntry(NotesList notesList, Note note) {
		this.notesList = notesList;
		this.note = note;
		parentScreen = notesList.getParentScreen();
		mc = Minecraft.getInstance();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int par1, int par2, int par3, int par4, int par5, int par6, int par7, boolean par8, float par9) {
		guiGraphics.drawString(mc.font, note.getTitle(), par3 + 1, par2 + 1, 0xffffff);
		guiGraphics.drawString(mc.font, note.getScope().format(), par3 + 4 + mc.font.width(note.getTitle()), par2 + 1, 0x808080);
		if (note.isPinned()) {
			guiGraphics.drawString(mc.font, I18n.get("notes.pinned"), par3 + 4 + mc.font.width(note.getTitle()) + mc.font.width(note.getScope().format()) + 4, par2 + 1, 0xffffff);
		}
		guiGraphics.drawString(mc.font, note.getTitle(), par3 + 1, par2 + 1, 0xffffff);
		guiGraphics.drawString(mc.font, note.getPreview(Mth.floor(notesList.getRowWidth() * 0.9)), par3 + 1, par2 + mc.font.lineHeight + 3, 0x808080);
		guiGraphics.drawString(mc.font, note.getLastModifiedString(), par3 + 1, par2 + mc.font.lineHeight + 14, 0x808080);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int button) {
		if (button == 0) {
			notesList.selectNote(this);
			if (Util.getMillis() - lastClickTime < 250L) {
				loadNote();
				return true;
			}

			lastClickTime = Util.getMillis();
		}
		return false;
	}

	public void editNote() {
		if (ConfigHandler.CLIENT.useInGameEditor.get() || !note.tryOpenExternal()) {
			mc.setScreen(new EditNoteScreen(parentScreen, note));
		}
	}

	public void copyNote() {
		note.copy();
		notesList.refreshList();
	}

	public void loadNote() {
		mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if (ConfigHandler.CLIENT.useInGameViewer.get() || !note.tryOpenExternal()) {
			mc.setScreen(new DisplayNoteScreen(parentScreen, note));
		}
	}

	public void togglePin() {
		if (isPinned()) {
			Notes.pinnedNote = null;
		} else {
			Notes.pinnedNote = note;
			mc.setScreen(null);
		}
	}

	public boolean isPinned() {
		return note.equals(Notes.pinnedNote);
	}
	
	public Note getNote() {
		return note;
	}

	public void deleteNote() {
		mc.setScreen(new NotesConfirmScreen((result) -> {
			if (result) {
				note.delete();
			}

			NotesListEntry.this.mc.setScreen(NotesListEntry.this.parentScreen);
		}, Component.translatable("notes.confirmDelete"), Component.literal(note.getTitle())));
	}

	@Override
	public Component getNarration() {
		return Component.literal(note.getTitle());
	}

}
