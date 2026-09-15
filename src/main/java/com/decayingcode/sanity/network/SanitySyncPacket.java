package com.decayingcode.sanity.network;

import com.decayingcode.client.data.ClientSanityData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Однонаправленный пакет SERVER -> CLIENT с актуальным значением рассудка.
 */
public record SanitySyncPacket(int sanity) {
    public static void encode(SanitySyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.sanity);
    }

    public static SanitySyncPacket decode(FriendlyByteBuf buffer) {
        return new SanitySyncPacket(buffer.readVarInt());
    }

    /**
     * consumerMainThread гарантирует вызов обработчика на главном клиентском потоке.
     */
    public static void handle(
            SanitySyncPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        ClientSanityData.setSanity(packet.sanity);
    }
}
