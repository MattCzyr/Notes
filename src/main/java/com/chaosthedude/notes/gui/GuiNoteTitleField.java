package com.chaosthedude.notes.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.chaosthedude.notes.util.RenderUtils;
import com.chaosthedude.notes.util.StringUtils;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.sun.prism.impl.VertexBuffer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

public class GuiNoteTitleField extends Gui {

	private final int id;
	private final FontRenderer fontRenderer;
	public int xPosition;
	public int yPosition;
	public int width;
	public int height;
	private String text = "";
	private int maxStringLength = 32;
	private int cursorCounter;
	private boolean enableBackgroundDrawing = true;
	private boolean canLoseFocus = true;
	private boolean isFocused;
	private boolean isEnabled = true;
	private int lineScrollOffset;
	private int cursorPosition;
	private int selectionEnd;
	private int enabledColor = 14737632;
	private int disabledColor = 7368816;
	private boolean visible = true;

	public GuiNoteTitleField(int id, FontRenderer fontRenderer, int x, int y, int width, int height) {
		this.id = id;
		this.fontRenderer = fontRenderer;
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
	}

	public void updateCursorCounter() {
		cursorCounter++;
	}

	public void setText(String newText) {
		if (newText.length() > maxStringLength) {
			text = newText.substring(0, maxStringLength);
		} else {
			text = newText;
		}

		setCursorPositionEnd();
	}

	public String getText() {
		return text;
	}

	public String getSelectedText() {
		final int start = cursorPosition < selectionEnd ? cursorPosition : selectionEnd;
		final int end = cursorPosition < selectionEnd ? selectionEnd : cursorPosition;
		return text.substring(start, end);
	}

	public void writeText(String textToWrite) {
		final String filtered = StringUtils.filterChatAllowedCharacters(textToWrite);
		String s = "";
		int i = cursorPosition < selectionEnd ? cursorPosition : selectionEnd;
		int j = cursorPosition < selectionEnd ? selectionEnd : cursorPosition;
		int k = maxStringLength - text.length() - (i - j);

		if (!text.isEmpty()) {
			s = s + text.substring(0, i);
		}

		int l;

		if (k < filtered.length()) {
			s = s + filtered.substring(0, k);
			l = k;
		} else {
			s = s + filtered;
			l = filtered.length();
		}

		if (!text.isEmpty() && j < text.length()) {
			s = s + text.substring(j);
		}

		text = s;
		moveCursorBy(i - selectionEnd + l);
	}

	public void deleteWords(int num) {
		if (!text.isEmpty()) {
			if (selectionEnd != cursorPosition) {
				writeText("");
			} else {
				deleteFromCursor(getNthWordFromCursor(num) - cursorPosition);
			}
		}
	}

	public void deleteFromCursor(int num) {
		if (!text.isEmpty()) {
			if (selectionEnd != cursorPosition) {
				writeText("");
			} else {
				final boolean flag = num < 0;
				final int start = flag ? cursorPosition + num : cursorPosition;
				final int end = flag ? cursorPosition : cursorPosition + num;
				String s = "";

				if (start >= 0) {
					s = text.substring(0, start);
				}

				if (end < text.length()) {
					s = s + text.substring(end);
				}

				text = s;

				if (flag) {
					moveCursorBy(num);
				}
			}
		}
	}

	public int getId() {
		return id;
	}

	public int getNthWordFromCursor(int numWords) {
		return getNthWordFromPos(numWords, getCursorPosition());
	}

	public int getNthWordFromPos(int n, int pos) {
		return getNthWordFromPosWS(n, pos, true);
	}

	public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
		final int absN = Math.abs(n);
		final boolean negative = n < 0;
		int wordPos = pos;

		for (int k = 0; k < absN; k++) {
			if (!negative) {
				int l = text.length();
				wordPos = text.indexOf(32, wordPos);

				if (wordPos == -1) {
					wordPos = l;
				} else {
					while (skipWs && wordPos < l && text.charAt(wordPos) == 32) {
						wordPos++;
					}
				}
			} else {
				while (skipWs && wordPos > 0 && text.charAt(wordPos - 1) == 32) {
					wordPos--;
				}

				while (wordPos > 0 && text.charAt(wordPos - 1) != 32) {
					wordPos--;
				}
			}
		}

