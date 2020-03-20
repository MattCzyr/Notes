package com.chaosthedude.notes.gui;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
		addButton(new GuiNotesButton(0, width / 2 - 155, height / 6 + 96, 150, 20, confirmButtonText) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				parentScreen.confirmResult(true, parentButtonClickedID);
			}
		});
		addButton(new GuiNotesButton(1, width / 2 + 5, height / 6 + 96, 150, 20, cancelButtonText) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				parentScreen.confirmResult(false, parentButtonClickedID);
			}
		});

		listLines.clear();
		listLines.addAll(fontRenderer.listFormattedStringToWidth(messageLine2, width - 50));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, messageLine1, width / 2, 70, 16777215);
		int i = 90;

		for (String s : listLines) {
			drawCenteredString(fontRenderer, s, width / 2, i, 16777215);
			i += fontRenderer.FONT_HEIGHT;
		}

		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		super.tick();
		if (--ticksUntilEnable == 0) {
			for (GuiButton button : buttons) {
				button.enabled = true;
			}
		}
	}

	public void setButtonDelay(int ticksUntilEnableIn) {
		ticksUntilEnable = ticksUntilEnableIn;

		for (GuiButton button : buttons) {
			button.enabled = false;
		}
	}

}
