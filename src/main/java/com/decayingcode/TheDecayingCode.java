package com.decayingcode;

import com.decayingcode.client.ClientModEvents;
import com.decayingcode.sanity.network.SanityNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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

        // Клиентский класс не должен загружаться на dedicated server.
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientModEvents::register);
    }
}
