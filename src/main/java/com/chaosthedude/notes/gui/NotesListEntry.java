package com.chaosthedude.notes.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.note.Note;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.list.ExtendedList.AbstractListEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NotesListEntry extends AbstractListEntry<NotesListEntry> {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
	private final Minecraft mc;
	private final SelectNoteScreen guiNotes;
	private final Note note;
	private final NotesList notesList;
	private long lastClickTime;

	public NotesListEntry(NotesList notesList, Note note) {
		this.notesList = notesList;
		this.note = note;
		guiNotes = notesList.getGuiNotes();
		mc = Minecraft.getInstance();
	}

	@Override
	public void render(MatrixStack stack, int par1, int par2, int par3, int par4, int par5, int par6, int par7, boolean par8, float par9) {
		mc.fontRenderer.drawString(stack, note.getTitle(), par3 + 1, par2 + 1, 0xffffff);
		mc.fontRenderer.drawString(stack, note.getScope().format(), par3 + 4 + mc.fontRenderer.getStringWidth(note.getTitle()), par2 + 1, 0x808080);
		if (note.isPinned()) {
			mc.fontRenderer.drawString(stack, I18n.format("notes.pinned"), par3 + 4 + mc.fontRenderer.getStringWidth(note.getTitle()) + mc.fontRenderer.getStringWidth(note.getScope().format()) + 4, par2 + 1, 0xffffff);
		}
		mc.fontRenderer.drawString(stack, note.getTitle(), par3 + 1, par2 + 1, 0xffffff);
		mc.fontRenderer.drawString(stack, note.getPreview(MathHelper.floor(notesList.getRowWidth() * 0.9)), par3 + 1, par2 + mc.fontRenderer.FONT_HEIGHT + 3, 0x808080);
		mc.fontRenderer.drawString(stack, note.getLastModifiedString(), par3 + 1, par2 + mc.fontRenderer.FONT_HEIGHT + 14, 0x808080);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int button) {
		if (button == 0) {
			notesList.selectNote(this);
			if (Util.milliTime() - lastClickTime < 250L) {
				loadNote();
				return true;
			}

			lastClickTime = Util.milliTime();
		}
		return false;
	}

	public void editNote() {
		if (ConfigHandler.CLIENT.useInGameEditor.get() || !note.tryOpenExternal()) {
			mc.displayGuiScreen(new EditNoteScreen(guiNotes, note));
		}
	}

	public void copyNote() {
		note.copy();
		notesList.refreshList();
	}

	public void loadNote() {
		mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		if (ConfigHandler.CLIENT.useInGameViewer.get() || !note.tryOpenExternal()) {
			mc.displayGuiScreen(new DisplayNoteScreen(guiNotes, note));
		}
	}

	public void togglePin() {
		if (isPinned()) {
			Notes.pinnedNote = null;
		} else {
			Notes.pinnedNote = note;
			mc.displayGuiScreen(null);
		}
	}

	public boolean isPinned() {
		return note.equals(Notes.pinnedNote);
	}
	
	public Note getNote() {
		return note;
	}

	public void deleteNote() {
		mc.displayGuiScreen(new NotesConfirmScreen((result) -> {
			if (result) {
				note.delete();
			}

			NotesListEntry.this.mc.displayGuiScreen(NotesListEntry.this.guiNotes);
		}, new StringTextComponent(I18n.format("notes.confirmDelete")), new StringTextComponent(note.getTitle())));
	}

}
