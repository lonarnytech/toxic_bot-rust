package com.decayingcode.sanity.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;

/**
 * Серверное состояние рассудка одного игрока.
 *
 * <p>Capability-провайдер сериализует этот объект в NBT игрока, поэтому значение
 * переживает сохранение мира и повторный вход. Любое входящее значение жестко
 * ограничивается диапазоном 0..100.</p>
 */
public final class PlayerSanity {
    public static final int MIN_SANITY = 0;
    public static final int MAX_SANITY = 100;
    public static final int DEFAULT_SANITY = MAX_SANITY;

    private static final String NBT_SANITY = "Sanity";

    private int sanity = DEFAULT_SANITY;

    public int getSanity() {
        return sanity;
    }

    /**
     * @return {@code true}, если значение действительно изменилось.
     */
    public boolean setSanity(int value) {
        int clampedValue = Mth.clamp(value, MIN_SANITY, MAX_SANITY);
        if (sanity == clampedValue) {
            return false;
        }

        sanity = clampedValue;
        return true;
    }

    public boolean addSanity(int amount) {
        return setSanity(sanity + amount);
    }

    public void copyFrom(PlayerSanity source) {
        setSanity(source.getSanity());
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_SANITY, sanity);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        // Старое сохранение без поля Sanity должно получить безопасный дефолт 100.
        if (tag.contains(NBT_SANITY, Tag.TAG_INT)) {
            setSanity(tag.getInt(NBT_SANITY));
        } else {
            setSanity(DEFAULT_SANITY);
        }
    }
}
