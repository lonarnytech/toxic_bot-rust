package com.decayingcode.client;

import com.decayingcode.client.gui.SanityHudOverlay;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Клиентская точка регистрации. Класс загружается только на физическом клиенте
 * через DistExecutor из главного класса мода.
 */
public final class ClientModEvents {
    private ClientModEvents() {
    }

    /**
     * Подписывает клиентские события именно на MOD event bus Forge 1.20.1.
     */
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientModEvents::registerGuiOverlays);
    }

    private static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        // Поверх всех ванильных элементов, чтобы глаз не перекрывался другим HUD.
        event.registerAboveAll("sanity", SanityHudOverlay.INSTANCE);
    }
}
