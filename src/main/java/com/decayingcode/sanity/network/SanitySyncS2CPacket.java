package com.decayingcode.sanity.network;

import com.decayingcode.client.data.ClientSanityData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Однонаправленный пакет SERVER -> CLIENT с актуальным значением рассудка.
 */
public record SanitySyncS2CPacket(int sanity) {
    public static void encode(SanitySyncS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.sanity());
    }

    public static SanitySyncS2CPacket decode(FriendlyByteBuf buffer) {
        return new SanitySyncS2CPacket(buffer.readVarInt());
    }

    /**
     * Пакет зарегистрирован через consumerMainThread, поэтому клиентский кэш
     * безопасно меняется на основном потоке рендера.
     */
    public static void handle(
            SanitySyncS2CPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        ClientSanityData.set(packet.sanity());
    }
}
