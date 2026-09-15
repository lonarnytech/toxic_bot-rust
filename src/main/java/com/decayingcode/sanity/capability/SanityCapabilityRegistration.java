package com.decayingcode.sanity.capability;

import com.decayingcode.TheDecayingCode;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Регистрация типа capability выполняется на MOD event bus.
 */
@Mod.EventBusSubscriber(
        modid = TheDecayingCode.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class SanityCapabilityRegistration {
    private SanityCapabilityRegistration() {
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerSanity.class);
    }
}
