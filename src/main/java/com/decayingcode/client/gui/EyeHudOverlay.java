package com.decayingcode.client.gui;

import com.decayingcode.TheDecayingCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * HUD-оверлей глаза для Forge 1.20.1.
 *
 * <p>Текстура 16x16 масштабируется до 32x32 и рисуется в правом верхнем углу.
 * Для отрисовки используется только актуальный GuiGraphics.blit().</p>
 */
public final class EyeHudOverlay {
    public static final IGuiOverlay INSTANCE = EyeHudOverlay::render;

    private static final ResourceLocation EYE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TheDecayingCode.MOD_ID,
            "textures/gui/eye.png"
    );

    private static final int TEXTURE_SIZE = 16;
    private static final int DISPLAY_SIZE = 32;
    private static final int SCREEN_MARGIN = 8;

    private EyeHudOverlay() {
    }

    private static void render(
            ForgeGui gui,
            GuiGraphics guiGraphics,
            float partialTick,
            int screenWidth,
            int screenHeight
    ) {
        Minecraft minecraft = gui.getMinecraft();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        int x = screenWidth - DISPLAY_SIZE - SCREEN_MARGIN;
        int y = SCREEN_MARGIN;

        gui.setupOverlayRenderState(true, false);
        guiGraphics.blit(
                EYE_TEXTURE,
                x,
                y,
                DISPLAY_SIZE,
                DISPLAY_SIZE,
                0.0F,
                0.0F,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE
        );
    }
}
