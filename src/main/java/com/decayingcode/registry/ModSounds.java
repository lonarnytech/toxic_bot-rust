package com.decayingcode.registry;

import com.decayingcode.TheDecayingCode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

/**
 * Реестр звуков мода.
 *
 * <p>На первом шаге события используют вариации ванильных звуков из sounds.json.
 * Позже их можно заменить собственными OGG-файлами, не меняя Java-код.</p>
 */
@Mod.EventBusSubscriber(
        modid = TheDecayingCode.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class ModSounds {
    public static final ResourceLocation SANITY_WHISPER_ID =
            new ResourceLocation(TheDecayingCode.MOD_ID, "sanity_whisper");
    public static final ResourceLocation SANITY_FOOTSTEPS_ID =
            new ResourceLocation(TheDecayingCode.MOD_ID, "sanity_footsteps");

    public static final SoundEvent SANITY_WHISPER =
            SoundEvent.createVariableRangeEvent(SANITY_WHISPER_ID);
    public static final SoundEvent SANITY_FOOTSTEPS =
            SoundEvent.createVariableRangeEvent(SANITY_FOOTSTEPS_ID);

    private ModSounds() {
    }

    @SubscribeEvent
    public static void registerSounds(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.SOUND_EVENTS, helper -> {
            helper.register(SANITY_WHISPER_ID, SANITY_WHISPER);
            helper.register(SANITY_FOOTSTEPS_ID, SANITY_FOOTSTEPS);
        });
    }
}
