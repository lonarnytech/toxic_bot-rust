package com.decayingcode.sanity.event;

import com.decayingcode.TheDecayingCode;
import com.decayingcode.client.data.ClientSanityData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Клиентский обработчик не дает значению прошлого мира остаться после отключения.
 */
@Mod.EventBusSubscriber(modid = TheDecayingCode.MOD_ID, value = Dist.CLIENT)
public final class ClientSanityEvents {
    private ClientSanityEvents() {
    }

    @SubscribeEvent
    public static void resetOnLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSanityData.reset();
    }
}
