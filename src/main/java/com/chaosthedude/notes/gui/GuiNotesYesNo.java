package com.chaosthedude.notes.gui;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiNotesYesNo extends GuiScreen {

	protected GuiYesNoCallback parentScreen;
	protected String messageLine1;
	private final String messageLine2;
	private final List<String> listLines = Lists.<String> newArrayList();
	protected String confirmButtonText;
	protected String cancelButtonText;
	protected int parentButtonClickedID;
	private int ticksUntilEnable;

	public GuiNotesYesNo(GuiYesNoCallback parentScreen, String messageLine1, String messageLine2, int parentButtonClickedID) {
		this.parentScreen = parentScreen;
		this.messageLine1 = messageLine1;
		this.messageLine2 = messageLine2;
		this.parentButtonClickedID = parentButtonClickedID;

		confirmButtonText = I18n.format("gui.yes", new Object[0]);
		cancelButtonText = I18n.format("gui.no", new Object[0]);
	}

	public GuiNotesYesNo(GuiYesNoCallback parentScreen, String messageLine1, String messageLine2, String confirmButtonText, String cancelButtonText, int parentButtonClickedID) {
		this.parentScreen = parentScreen;
		this.messageLine1 = messageLine1;
		this.messageLine2 = messageLine2;
		this.confirmButtonText = confirmButtonText;
		this.cancelButtonText = cancelButtonText;
		this.parentButtonClickedID = parentButtonClickedID;
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiNotesButton(0, width / 2 - 155, height / 6 + 96, 150, 20, confirmButtonText));
		buttonList.add(new GuiNotesButton(1, width / 2 + 5, height / 6 + 96, 150, 20, cancelButtonText));

		listLines.clear();
		listLines.addAll(fontRendererObj.listFormattedStringToWidth(messageLine2, width - 50));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		parentScreen.confirmClicked(button.id == 0, parentButtonClickedID);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, messageLine1, width / 2, 70, 16777215);
		int i = 90;

		for (String s : listLines) {
			drawCenteredString(fontRendererObj, s, width / 2, i, 16777215);
			i += fontRendererObj.FONT_HEIGHT;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (--ticksUntilEnable == 0) {
			for (GuiButton button : buttonList) {
				button.enabled = true;
			}
		}
	}

	public void setButtonDelay(int ticksUntilEnableIn) {
		ticksUntilEnable = ticksUntilEnableIn;

		for (GuiButton button : buttonList) {
			button.enabled = false;
		}
	}

}
