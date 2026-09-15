package com.decayingcode.client.gui;

import com.decayingcode.TheDecayingCode;
import com.decayingcode.client.data.ClientSanityData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Клиентский HUD рассудка для Forge 1.20.1.
 *
 * <p>Оверлей не читает capability игрока: клиентское значение приходит с
 * сервера в SanitySyncS2CPacket и хранится в ClientSanityData.</p>
 */
public final class SanityHudOverlay {
    public static final IGuiOverlay INSTANCE = SanityHudOverlay::render;

    private static final ResourceLocation EYE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TheDecayingCode.MOD_ID,
            "textures/gui/eye.png"
    );

    private static final int TEXTURE_SIZE = 16;
    private static final int DISPLAY_SIZE = 32;
    private static final int SCREEN_MARGIN = 8;

    private SanityHudOverlay() {
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

        int sanity = ClientSanityData.get();
        float corruption = 1.0F - sanity / 100.0F;
        long animationTime = Util.getMillis();

        int baseX = screenWidth - DISPLAY_SIZE - SCREEN_MARGIN;
        int baseY = SCREEN_MARGIN;
        int shakeX = calculateShake(animationTime, corruption, sanity, true);
        int shakeY = calculateShake(animationTime, corruption, sanity, false);

        gui.setupOverlayRenderState(true, false);

        // После первой потерянной единицы белок глаза начинает краснеть.
        float pulse = 0.5F + 0.5F * (float) Math.sin(animationTime / 140.0D);
        float redness = sanity >= 100
                ? 0.0F
                : Mth.clamp(0.18F + corruption * 0.72F + pulse * corruption * 0.10F, 0.0F, 1.0F);
        float greenBlue = 1.0F - redness * 0.9F;

        RenderSystem.setShaderColor(1.0F, greenBlue, greenBlue, 1.0F);
        guiGraphics.blit(
                EYE_TEXTURE,
                baseX + shakeX,
                baseY + shakeY,
                DISPLAY_SIZE,
                DISPLAY_SIZE,
                0.0F,
                0.0F,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Число под глазом позволяет сразу проверить приход S2C-пакетов.
        String sanityText = Integer.toString(sanity);
        int textX = baseX + (DISPLAY_SIZE - minecraft.font.width(sanityText)) / 2;
        int colorPart = Mth.clamp(Math.round(255.0F * (1.0F - corruption)), 0, 255);
        int textColor = 0xFFFF0000 | colorPart << 8 | colorPart;
        guiGraphics.drawString(
                minecraft.font,
                sanityText,
                textX,
                baseY + DISPLAY_SIZE + 2,
                textColor,
                true
        );
    }

    private static int calculateShake(
            long animationTime,
            float corruption,
            int sanity,
            boolean horizontal
    ) {
        if (sanity >= 100) {
            return 0;
        }

        // Даже 99 единиц дают заметный сдвиг в один пиксель; чем ниже значение,
        // тем выше амплитуда и частота дрожания.
        float strength = 0.75F + corruption * 3.25F;
        double axisOffset = horizontal ? 0.0D : 1.7D;
        double primary = Math.sin(animationTime * (0.018D + corruption * 0.030D) + axisOffset);
        double secondary = Math.sin(animationTime * 0.043D + axisOffset * 2.3D) * 0.45D;
        return (int) Math.round((primary + secondary) * strength);
    }
}
