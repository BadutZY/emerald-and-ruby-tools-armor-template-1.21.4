package com.example.emeraldmod.event;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.effect.ModEffects;
import com.example.emeraldmod.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * ⭐ FINAL FIX v3: Effect HANYA ada saat riding horse dengan mod armor
 * ✅ SMART CLEANUP: Cek boots player sebelum remove SNOW_POWDER_WALKER
 * ✅ Tidak interfere dengan ArmorEffectsHandler
 */
public class HorseArmorEffectsHandler {

    private static final Identifier SPEED_MODIFIER_ID = Identifier.of(EmeraldMod.MOD_ID, "horse_armor_speed");
    private static final double SPEED_BOOST_AMOUNT = 0.3;

    private static final Set<UUID> CURRENTLY_RIDING_MOD_HORSE = new HashSet<>();

    public static void register() {
        // ⭐ Use END_WORLD_TICK to ensure this runs AFTER ArmorEffectsHandler
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld serverWorld) {
                applyHorseArmorEffects(serverWorld);
            }
        });

        EmeraldMod.LOGGER.info("✅ Registered Horse Armor Effects Handler (FINAL FIX v3)");
        EmeraldMod.LOGGER.info("  ⭐ Effects ONLY when riding horse with armor");
        EmeraldMod.LOGGER.info("  ⭐ SMART cleanup - checks boots before removing effects");
        EmeraldMod.LOGGER.info("  ⭐ NO interference with ArmorEffectsHandler");
    }

    private static void applyHorseArmorEffects(ServerWorld world) {
        Set<UUID> ridingThisTick = new HashSet<>();

        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            if (player.getWorld() != world) continue;

            UUID playerUUID = player.getUuid();
            boolean isRidingModHorse = false;

            if (player.hasVehicle() && player.getVehicle() instanceof HorseEntity horse) {
                ItemStack horseArmor = horse.getBodyArmor();

                if (!horseArmor.isEmpty() &&
                        (horseArmor.getItem() == ModItems.EMERALD_HORSE_ARMOR ||
                                horseArmor.getItem() == ModItems.RUBY_HORSE_ARMOR)) {

                    isRidingModHorse = true;
                    ridingThisTick.add(playerUUID);

                    boolean isRubyArmor = horseArmor.getItem() == ModItems.RUBY_HORSE_ARMOR;

                    // Apply effects to horse
                    applyEffectsToHorse(horse, isRubyArmor);

                    // Clear negative effects from horse if Ruby Armor
                    if (isRubyArmor) {
                        removeAllNegativeEffectsFromHorse(horse);
                    }

                    // Apply effects to rider
                    applyEffectsToRider(player, isRubyArmor);

                    // Handle swimming
                    handleHorseSwimming(horse);
                    handleHorseLavaSwimming(horse);
                }
            }

            // Remove effects if NOT riding mod horse
            if (!isRidingModHorse) {
                if (CURRENTLY_RIDING_MOD_HORSE.contains(playerUUID) || hasAnyHorseEffect(player)) {
                    removeEffectsFromRider(player);
                }
            }
        }

        // Remove effects from horses without mod armor
        world.iterateEntities().forEach(entity -> {
            if (entity instanceof HorseEntity horse) {
                ItemStack armorStack = horse.getBodyArmor();

                if (armorStack.isEmpty() ||
                        (armorStack.getItem() != ModItems.EMERALD_HORSE_ARMOR &&
                                armorStack.getItem() != ModItems.RUBY_HORSE_ARMOR)) {
                    removeEffectsFromHorse(horse);
                }
            }
        });

        CURRENTLY_RIDING_MOD_HORSE.clear();
        CURRENTLY_RIDING_MOD_HORSE.addAll(ridingThisTick);
    }

    private static void applyEffectsToHorse(HorseEntity horse, boolean isRubyArmor) {
        // Speed boost
        EntityAttributeInstance speedAttribute = horse.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null && !speedAttribute.hasModifier(SPEED_MODIFIER_ID)) {
            EntityAttributeModifier speedModifier = new EntityAttributeModifier(
                    SPEED_MODIFIER_ID,
                    SPEED_BOOST_AMOUNT,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
            speedAttribute.addTemporaryModifier(speedModifier);
        }

        // Regeneration
        if (!horse.hasStatusEffect(StatusEffects.REGENERATION)) {
            horse.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION,
                    StatusEffectInstance.INFINITE, 1, false, false, false
            ));
        }

        // Jump Boost
        if (!horse.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            horse.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.JUMP_BOOST,
                    StatusEffectInstance.INFINITE, 1, false, false, false
            ));
        }

        // Resistance
        if (!horse.hasStatusEffect(StatusEffects.RESISTANCE)) {
            horse.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE,
                    StatusEffectInstance.INFINITE, 0, false, false, false
            ));
        }

        // Fire Resistance
        if (!horse.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            horse.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE,
                    StatusEffectInstance.INFINITE, 0, false, false, false
            ));
        }

        // Negative Immunity (Ruby only)
        if (isRubyArmor) {
            if (!horse.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                horse.addStatusEffect(new StatusEffectInstance(
                        ModEffects.NEGATIVE_IMMUNITY_ENTRY,
                        StatusEffectInstance.INFINITE, 0, false, false, false
                ));
            }
        } else {
            if (horse.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                horse.removeStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY);
            }
        }
    }

    private static void removeAllNegativeEffectsFromHorse(HorseEntity horse) {
        java.util.List<net.minecraft.registry.entry.RegistryEntry<StatusEffect>> effectsToRemove =
                new java.util.ArrayList<>();

        for (StatusEffectInstance activeEffect : horse.getStatusEffects()) {
            StatusEffect statusEffect = activeEffect.getEffectType().value();
            if (statusEffect.getCategory() == StatusEffectCategory.HARMFUL) {
                effectsToRemove.add(activeEffect.getEffectType());
            }
        }

        for (net.minecraft.registry.entry.RegistryEntry<StatusEffect> effectToRemove : effectsToRemove) {
            horse.removeStatusEffect(effectToRemove);
        }
    }

    private static void handleHorseSwimming(HorseEntity horse) {
        if (!horse.isTouchingWater()) return;

        Vec3d velocity = horse.getVelocity();
        if (velocity.y < 0.1) {
            horse.setVelocity(
                    velocity.x * 0.7,
                    velocity.y + 0.06,
                    velocity.z * 0.7
            );
            horse.velocityModified = true;
        }

        if (horse.isOnFire()) {
            horse.setFireTicks(0);
        }
    }

    private static void handleHorseLavaSwimming(HorseEntity horse) {
        if (!horse.isInLava()) return;

        Vec3d velocity = horse.getVelocity();
        if (velocity.y < 0.05) {
            horse.setVelocity(
                    velocity.x * 0.6,
                    velocity.y + 0.09,
                    velocity.z * 0.6
            );
            horse.velocityModified = true;
        }

        if (horse.isOnFire()) {
            horse.setFireTicks(0);
        }
    }

    /**
     * ⭐ Apply effects to rider - SIMPLE version
     * Just apply effects, don't force remove/re-apply
     */
    private static void applyEffectsToRider(PlayerEntity player, boolean isRubyArmor) {
        // Speed
        if (!player.hasStatusEffect(StatusEffects.SPEED)) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED,
                    StatusEffectInstance.INFINITE, 1, false, false, true
            ));
        }

        // Regeneration
        if (!player.hasStatusEffect(StatusEffects.REGENERATION)) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION,
                    StatusEffectInstance.INFINITE, 0, false, false, true
            ));
        }

        // Jump Boost
        if (!player.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.JUMP_BOOST,
                    StatusEffectInstance.INFINITE, 1, false, false, true
            ));
        }

        // Resistance
        if (!player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.RESISTANCE,
                    StatusEffectInstance.INFINITE, 0, false, false, true
            ));
        }

        // ⭐ Fire Resistance - Simple check
        if (!player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.FIRE_RESISTANCE,
                    StatusEffectInstance.INFINITE, 0, false, false, true
            ));
        }

        // ⭐ Snow Powder Walker - Simple check
        if (!player.hasStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
            player.addStatusEffect(new StatusEffectInstance(
                    ModEffects.SNOW_POWDER_WALKER_ENTRY,
                    StatusEffectInstance.INFINITE, 0, false, false, true
            ));
        }

        //Simple check
        if (isRubyArmor) {
            if (!player.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.NEGATIVE_IMMUNITY_ENTRY,
                        StatusEffectInstance.INFINITE, 0, false, false, true
                ));
            }
        }

        // Visual indicators (custom effects - always safe)
        if (!player.hasStatusEffect(ModEffects.SWIMMING_HORSE_ENTRY)) {
            player.addStatusEffect(new StatusEffectInstance(
                    ModEffects.SWIMMING_HORSE_ENTRY,
                    StatusEffectInstance.INFINITE, 0, false, false, true
            ));
        }

        if (!player.hasStatusEffect(ModEffects.HORSE_FIRE_ENTRY)) {
            player.addStatusEffect(new StatusEffectInstance(
                    ModEffects.HORSE_FIRE_ENTRY,
                    StatusEffectInstance.INFINITE, 0, false, false, true
            ));
        }
    }

    /**
     * ⭐ CRITICAL FIX: SMART removal - check player's boots before removing SNOW_POWDER_WALKER
     *
     */
    private static void removeEffectsFromRider(PlayerEntity player) {
        // Remove vanilla effects only if they're horse effects (infinite + amp match)
        StatusEffectInstance speed = player.getStatusEffect(StatusEffects.SPEED);
        if (speed != null && isHorseEffect(speed, 1)) {
            player.removeStatusEffect(StatusEffects.SPEED);
        }

        StatusEffectInstance regen = player.getStatusEffect(StatusEffects.REGENERATION);
        if (regen != null && isHorseEffect(regen, 0)) {
            player.removeStatusEffect(StatusEffects.REGENERATION);
        }

        StatusEffectInstance jump = player.getStatusEffect(StatusEffects.JUMP_BOOST);
        if (jump != null && isHorseEffect(jump, 1)) {
            player.removeStatusEffect(StatusEffects.JUMP_BOOST);
        }

        StatusEffectInstance resistance = player.getStatusEffect(StatusEffects.RESISTANCE);
        if (resistance != null && isHorseEffect(resistance, 0)) {
            player.removeStatusEffect(StatusEffects.RESISTANCE);
        }

        // ONLY remove if player doesn't have mod armor
        StatusEffectInstance fireResistance = player.getStatusEffect(StatusEffects.FIRE_RESISTANCE);
        if (fireResistance != null && isHorseEffect(fireResistance, 0) && !hasAnyModArmor(player)) {
            player.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        }

        // ONLY remove if player doesn't have mod boots
        StatusEffectInstance snowWalk = player.getStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY);
        if (snowWalk != null && isHorseEffect(snowWalk, 0) && !hasModBoots(player)) {
            player.removeStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY);
        }

        StatusEffectInstance negativeImmun = player.getStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY);
        if (negativeImmun != null && isHorseEffect(negativeImmun, 0) && !hasAnyModArmor(player)) {
            player.removeStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY);
        }

        // Remove custom horse-specific effects (always safe)
        if (player.hasStatusEffect(ModEffects.SWIMMING_HORSE_ENTRY)) {
            player.removeStatusEffect(ModEffects.SWIMMING_HORSE_ENTRY);
        }
        if (player.hasStatusEffect(ModEffects.HORSE_FIRE_ENTRY)) {
            player.removeStatusEffect(ModEffects.HORSE_FIRE_ENTRY);
        }
    }

    /**
     * ⭐ Check if player has mod boots
     */
    private static boolean hasModBoots(PlayerEntity player) {
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        Item bootsItem = boots.getItem();
        return bootsItem == ModItems.EMERALD_BOOTS || bootsItem == ModItems.RUBY_BOOTS;
    }

    /**
     * ⭐ Check if player has any mod armor
     */
    private static boolean hasAnyModArmor(PlayerEntity player) {
        for (ItemStack armorStack : player.getArmorItems()) {
            Item armorItem = armorStack.getItem();

            if (armorItem == ModItems.EMERALD_HELMET ||
                    armorItem == ModItems.EMERALD_CHESTPLATE ||
                    armorItem == ModItems.EMERALD_LEGGINGS ||
                    armorItem == ModItems.EMERALD_BOOTS ||
                    armorItem == ModItems.RUBY_HELMET ||
                    armorItem == ModItems.RUBY_CHESTPLATE ||
                    armorItem == ModItems.RUBY_LEGGINGS ||
                    armorItem == ModItems.RUBY_BOOTS) {
                return true;
            }
        }
        return false;
    }

    /**
     * ⭐ Detect if effect is from horse (infinite + specific amplifier)
     */
    private static boolean isHorseEffect(StatusEffectInstance effect, int expectedAmplifier) {
        return (effect.isDurationBelow(0) || effect.getDuration() == StatusEffectInstance.INFINITE)
                && effect.getAmplifier() == expectedAmplifier;
    }

    private static boolean hasAnyHorseEffect(PlayerEntity player) {
        return player.hasStatusEffect(ModEffects.SWIMMING_HORSE_ENTRY) ||
                player.hasStatusEffect(ModEffects.HORSE_FIRE_ENTRY);
    }

    private static void removeEffectsFromHorse(HorseEntity horse) {
        // Remove speed modifier
        EntityAttributeInstance speedAttribute = horse.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SPEED_MODIFIER_ID)) {
            speedAttribute.removeModifier(SPEED_MODIFIER_ID);
        }

        // Remove all effects
        if (horse.hasStatusEffect(StatusEffects.REGENERATION)) {
            horse.removeStatusEffect(StatusEffects.REGENERATION);
        }
        if (horse.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            horse.removeStatusEffect(StatusEffects.JUMP_BOOST);
        }
        if (horse.hasStatusEffect(StatusEffects.RESISTANCE)) {
            horse.removeStatusEffect(StatusEffects.RESISTANCE);
        }
        if (horse.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            horse.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        }
        if (horse.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
            horse.removeStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY);
        }
    }

    public static void cleanup() {
        CURRENTLY_RIDING_MOD_HORSE.clear();
    }
}