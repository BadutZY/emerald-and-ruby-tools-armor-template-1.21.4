package com.example.emeraldmod.mixin;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.effect.ModEffects;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * ✅ NEW MIXIN: Block splash/lingering potion effects when has Negative Immunity
 * Handles thrown potions (splash and lingering)
 */
@Mixin(PotionEntity.class)
public class PotionEntityMixin {

    /**
     * Inject ke onCollision - dipanggil ketika potion mengenai sesuatu
     */
    @Inject(method = "onCollision", at = @At("HEAD"))
    private void emeraldmod$onPotionHit(HitResult hitResult, CallbackInfo ci) {
        PotionEntity potionEntity = (PotionEntity) (Object) this;

        if (potionEntity.getWorld().isClient()) {
            return; // Only process on server
        }

        // Get potion contents
        net.minecraft.component.type.PotionContentsComponent potionContents =
                potionEntity.getStack().get(net.minecraft.component.DataComponentTypes.POTION_CONTENTS);

        if (potionContents == null) {
            return;
        }

        // Check if potion contains harmful effects
        boolean hasHarmfulEffect = false;

        for (net.minecraft.entity.effect.StatusEffectInstance effect : potionContents.getEffects()) {
            StatusEffect statusEffect = effect.getEffectType().value();

            if (statusEffect.getCategory() == StatusEffectCategory.HARMFUL ||
                    statusEffect == StatusEffects.INSTANT_DAMAGE) {
                hasHarmfulEffect = true;
                break;
            }
        }

        if (!hasHarmfulEffect) {
            return; // Not a harmful potion, allow normal behavior
        }

        // Find all entities in splash radius
        List<LivingEntity> affectedEntities = potionEntity.getWorld().getNonSpectatingEntities(
                LivingEntity.class,
                potionEntity.getBoundingBox().expand(4.0, 2.0, 4.0)
        );

        for (LivingEntity entity : affectedEntities) {
            // Check if entity has Negative Immunity
            if (entity.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                // Calculate distance for splash radius check
                double distance = entity.squaredDistanceTo(potionEntity);

                if (distance < 16.0) { // Within splash radius
                    EmeraldMod.LOGGER.debug("Entity {} is protected from harmful splash potion (Negative Immunity)",
                            entity.getName().getString());
                    // Effect will be blocked by LivingEntityNegativeImmunityMixin
                }
            }
        }
    }
}