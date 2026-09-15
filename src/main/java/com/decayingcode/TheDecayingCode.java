package com.decayingcode;

import com.decayingcode.sanity.network.SanityNetwork;
import net.minecraftforge.fml.common.Mod;

/**
 * Главная точка входа мода The Decaying Code.
 *
 * <p>На первом шаге здесь запускается только сетевой канал системы рассудка.
 * Остальные части мода будут подключаться по мере реализации актов.</p>
 */
@Mod(TheDecayingCode.MOD_ID)
public final class TheDecayingCode {
    public static final String MOD_ID = "decaying_code";

    public TheDecayingCode() {
        SanityNetwork.register();
    }
}
