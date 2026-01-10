package com.example.emeraldmod.mixin;

import com.example.emeraldmod.item.ModItems;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin untuk PowderSnowBlock
 * Membuat Emerald & Ruby Boots dapat berjalan di atas powder snow
 * seperti Leather Boots - FULL FUNCTIONALITY
 */
@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(
            method = "canWalkOnPowderSnow",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void allowModBootsWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack boots = livingEntity.getEquippedStack(EquipmentSlot.FEET);

            if (!boots.isEmpty()) {
                Item bootsItem = boots.getItem();
                if (bootsItem == ModItems.EMERALD_BOOTS || bootsItem == ModItems.RUBY_BOOTS) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}