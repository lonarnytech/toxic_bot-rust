package com.decayingcode.sanity.event;

import com.decayingcode.TheDecayingCode;
import com.decayingcode.sanity.capability.SanityCapability;
import com.decayingcode.sanity.capability.SanityProvider;
import com.decayingcode.sanity.network.SanityNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Жизненный цикл capability: прикрепление, копирование при пересоздании Player
 * и начальная синхронизация после входа/респавна/смены измерения.
 */
@Mod.EventBusSubscriber(modid = TheDecayingCode.MOD_ID)
public final class SanityCapabilityEvents {
    private SanityCapabilityEvents() {
    }

    @SubscribeEvent
    public static void attachToPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player player)
                || player.getCapability(SanityCapability.INSTANCE).isPresent()) {
            return;
        }

        SanityProvider provider = new SanityProvider();
        event.addCapability(SanityProvider.ID, provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public static void copyOnPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player clone = event.getEntity();

        // К моменту Clone старые capability уже могут быть инвалидированы Forge.
        original.reviveCaps();
        original.getCapability(SanityCapability.INSTANCE).ifPresent(oldData ->
                clone.getCapability(SanityCapability.INSTANCE).ifPresent(newData ->
                        newData.copyFrom(oldData)
                )
        );
        original.invalidateCaps();

        if (clone instanceof ServerPlayer serverPlayer) {
            SanityNetwork.syncTo(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void syncOnLogin(PlayerEvent.PlayerLoggedInEvent event) {
        sync(event.getEntity());
    }

    @SubscribeEvent
    public static void syncOnRespawn(PlayerEvent.PlayerRespawnEvent event) {
        sync(event.getEntity());
    }

    @SubscribeEvent
    public static void syncOnDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        sync(event.getEntity());
    }

    private static void sync(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            SanityNetwork.syncTo(serverPlayer);
        }
    }
}
