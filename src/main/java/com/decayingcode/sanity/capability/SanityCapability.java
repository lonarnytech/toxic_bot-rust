package com.decayingcode.sanity.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * Общая точка доступа к типу capability рассудка.
 */
public final class SanityCapability {
    public static final Capability<PlayerSanity> INSTANCE =
            CapabilityManager.get(new CapabilityToken<>() {
            });

    private SanityCapability() {
    }
}
