package com.decayingcode.sanity.event;

import com.decayingcode.TheDecayingCode;
import com.decayingcode.registry.ModSounds;
import com.decayingcode.sanity.capability.SanityCapability;
import com.decayingcode.sanity.capability.PlayerSanity;
import com.decayingcode.sanity.network.SanityNetwork;
import com.decayingcode.sanity.network.SanitySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Серверная логика изменения рассудка и пороговых эффектов.
 */
@Mod.EventBusSubscriber(modid = TheDecayingCode.MOD_ID)
public final class SanityTickEvents {
    private static final int UPDATE_INTERVAL_TICKS = 5 * 20;
    private static final int DARKNESS_LIGHT_LIMIT = 4;

    // «Рядом» с факелом/костром: куб 11x7x11 вокруг ног игрока.
    private static final int COMFORT_HORIZONTAL_RADIUS = 5;
    private static final int COMFORT_VERTICAL_RADIUS = 3;

    private static final int UNSETTLING_SANITY_THRESHOLD = 50;
    private static final int BLINDNESS_SANITY_THRESHOLD = 20;
    private static final int BLINDNESS_DURATION_TICKS = 2 * 20;
    private static final float AMBIENT_SOUND_CHANCE = 0.25F;

    private SanityTickEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayer player)
                || !player.isAlive()
                || player.isSpectator()
                || player.tickCount % UPDATE_INTERVAL_TICKS != 0) {
            return;
        }

        ServerLevel level = player.serverLevel();
        BlockPos playerPos = player.blockPosition();

        player.getCapability(SanityCapability.INSTANCE).ifPresent(sanityData -> {
            boolean changed = updateSanity(level, playerPos, sanityData);
            int playerSanity = sanityData.getSanity();

            if (changed) {
                // Каждое серверное изменение немедленно попадает в клиентский кэш HUD.
                SanityNetwork.sendToPlayer(
                        new SanitySyncS2CPacket(playerSanity),
                        player
                );
            }

            applyThresholdEffects(player, playerSanity);
        });
    }

    private static boolean updateSanity(
            ServerLevel level,
            BlockPos playerPos,
            PlayerSanity sanityData
    ) {
        // Восстановление приоритетнее потери: это важно для темной пещеры с костром.
        if (isDayUnderOpenSky(level, playerPos) || hasComfortSourceNearby(level, playerPos)) {
            return sanityData.addSanity(2);
        }

        int combinedLight = level.getMaxLocalRawBrightness(playerPos);
        if (combinedLight < DARKNESS_LIGHT_LIMIT) {
            return sanityData.addSanity(-1);
        }

        return false;
    }

    private static boolean isDayUnderOpenSky(ServerLevel level, BlockPos playerPos) {
        return level.isDay() && level.canSeeSky(playerPos.above());
    }

    private static boolean hasComfortSourceNearby(ServerLevel level, BlockPos center) {
        BlockPos min = center.offset(
                -COMFORT_HORIZONTAL_RADIUS,
                -COMFORT_VERTICAL_RADIUS,
                -COMFORT_HORIZONTAL_RADIUS
        );
        BlockPos max = center.offset(
                COMFORT_HORIZONTAL_RADIUS,
                COMFORT_VERTICAL_RADIUS,
                COMFORT_HORIZONTAL_RADIUS
        );

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);
            if (isTorch(state) || isLitCampfire(state)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTorch(BlockState state) {
        return state.is(Blocks.TORCH)
                || state.is(Blocks.WALL_TORCH)
                || state.is(Blocks.SOUL_TORCH)
                || state.is(Blocks.SOUL_WALL_TORCH);
    }

    private static boolean isLitCampfire(BlockState state) {
        return state.is(BlockTags.CAMPFIRES)
                && state.hasProperty(BlockStateProperties.LIT)
                && state.getValue(BlockStateProperties.LIT);
    }

    private static void applyThresholdEffects(ServerPlayer player, int sanity) {
        if (sanity < UNSETTLING_SANITY_THRESHOLD) {
            tryPlayUnsettlingSound(player);
        }

        if (sanity < BLINDNESS_SANITY_THRESHOLD) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS,
                    BLINDNESS_DURATION_TICKS,
                    0,
                    true,
                    false,
                    true
            ));
        }
    }

    private static void tryPlayUnsettlingSound(ServerPlayer player) {
        RandomSource random = player.getRandom();
        if (random.nextFloat() >= AMBIENT_SOUND_CHANCE) {
            return;
        }

        SoundEvent sound = random.nextBoolean()
                ? ModSounds.SANITY_WHISPER
                : ModSounds.SANITY_FOOTSTEPS;
        float pitch = 0.85F + random.nextFloat() * 0.25F;

        // playNotifySound отправляет звук только конкретному ServerPlayer.
        player.playNotifySound(sound, SoundSource.AMBIENT, 0.7F, pitch);
    }
}
