package com.decayingcode.client.data;

import com.decayingcode.sanity.capability.PlayerSanity;
import net.minecraft.util.Mth;

/**
 * Клиентское зеркало серверного значения рассудка.
 *
 * <p>Будущий HUD должен читать данные отсюда, а не пытаться сам менять capability.
 * Сервер остается единственным источником истины.</p>
 */
public final class ClientSanityData {
    private static int sanity = PlayerSanity.DEFAULT_SANITY;

    private ClientSanityData() {
    }

    public static int getSanity() {
        return sanity;
    }

    public static void setSanity(int value) {
        sanity = Mth.clamp(value, PlayerSanity.MIN_SANITY, PlayerSanity.MAX_SANITY);
    }

    public static void reset() {
        sanity = PlayerSanity.DEFAULT_SANITY;
    }
}
