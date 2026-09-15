package com.decayingcode.sanity.capability;

import com.decayingcode.TheDecayingCode;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Провайдер прикрепляется к каждому Player через AttachCapabilitiesEvent.
 */
public final class SanityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(TheDecayingCode.MOD_ID, "sanity");

    private final PlayerSanity data = new PlayerSanity();
    private final LazyOptional<PlayerSanity> optional = LazyOptional.of(() -> data);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(
            @NotNull Capability<T> capability,
            @Nullable Direction side
    ) {
        return SanityCapability.INSTANCE.orEmpty(capability, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return data.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        data.deserializeNBT(tag);
    }

    /**
     * Вызывается при уничтожении владельца capability, чтобы не держать старые ссылки.
     */
    public void invalidate() {
        optional.invalidate();
    }
}
