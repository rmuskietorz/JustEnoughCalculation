package me.towdium.jecalculation.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

//Thanks to Forge
@Environment(EnvType.CLIENT)
public class GuiUtils {
    public static void drawContinuousTexturedBox(GuiGraphics guiGraphics, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, res);
        drawContinuousTexturedBox(guiGraphics, res, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
    }

    public static void drawContinuousTexturedBox(GuiGraphics guiGraphics, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int fillerWidth = textureWidth - leftBorder - rightBorder;
        int fillerHeight = textureHeight - topBorder - bottomBorder;
        int canvasWidth = width - leftBorder - rightBorder;
        int canvasHeight = height - topBorder - bottomBorder;
        int xPasses = canvasWidth / fillerWidth;
        int remainderWidth = canvasWidth % fillerWidth;
        int yPasses = canvasHeight / fillerHeight;
        int remainderHeight = canvasHeight % fillerHeight;
        drawTexturedModalRect(guiGraphics, res, x, y, u, v, leftBorder, topBorder, textureWidth, textureHeight, zLevel);
        drawTexturedModalRect(guiGraphics, res, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, textureWidth, textureHeight, zLevel);
        drawTexturedModalRect(guiGraphics, res, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, textureWidth, textureHeight, zLevel);
        drawTexturedModalRect(guiGraphics, res, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, textureWidth, textureHeight, zLevel);

        int i;
        for (i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); ++i) {
            drawTexturedModalRect(guiGraphics, res, x + leftBorder + i * fillerWidth, y, u + leftBorder, v, i == xPasses ? remainderWidth : fillerWidth, topBorder, textureWidth, textureHeight, zLevel);
            drawTexturedModalRect(guiGraphics, res, x + leftBorder + i * fillerWidth, y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, i == xPasses ? remainderWidth : fillerWidth, bottomBorder, textureWidth, textureHeight, zLevel);

            for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); ++j) {
                drawTexturedModalRect(guiGraphics, res, x + leftBorder + i * fillerWidth, y + topBorder + j * fillerHeight, u + leftBorder, v + topBorder, i == xPasses ? remainderWidth : fillerWidth, j == yPasses ? remainderHeight : fillerHeight, textureWidth, textureHeight, zLevel);
            }
        }

        for (i = 0; i < yPasses + (remainderHeight > 0 ? 1 : 0); ++i) {
            drawTexturedModalRect(guiGraphics, res, x, y + topBorder + i * fillerHeight, u, v + topBorder, leftBorder, i == yPasses ? remainderHeight : fillerHeight, textureWidth, textureHeight, zLevel);
            drawTexturedModalRect(guiGraphics, res, x + leftBorder + canvasWidth, y + topBorder + i * fillerHeight, u + leftBorder + fillerWidth, v + topBorder, rightBorder, i == yPasses ? remainderHeight : fillerHeight, textureWidth, textureHeight, zLevel);
        }

    }

    private static void drawTexturedModalRect(GuiGraphics guiGraphics, ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, float zLevel) {
        guiGraphics.blit(res, x, y, zLevel, u, v, width, height, textureWidth, textureHeight);
    }

}
