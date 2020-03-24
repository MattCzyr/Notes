package com.chaosthedude.notes.gui;

import com.chaosthedude.notes.util.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

public class NotesButton extends Button {
	
	public NotesButton(int x, int y, int width, int height, String text, IPressable onPress) {
		super(x, y, width, height, text, onPress);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			Minecraft mc = Minecraft.getInstance();
			boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			final float state = getHoverState(hovered);
			final float f = state / 2 * 0.9F + 0.1F;
			final int color = (int) (255.0F * f);

			RenderUtils.drawRect(x, y, x + width, y + height, color / 2 << 24);
			drawCenteredString(mc.fontRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, 0xffffff);
		}
	}

	protected int getHoverState(boolean mouseOver) {
		int state = 2;
		if (!active) {
			state = 5;
		} else if (mouseOver) {
			state = 4;
		}

		return state;
	}

}
