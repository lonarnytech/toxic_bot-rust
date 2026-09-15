package com.decayingcode.sanity.network;

import com.decayingcode.TheDecayingCode;
import com.decayingcode.sanity.capability.SanityCapability;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Сетевой канал подсистемы рассудка.
 */
public final class SanityNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(TheDecayingCode.MOD_ID, "sanity"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static boolean registered;

    private SanityNetwork() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        CHANNEL.messageBuilder(SanitySyncPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SanitySyncPacket::encode)
                .decoder(SanitySyncPacket::decode)
                .consumerMainThread(SanitySyncPacket::handle)
                .add();
    }

    /**
     * Отправляет значение только владельцу capability: чужой рассудок клиенту не нужен.
     */
    public static void syncTo(ServerPlayer player) {
        player.getCapability(SanityCapability.INSTANCE).ifPresent(data ->
                CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new SanitySyncPacket(data.getSanity())
                )
        );
    }
}
