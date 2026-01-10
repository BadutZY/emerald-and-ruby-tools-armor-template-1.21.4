package com.example.emeraldmod.effect;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * Status Effect untuk Powder Snow Walker
 * ✅ FIXED: Tanpa override methods yang tidak ada di superclass
 */
public class SnowPowderWalkerEffect extends StatusEffect {

    public SnowPowderWalkerEffect() {
        super(
                StatusEffectCategory.BENEFICIAL, // Kategori beneficial (biru)
                0x87CEEB // Warna sky blue untuk powder snow
        );

        EmeraldMod.LOGGER.info("Initialized SnowPowderWalkerEffect");
        EmeraldMod.LOGGER.info("  - Category: BENEFICIAL");
        EmeraldMod.LOGGER.info("  - Color: 0x87CEEB (Sky Blue)");
        EmeraldMod.LOGGER.info("  - Icon: Will use custom sprite texture");
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Effect tidak memerlukan periodic update
        return false;
    }
}