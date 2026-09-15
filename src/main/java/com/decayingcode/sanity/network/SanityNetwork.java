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
 * SimpleChannel и методы отправки пакетов подсистемы рассудка.
 */
public final class SanityNetwork {
    private static final String PROTOCOL_VERSION = "2";

    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(TheDecayingCode.MOD_ID, "sanity"))
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

        CHANNEL.messageBuilder(SanitySyncS2CPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SanitySyncS2CPacket::encode)
                .decoder(SanitySyncS2CPacket::decode)
                .consumerMainThread(SanitySyncS2CPacket::handle)
                .add();
    }

    /**
     * Отправляет произвольный S2C-пакет рассудка конкретному игроку.
     */
    public static void sendToPlayer(SanitySyncS2CPacket packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /**
     * Читает текущее серверное capability и отправляет его владельцу.
     */
    public static void syncTo(ServerPlayer player) {
        player.getCapability(SanityCapability.INSTANCE).ifPresent(data ->
                sendToPlayer(new SanitySyncS2CPacket(data.getSanity()), player)
        );
    }
}
