package com.chaosthedude.notes.note;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.chaosthedude.notes.Notes;
import com.chaosthedude.notes.config.ConfigHandler;
import com.chaosthedude.notes.util.FileUtils;
import com.chaosthedude.notes.util.RenderUtils;
import com.chaosthedude.notes.util.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SharedConstants;

public class Note {

	private static final DateFormat dateFormat = new SimpleDateFormat(ConfigHandler.CLIENT.dateFormat.get());
	private static final Minecraft mc = Minecraft.getInstance();
	private static final FontRenderer fontRenderer = mc.fontRenderer;

	private String title;
	private String rawText;
	private Scope scope;
	private File prevSaveFile;
	private File saveFile;
	private File saveDir;

	public Note(String title, String text, Scope scope) {
		this.title = title;
		this.rawText = text;
		this.scope = scope;

		updateSaveFile();
		prevSaveFile = saveFile;
	}

	public Note(File file) {
		update(file);
	}

	public Note setTitle(String newTitle) {
		title = newTitle;
		updateSaveFile();
		return this;
	}

	public Note setText(String text) {
		rawText = text;
		return this;
	}

	public Note setScope(Scope newScope) {
		scope = newScope;
		updateSaveFile();
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getRawText() {
		return rawText;
	}

	public String getFilteredText() {
		return StringUtils.filter(rawText);
	}

	public File getPrevSaveFile() {
		return prevSaveFile;
	}

	public File getSaveFile() {
		return saveFile;
	}

	public Scope getScope() {
		return scope;
	}

	public void updateSaveFile() {
		updateSaveDir();
		saveFile = new File(saveDir, getSaveName());
	}

	public void updateSaveDir() {
		if (scope != null) {
			saveDir = scope.getCurrentSaveDirectory();
		} else {
			catchNullScope();
		}
	}

	public String getPreview(int width) {
		String preview = rawText;
		boolean addEllipsis = false;
		if (fontRenderer.getStringWidth(preview) > width || RenderUtils.trimStringToWidth(preview, width).size() > 1) {
			preview = RenderUtils.trimStringToWidth(preview, width).get(0);
			addEllipsis = true;
		}

		for (char c : StringUtils.FILTER_CHARS) {
			if (preview.contains(String.valueOf(c))) {
				preview = preview.substring(0, preview.indexOf(String.valueOf(c)));
			}
		}
		
		if (preview.indexOf('\n') >= 0) {
			preview = preview.substring(0, preview.indexOf('\n'));
		}

		if (addEllipsis) {
			preview += "...";
		}

		return preview;
	}

	public long getLastModified() {
		return saveFile.lastModified();
	}

	public String getLastModifiedString() {
		return I18n.format("notes.lastModified") + ": " + dateFormat.format(getLastModified());
	}

	public String getUncollidingSaveName(String name) {
		name = name.replaceAll("[\\./\"]", "_");
		File file = new File(saveDir, addFileExtension(name));
		if (!file.equals(prevSaveFile)) {
			while (file.exists()) {
				name = name + "-";
				file = new File(saveDir, addFileExtension(name));
			}
		}

		return name;
	}

	public String getSaveName() {
		String saveDirName = title.trim();
		for (char c : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
			saveDirName = saveDirName.replace(c, '_');
		}

		if (saveDirName == null || saveDirName.isEmpty()) {
			saveDirName = "New Note";
		}

		return addFileExtension(getUncollidingSaveName(saveDirName));
	}

	public String addFileExtension(String name) {
		return name + ".txt";
	}

	public void save() {
		BufferedWriter writer = null;
		try {
			prevSaveFile.delete();

			if (!saveFile.getParentFile().exists()) {
				saveFile.getParentFile().mkdirs();
			}

			writer = new BufferedWriter(new FileWriter(saveFile));
			writer.write(rawText);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean tryOpenExternal() {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().edit(getSaveFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	public void copy() {
		final Note copy = new Note(title + " Copy", rawText, scope);
		copy.save();
	}

	public void delete() {
		if (isPinned()) {
			Notes.pinnedNote = null;
		}

		saveFile.delete();
	}

	public void catchNullScope() {
		scope = Scope.GLOBAL;
		saveDir = scope.getCurrentSaveDirectory();
		Notes.logger.error("No scope found for the following note:" + getTitle() + ". Setting scope to Global.");
	}

	public void update(File file) {
		title = FileUtils.getFileName(file);
		scope = Scope.getScopeFromParentFile(file.getParentFile());
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			final StringBuilder builder = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				builder.append(line);
				builder.append(System.lineSeparator());
				line = reader.readLine();
			}
			rawText = builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		saveFile = file;
		prevSaveFile = file;
	}

	public void update() {
		update(saveFile);
	}

	public boolean equals(Note note) {
		try {
			return note != null && note.getSaveFile() != null && saveFile.getCanonicalPath().equals(note.getSaveFile().getCanonicalPath());
		} catch (IOException e) {
			return false;
		}
	}

	public boolean isPinned() {
		return equals(Notes.pinnedNote);
	}

	public boolean isValidScope() {
		return scope == Scope.getCurrentScope() || scope == Scope.GLOBAL;
	}

	public static List<Note> getCurrentNotes() {
		final List<Note> notes = new ArrayList<Note>();
		if (Scope.currentScopeIsValid()) {
			for (final File file : Scope.getCurrentScope().getCurrentSaveDirectory().listFiles()) {
				if (FileUtils.isNote(file)) {
					notes.add(new Note(file));
				}
			}
		}

		for (final File file : Scope.GLOBAL.getCurrentSaveDirectory().listFiles()) {
			if (FileUtils.isNote(file)) {
				notes.add(new Note(file));
			}
		}

		Collections.sort(notes, Collections.reverseOrder(new Comparator<Note>() {
			@Override
			public int compare(Note n1, Note n2) {
				return Long.compare(n1.getLastModified(), n2.getLastModified());
			}
		}));

		return notes;
	}

}
