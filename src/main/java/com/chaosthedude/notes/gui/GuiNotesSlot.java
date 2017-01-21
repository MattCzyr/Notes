package com.chaosthedude.notes.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public abstract class GuiNotesSlot {

	private final Minecraft mc;
	public int width;
	public int height;
	public int top;
	public int bottom;
	public int right;
	public int left;
	public final int slotHeight;
	private int scrollUpButtonID;
	private int scrollDownButtonID;
	protected int mouseX;
	protected int mouseY;
	protected boolean field_148163_i = true;
	private float initialClickY = -2.0F;
	private float scrollMultiplier;
	private float amountScrolled;
	private int selectedElement = -1;
	private long lastClicked;
	private boolean showSelectionBox = true;
	private boolean hasListHeader;
	public int headerPadding;
	private boolean visible = true;

	public GuiNotesSlot(Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
		this.mc = mc;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.slotHeight = slotHeight;
		this.left = 0;
		this.right = width;
	}

	public void setDimensions(int width, int height, int top, int bottom) {
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = 0;
		this.right = width;
	}

	public void setShowSelectionBox(boolean showSelectionBox) {
		this.showSelectionBox = showSelectionBox;
	}

	protected void setHasListHeader(boolean hasListHeader, int headerPadding) {
		this.hasListHeader = hasListHeader;
		this.headerPadding = headerPadding;

		if (!hasListHeader) {
			this.headerPadding = 0;
		}
	}

	protected abstract int getSize();

	protected abstract void elementClicked(int index, boolean flag, int mouseX, int mouseY);

	protected abstract boolean isSelected(int index);

	protected int getContentHeight() {
		return getSize() * slotHeight + headerPadding;
	}

	protected abstract void drawBackground();

	protected abstract void drawSlot(int p_148126_1_, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator tessellator, int mouseX, int mouseY);

	protected void drawListHeader(int x, int y, Tessellator tessellator) {
	}

	protected void func_148132_a(int p_148132_1_, int p_148132_2_) {
	}

	protected void func_148142_b(int p_148142_1_, int p_148142_2_) {
	}

	public int getIndexAtPos(int mouseX, int mouseY) {
		int k = left + width / 2 - getListWidth() / 2;
		int l = left + width / 2 + getListWidth() / 2;
		int i1 = mouseY - top - headerPadding + (int) amountScrolled - 4;
		int j1 = i1 / slotHeight;
		return mouseX < getScrollBarX() && mouseX >= k && mouseX <= l && j1 >= 0 && i1 >= 0 && j1 < getSize() ? j1 : -1;
	}

	public void registerScrollButtons(int scrollUpButtonID, int scrollDownButtonID) {
		this.scrollUpButtonID = scrollUpButtonID;
		this.scrollDownButtonID = scrollDownButtonID;
	}

	private void bindAmountScrolled() {
		int i = func_148135_f();
		if (i < 0) {
			i /= 2;
		}

		if (!field_148163_i && i < 0) {
			i = 0;
		}

		if (amountScrolled < 0.0F) {
			amountScrolled = 0.0F;
		}

		if (amountScrolled > (float) i) {
			amountScrolled = (float) i;
		}
	}

	public int func_148135_f() {
		return getContentHeight() - (bottom - top - 4);
	}

	public int getAmountScrolled() {
		return (int) amountScrolled;
	}

	public boolean isWithinYBounds(int position) {
		return position >= top && position <= bottom;
	}

	public void scrollBy(int amount) {
		amountScrolled += (float) amount;
		bindAmountScrolled();
		initialClickY = -2.0F;
	}

	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == scrollUpButtonID) {
				amountScrolled -= (float) (slotHeight * 2 / 3);
				initialClickY = -2.0F;
				bindAmountScrolled();
			} else if (button.id == scrollDownButtonID) {
				amountScrolled += (float) (slotHeight * 2 / 3);
				initialClickY = -2.0F;
				bindAmountScrolled();
			}
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.drawBackground();
		int k = this.getSize();
		int l = this.getScrollBarX();
		int i1 = l + 6;
		int l1;
		int i2;
		int k2;
		int i3;

		if (mouseX > this.left && mouseX < this.right && mouseY > this.top && mouseY < this.bottom) {
			if (Mouse.isButtonDown(0) && isVisible()) {
				if (this.initialClickY == -1.0F) {
					boolean flag1 = true;

					if (mouseY >= this.top && mouseY <= this.bottom) {
						int k1 = this.width / 2 - this.getListWidth() / 2;
						l1 = this.width / 2 + this.getListWidth() / 2;
						i2 = mouseY - this.top - this.headerPadding + (int) this.amountScrolled - 4;
						int j2 = i2 / this.slotHeight;

						if (mouseX >= k1 && mouseX <= l1 && j2 >= 0 && i2 >= 0 && j2 < k) {
							boolean flag = j2 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
							this.elementClicked(j2, flag, mouseX, mouseY);
							this.selectedElement = j2;
							this.lastClicked = Minecraft.getSystemTime();
						} else if (mouseX >= k1 && mouseX <= l1 && i2 < 0) {
							this.func_148132_a(mouseX - k1, mouseY - this.top + (int) this.amountScrolled - 4);
							flag1 = false;
						}

						if (mouseX >= l && mouseX <= i1) {
							this.scrollMultiplier = -1.0F;
							i3 = this.func_148135_f();

							if (i3 < 1) {
								i3 = 1;
							}

							k2 = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getContentHeight());

							if (k2 < 32) {
								k2 = 32;
							}

							if (k2 > this.bottom - this.top - 8) {
								k2 = this.bottom - this.top - 8;
							}

							this.scrollMultiplier /= (float) (this.bottom - this.top - k2) / (float) i3;
						} else {
							this.scrollMultiplier = 1.0F;
						}

						if (flag1) {
							this.initialClickY = (float) mouseY;
						} else {
							this.initialClickY = -2.0F;
						}
					} else {
						this.initialClickY = -2.0F;
					}
				} else if (this.initialClickY >= 0.0F) {
					this.amountScrolled -= ((float) mouseY - this.initialClickY) * this.scrollMultiplier;
					this.initialClickY = (float) mouseY;
				}
			} else {
				for (; !this.mc.gameSettings.touchscreen && Mouse.next(); this.mc.currentScreen.handleMouseInput()) {
					int j1 = Mouse.getEventDWheel();

					if (j1 != 0) {
						if (j1 > 0) {
							j1 = -1;
						} else if (j1 < 0) {
							j1 = 1;
						}

						this.amountScrolled += (float) (j1 * this.slotHeight / 2);
					}
				}

				this.initialClickY = -1.0F;
			}
		}

		this.bindAmountScrolled();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		Tessellator tessellator = Tessellator.instance;
		drawContainerBackground(tessellator);
		l1 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
		i2 = this.top + 4 - (int) this.amountScrolled;

		if (this.hasListHeader) {
			this.drawListHeader(l1, i2, tessellator);
		}

		this.drawSelectionBox(l1, i2, mouseX, mouseY);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getListWidth() {
		return 220;
	}

	protected void drawSelectionBox(int x, int y, int mouseX, int mouseY) {
		final Tessellator tessellator = Tessellator.instance;
		final int size = getSize();
		for (int i = 0; i < size; i++) {
			int j = x + i * slotHeight + headerPadding;
			int k = slotHeight - 4;
			if (j <= bottom && j + k >= top) {
				if (showSelectionBox && isSelected(i)) {
					int i2 = left + (width / 2 - getListWidth() / 2);
					int j2 = left + width / 2 + getListWidth() / 2;
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					tessellator.startDrawingQuads();
					tessellator.setColorOpaque_I(8421504);
					tessellator.addVertexWithUV(i2, j + k + 2, 0.0D, 0.0D, 1.0D);
					tessellator.addVertexWithUV(j2, j + k + 2, 0.0D, 1.0D, 1.0D);
					tessellator.addVertexWithUV(j2, j - 2, 0.0D, 1.0D, 0.0D);
					tessellator.addVertexWithUV(i2, j - 2, 0.0D, 0.0D, 0.0D);
					tessellator.setColorOpaque_I(0);
					tessellator.addVertexWithUV(i2 + 1, j + k + 1, 0.0D, 0.0D, 1.0D);
					tessellator.addVertexWithUV(j2 - 1, j + k + 1, 0.0D, 1.0D, 1.0D);
					tessellator.addVertexWithUV(j2 - 1, j - 1, 0.0D, 1.0D, 0.0D);
					tessellator.addVertexWithUV(i2 + 1, j - 1, 0.0D, 0.0D, 0.0D);
					tessellator.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				drawSlot(i, y, j, k, tessellator, mouseX, mouseY);
			}
		}
	}

	protected int getScrollBarX() {
		return width / 2 + 124;
	}

	public void setSlotXBoundsFromLeft(int position) {
		left = position;
		right = position + width;
	}

	public int getSlotHeight() {
		return slotHeight;
	}

	protected void drawContainerBackground(Tessellator tessellator) {
		mc.getTextureManager().bindTexture(Gui.optionsBackground);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f1 = 32.0F;
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(2105376);
		tessellator.addVertexWithUV(left, bottom, 0.0D, ((float) left / f1), ((float) (bottom + (int) amountScrolled) / f1));
		tessellator.addVertexWithUV(right, bottom, 0.0D, ((float) right / f1), ((float) (bottom + (int) amountScrolled) / f1));
		tessellator.addVertexWithUV(right, top, 0.0D, ((float) right / f1), ((float) (top + (int) amountScrolled) / f1));
		tessellator.addVertexWithUV(left, top, 0.0D, ((float) left / f1), ((float) (top + (int) amountScrolled) / f1));
		tessellator.draw();
	}

}
