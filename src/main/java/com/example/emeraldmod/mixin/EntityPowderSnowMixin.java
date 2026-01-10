package com.example.emeraldmod.mixin;

import com.example.emeraldmod.item.ModItems;
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
 * Mixin untuk Entity class
 * Mencegah entity tenggelam ke dalam powder snow
 * ketika memakai Emerald atau Ruby Boots
 */
@Mixin(Entity.class)
public class EntityPowderSnowMixin {

    /**
     * Hook ke method canFreeze
     * Method ini menentukan apakah entity bisa freeze di powder snow
     */
    @Inject(
            method = "canFreeze",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventFreezeWithModBoots(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;

        // Cek apakah entity adalah LivingEntity
        if (entity instanceof LivingEntity livingEntity) {
            // Ambil boots yang sedang dipakai
            ItemStack boots = livingEntity.getEquippedStack(EquipmentSlot.FEET);
            Item bootsItem = boots.getItem();

            // Cek apakah boots adalah Emerald atau Ruby Boots (NON-BASIC)
            if (bootsItem == ModItems.EMERALD_BOOTS || bootsItem == ModItems.RUBY_BOOTS) {
                // Return false - entity TIDAK bisa freeze
                cir.setReturnValue(false);
            }
        }
    }
}