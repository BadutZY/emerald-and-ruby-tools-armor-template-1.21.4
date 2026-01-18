package com.example.emeraldmod.mixin;

import com.example.emeraldmod.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin untuk membuat Ruby Helmet mencegah Enderman marah
 * sama seperti Carved Pumpkin, tetapi tanpa efek visual overlay
 */
@Mixin(EndermanEntity.class)
public class EndermanAngerMixin {

    /**
     * Hook ke method isPlayerStaring untuk mencegah Enderman marah
     * ketika player memakai Ruby Helmet atau Basic Ruby Helmet
     */
    @Inject(
            method = "isPlayerStaring",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventAngerWithRubyHelmet(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        // Cek apakah player memakai helmet
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);

        if (!helmet.isEmpty()) {
            // Cek apakah helmet adalah Ruby Helmet atau Basic Ruby Helmet
            boolean isRubyHelmet = helmet.getItem() == ModItems.RUBY_HELMET;
            boolean isBasicRubyHelmet = helmet.getItem() == ModItems.BASIC_RUBY_HELMET;

            if (isRubyHelmet || isBasicRubyHelmet) {
                // Return false = Enderman tidak akan marah
                cir.setReturnValue(false);
            }
        }
    }
}