		return wordPos;
	}

	public void moveCursorBy(int num) {
		setCursorPosition(selectionEnd + num);
	}

	public void setCursorPosition(int pos) {
		cursorPosition = pos;
		cursorPosition = MathHelper.clamp_int(cursorPosition, 0, text.length());
		setSelectionPos(cursorPosition);
	}

	public void setCursorPositionZero() {
		setCursorPosition(0);
	}

	public void setCursorPositionEnd() {
		setCursorPosition(text.length());
	}

	public boolean textboxKeyTyped(char typedChar, int keyCode) {
		if (!isFocused) {
			return false;
		} else if (isKeyComboCtrlA(keyCode)) {
			setCursorPositionEnd();
			setSelectionPos(0);
			return true;
		} else if (isKeyComboCtrlC(keyCode)) {
			GuiScreen.setClipboardString(getSelectedText());
			return true;
		} else if (isKeyComboCtrlV(keyCode)) {
			if (isEnabled) {
				writeText(GuiScreen.getClipboardString());
			}

			return true;
		} else if (isKeyComboCtrlX(keyCode)) {
			GuiScreen.setClipboardString(getSelectedText());
			if (this.isEnabled) {
				writeText("");
			}

			return true;
		} else {
			switch (keyCode) {
			case Keyboard.KEY_BACK:
				if (GuiScreen.isCtrlKeyDown()) {
					if (isEnabled) {
						deleteWords(-1);
					}
				} else if (isEnabled) {
					deleteFromCursor(-1);
				}

				return true;
			case Keyboard.KEY_LEFT:
				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						setSelectionPos(getNthWordFromPos(-1, getSelectionEnd()));
					} else {
						setSelectionPos(getSelectionEnd() - 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					setCursorPosition(getNthWordFromCursor(-1));
				} else {
					moveCursorBy(-1);
				}

				return true;
			case Keyboard.KEY_RIGHT:
				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown()) {
						setSelectionPos(getNthWordFromPos(1, getSelectionEnd()));
					} else {
						setSelectionPos(getSelectionEnd() + 1);
					}
				} else if (GuiScreen.isCtrlKeyDown()) {
					setCursorPosition(getNthWordFromCursor(1));
				} else {
					moveCursorBy(1);
				}

				return true;
			case Keyboard.KEY_DOWN:
			case Keyboard.KEY_END:
				if (GuiScreen.isShiftKeyDown()) {
					setSelectionPos(text.length());
				} else {
					setCursorPositionEnd();
				}

				return true;
			case Keyboard.KEY_UP:
			case Keyboard.KEY_HOME:
				if (GuiScreen.isShiftKeyDown()) {
					setSelectionPos(0);
				} else {
					setCursorPositionZero();
				}

				return true;
			case Keyboard.KEY_DELETE:
				if (GuiScreen.isCtrlKeyDown()) {
					if (isEnabled) {
						deleteWords(1);
					}
				} else if (isEnabled) {
					deleteFromCursor(1);
				}

				return true;
			default:
				if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
					if (isEnabled) {
						writeText(Character.toString(typedChar));
					}

					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		final boolean isWithinBounds = mouseX >= xPosition && mouseX < xPosition + width && mouseY >= yPosition && mouseY < yPosition + height;
		if (canLoseFocus) {
			setFocused(isWithinBounds);
		}

		if (isFocused && isWithinBounds && mouseButton == 0) {
			int i = mouseX - xPosition;
			if (enableBackgroundDrawing) {
				i -= 4;
			}

			final String trimmed = fontRenderer.trimStringToWidth(text.substring(lineScrollOffset), getWidth());
			setCursorPosition(fontRenderer.trimStringToWidth(trimmed, i).length() + lineScrollOffset);
		}
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		final boolean isWithinBounds = mouseX >= xPosition && mouseX < xPosition + width && mouseY >= yPosition && mouseY < yPosition + height;
		if (canLoseFocus) {
			setFocused(isWithinBounds);
		}

		if (isFocused && isWithinBounds && state == 0) {
			int i = mouseX - xPosition;
			if (enableBackgroundDrawing) {
				i -= 4;
			}

			final String trimmed = fontRenderer.trimStringToWidth(text.substring(lineScrollOffset), getWidth());
			setSelectionPos(fontRenderer.trimStringToWidth(trimmed, i).length() + lineScrollOffset);
		}
	}

	public void drawTextBox() {
		if (getVisible()) {
			if (getEnableBackgroundDrawing()) {
				RenderUtils.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 255 / 2 << 24);
			}

			final int color = isEnabled ? enabledColor : disabledColor;
			final int renderCursorPos = cursorPosition - lineScrollOffset;
			final String trimmed = fontRenderer.trimStringToWidth(text.substring(lineScrollOffset), getWidth());
			final boolean cursorIsVisible = renderCursorPos >= 0 && renderCursorPos <= trimmed.length();
			final boolean shouldDisplayCursor = isFocused && cursorCounter / 6 % 2 == 0 && cursorIsVisible;
			final int x = enableBackgroundDrawing ? xPosition + 4 : xPosition;
			final int y = enableBackgroundDrawing ? yPosition + (height - 8) / 2 : yPosition;
			int renderSelectionPos = selectionEnd - lineScrollOffset;
			int whatIsThisMojang = x;

			if (renderSelectionPos > trimmed.length()) {
				renderSelectionPos = trimmed.length();
			}

			if (!trimmed.isEmpty()) {
				final String s1 = cursorIsVisible ? trimmed.substring(0, renderCursorPos) : trimmed;
				whatIsThisMojang = fontRenderer.drawString(s1, x, y, color, true);
			}

			final boolean cursorIsAtEnd = cursorPosition < text.length() || text.length() >= getMaxStringLength();
			int renderX = whatIsThisMojang;

			if (!cursorIsVisible) {
				renderX = renderCursorPos > 0 ? x + width : x;
			} else if (cursorIsAtEnd) {
				renderX = whatIsThisMojang - 1;
				whatIsThisMojang--;
			}

			if (!trimmed.isEmpty() && cursorIsVisible && renderCursorPos < trimmed.length()) {
				whatIsThisMojang = fontRenderer.drawString(trimmed.substring(renderCursorPos), whatIsThisMojang, y, color, true);
			}

			if (shouldDisplayCursor) {
				if (cursorIsAtEnd) {
					Gui.drawRect(renderX, y - 1, renderX + 1, y + 1 + fontRenderer.FONT_HEIGHT, -3092272);
				} else {
					fontRenderer.drawStringWithShadow("_", renderX, y, color);
				}
			}

			if (renderSelectionPos != renderCursorPos) {
				int l1 = x + fontRenderer.getStringWidth(trimmed.substring(0, renderSelectionPos));
				drawSelectionBox(renderX, y - 1, l1 - 1, y + 1 + fontRenderer.FONT_HEIGHT);
			}
		}
	}

	private void drawSelectionBox(int startX, int startY, int endX, int endY) {
		int i1;

		if (startX < endX) {
			i1 = startX;
			startX = endX;
			endX = i1;
		}

		if (startY < endY) {
			i1 = startY;
			startY = endY;
			endY = i1;
		}

		if (endX > this.xPosition + this.width) {
			endX = this.xPosition + this.width;
		}

		if (startX > this.xPosition + this.width) {
			startX = this.xPosition + this.width;
		}

		Tessellator tessellator = Tessellator.instance;
		GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);
		tessellator.startDrawingQuads();
		tessellator.addVertex((double) startX, (double) endY, 0.0D);
		tessellator.addVertex((double) endX, (double) endY, 0.0D);
		tessellator.addVertex((double) endX, (double) startY, 0.0D);
		tessellator.addVertex((double) startX, (double) startY, 0.0D);
		tessellator.draw();
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void setMaxStringLength(int length) {
		maxStringLength = length;

		if (text.length() > length) {
			text = text.substring(0, length);
		}
	}

	public int getMaxStringLength() {
		return maxStringLength;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	public boolean getEnableBackgroundDrawing() {
		return enableBackgroundDrawing;
	}

	public void setEnableBackgroundDrawing(boolean enableBackgroundDrawing) {
		this.enableBackgroundDrawing = enableBackgroundDrawing;
	}

	public void setTextColor(int color) {
		enabledColor = color;
	}

	public void setDisabledTextColour(int color) {
		disabledColor = color;
	}

	public void setFocused(boolean isFocused) {
		if (isFocused && !this.isFocused) {
			cursorCounter = 0;
		}

		this.isFocused = isFocused;
	}

	public boolean isFocused() {
		return isFocused;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}

	public int getSelectionEnd() {
		return selectionEnd;
	}

	public int getWidth() {
		return getEnableBackgroundDrawing() ? width - 8 : width;
	}

	public void setSelectionPos(int position) {
		final int length = text.length();

		if (position > length) {
			position = length;
		}

		if (position < 0) {
			position = 0;
		}

		selectionEnd = position;

		if (fontRenderer != null) {
			if (lineScrollOffset > length) {
				lineScrollOffset = length;
			}

			final int width = getWidth();
			String s = fontRenderer.trimStringToWidth(text.substring(lineScrollOffset), width);
			int k = s.length() + lineScrollOffset;

			if (position == lineScrollOffset) {
				lineScrollOffset -= fontRenderer.trimStringToWidth(text, width, true).length();
			}

			if (position > k) {
				lineScrollOffset += position - k;
			} else if (position <= lineScrollOffset) {
				lineScrollOffset -= lineScrollOffset - position;
			}

			lineScrollOffset = MathHelper.clamp_int(lineScrollOffset, 0, length);
		}
	}

	public void setCanLoseFocus(boolean canLoseFocus) {
		this.canLoseFocus = canLoseFocus;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean isVisible) {
		visible = isVisible;
	}

	public static boolean isAltKeyDown() {
		return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
	}

	public static boolean isKeyComboCtrlX(int keyID) {
		return keyID == 45 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !isAltKeyDown();
	}

	public static boolean isKeyComboCtrlV(int keyID) {
		return keyID == 47 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !isAltKeyDown();
	}

	public static boolean isKeyComboCtrlC(int keyID) {
		return keyID == 46 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !isAltKeyDown();
	}

	public static boolean isKeyComboCtrlA(int keyID) {
		return keyID == 30 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !isAltKeyDown();
	}

}
