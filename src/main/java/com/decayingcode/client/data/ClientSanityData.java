package com.decayingcode.client.data;

import com.decayingcode.sanity.capability.PlayerSanity;
import net.minecraft.util.Mth;

/**
 * Клиентское зеркало серверного значения рассудка.
 *
 * <p>HUD только читает это значение. Изменять настоящий рассудок имеет право
 * исключительно сервер, после чего он присылает SanitySyncS2CPacket.</p>
 */
public final class ClientSanityData {
    private static int sanity = PlayerSanity.DEFAULT_SANITY;

    private ClientSanityData() {
    }

    public static int get() {
        return sanity;
    }

    public static void set(int value) {
        sanity = Mth.clamp(value, PlayerSanity.MIN_SANITY, PlayerSanity.MAX_SANITY);
    }

    public static void reset() {
        sanity = PlayerSanity.DEFAULT_SANITY;
    }
}
