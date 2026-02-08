package com.example.emeraldmod.event;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.effect.ModEffects;
import com.example.emeraldmod.item.ModItems;
import com.example.emeraldmod.state.EffectStateManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ⭐ FINAL FIX v3: Effect HANYA ada saat wearing armor DAN TIDAK naik kuda
 * ✅ COMPLETE SEPARATION: Skip ALL logic saat riding horse dengan mod armor
 * ✅ Horse handler punya FULL CONTROL saat riding
 */
public class ArmorEffectsHandler {

    private static final Map<UUID, Boolean> previousNegativeImmunityState = new HashMap<>();

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            EffectStateManager stateManager = EffectStateManager.getServerState(server);

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID playerUuid = player.getUuid();

                // ⭐ CRITICAL FIX: Skip SEMUA logic jika sedang naik kuda dengan mod armor
                // Biar HorseArmorEffectsHandler yang FULL CONTROL
                if (isRidingHorseWithModArmor(player)) {
                    // DO NOTHING - Horse handler will manage everything
                    continue;
                }

                boolean armorEffectEnabled = stateManager.isArmorEnabled(playerUuid);

                if (armorEffectEnabled) {
                    // ✅ Effect ON: Apply effects based on current armor
                    boolean hasRubyArmor = hasAnyRubyArmor(player);
                    Boolean previousNegativeImmunity = previousNegativeImmunityState.get(playerUuid);

                    // Clear negative effects when first equipping Ruby Armor
                    if (hasRubyArmor && (previousNegativeImmunity == null || !previousNegativeImmunity)) {
                        removeAllNegativeEffects(player);
                        EmeraldMod.LOGGER.info("Cleared all negative effects from player {} (Negative Immunity activated)",
                                player.getName().getString());
                    }

                    // Apply armor effects (only when NOT riding)
                    applyArmorEffects(player);

                    // Continuously remove negative effects if has Ruby Armor
                    if (hasRubyArmor) {
                        removeAllNegativeEffects(player);
                    }

                    previousNegativeImmunityState.put(playerUuid, hasRubyArmor);
                } else {
                    // ✅ Effect OFF: Remove ALL armor effects EXCEPT SNOW_POWDER_WALKER
                    removeAllArmorEffects(player);

                    // ⭐ ALWAYS ensure SNOW_POWDER_WALKER active if wearing boots (even when toggle OFF)
                    applySnowWalkerOnly(player);

                    previousNegativeImmunityState.put(playerUuid, false);
                }
            }
        });

        EmeraldMod.LOGGER.info("✅ Registered Armor Effects Handler (FINAL FIX v3)");
        EmeraldMod.LOGGER.info("  ⭐ Effects ONLY when wearing armor AND NOT riding");
        EmeraldMod.LOGGER.info("  ⭐ COMPLETE SKIP when riding horse");
        EmeraldMod.LOGGER.info("  ⭐ Horse handler has FULL CONTROL when riding");
        EmeraldMod.LOGGER.info("  ⭐ NO MORE GLITCH - Clean separation of concerns");
    }

    /**
     * ⭐ Check if player is riding horse with mod armor
     */
    private static boolean isRidingHorseWithModArmor(PlayerEntity player) {
        if (!player.hasVehicle()) return false;

        if (player.getVehicle() instanceof HorseEntity horse) {
            ItemStack horseArmor = horse.getBodyArmor();
            return !horseArmor.isEmpty() &&
                    (horseArmor.getItem() == ModItems.EMERALD_HORSE_ARMOR ||
                            horseArmor.getItem() == ModItems.RUBY_HORSE_ARMOR);
        }

        return false;
    }

    /**
     * ⭐ Apply armor effects (ONLY called when NOT riding horse)
     */
    private static void applyArmorEffects(PlayerEntity player) {
        // ===== HELMET: Water Breathing =====
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        Item helmetItem = helmet.getItem();

        if (helmetItem == ModItems.EMERALD_HELMET || helmetItem == ModItems.RUBY_HELMET) {
            if (!player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.WATER_BREATHING,
                        StatusEffectInstance.INFINITE,
                        0, false, false, true
                ));
            }
        } else {
            StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.WATER_BREATHING);
            if (currentEffect != null && isArmorEffect(currentEffect, 0)) {
                player.removeStatusEffect(StatusEffects.WATER_BREATHING);
            }
        }

        // ===== CHESTPLATE: Dolphin's Grace =====
        ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
        Item chestplateItem = chestplate.getItem();

        if (chestplateItem == ModItems.EMERALD_CHESTPLATE || chestplateItem == ModItems.RUBY_CHESTPLATE) {
            if (!player.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.DOLPHINS_GRACE,
                        StatusEffectInstance.INFINITE,
                        0, false, false, true
                ));
            }
        } else {
            StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.DOLPHINS_GRACE);
            if (currentEffect != null && isArmorEffect(currentEffect, 0)) {
                player.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
            }
        }

        // ===== LEGGINGS: Silent Step =====
        ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
        Item leggingsItem = leggings.getItem();

        if (leggingsItem == ModItems.EMERALD_LEGGINGS || leggingsItem == ModItems.RUBY_LEGGINGS) {
            if (!player.hasStatusEffect(ModEffects.SILENT_STEP_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.SILENT_STEP_ENTRY,
                        StatusEffectInstance.INFINITE,
                        0, false, false, true
                ));
            }
        } else {
            if (player.hasStatusEffect(ModEffects.SILENT_STEP_ENTRY)) {
                player.removeStatusEffect(ModEffects.SILENT_STEP_ENTRY);
            }
        }

        // ===== BOOTS: Powder Snow Walker =====
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        Item bootsItem = boots.getItem();

        if (bootsItem == ModItems.EMERALD_BOOTS || bootsItem == ModItems.RUBY_BOOTS) {
            if (!player.hasStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.SNOW_POWDER_WALKER_ENTRY,
                        StatusEffectInstance.INFINITE,
                        0, false, false, true
                ));
            }
        } else {
            if (player.hasStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
                player.removeStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY);
            }
        }

        // ===== FIRE RESISTANCE: Any Mod Armor =====
        if (hasAnyModArmor(player)) {
            if (!player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.FIRE_RESISTANCE,
                        StatusEffectInstance.INFINITE,
                        0, false, false, true
                ));
            }

            // Extinguish fire
            if (player.isOnFire()) {
                player.setFireTicks(0);
            }
        } else {
            StatusEffectInstance currentEffect = player.getStatusEffect(StatusEffects.FIRE_RESISTANCE);
            if (currentEffect != null && isArmorEffect(currentEffect, 0)) {
                player.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
            }
        }

        // ===== NEGATIVE IMMUNITY: Ruby Armor Only =====
        if (hasAnyRubyArmor(player)) {
            if (!player.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.NEGATIVE_IMMUNITY_ENTRY,
                        StatusEffectInstance.INFINITE,
                        0, false, false, true
                ));
            }
        } else {
            if (player.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                player.removeStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY);
            }
        }
    }

    /**
     * ⭐ Detect if effect is from armor
     */
    private static boolean isArmorEffect(StatusEffectInstance effect, int expectedAmplifier) {
        return (effect.isDurationBelow(0) || effect.getDuration() == StatusEffectInstance.INFINITE)
                && effect.getAmplifier() == expectedAmplifier;
    }

    /**
     * ⭐ Apply SNOW_POWDER_WALKER only (when toggle OFF but wearing boots)
     */
    private static void applySnowWalkerOnly(PlayerEntity player) {
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        Item bootsItem = boots.getItem();

        if (bootsItem == ModItems.EMERALD_BOOTS || bootsItem == ModItems.RUBY_BOOTS) {
            if (!player.hasStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.SNOW_POWDER_WALKER_ENTRY,
                        StatusEffectInstance.INFINITE,
                        0, false, false, true
                ));
            }
        } else {
            if (player.hasStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
                player.removeStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY);
            }
        }
    }

    /**
     * ⭐ Remove ALL armor effects when armor effect disabled
     */
    private static void removeAllArmorEffects(PlayerEntity player) {
        // Remove vanilla effects (only if they're armor effects)
        StatusEffectInstance waterBreathing = player.getStatusEffect(StatusEffects.WATER_BREATHING);
        if (waterBreathing != null && isArmorEffect(waterBreathing, 0)) {
            player.removeStatusEffect(StatusEffects.WATER_BREATHING);
        }

        StatusEffectInstance dolphinsGrace = player.getStatusEffect(StatusEffects.DOLPHINS_GRACE);
        if (dolphinsGrace != null && isArmorEffect(dolphinsGrace, 0)) {
            player.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
        }

        StatusEffectInstance fireResistance = player.getStatusEffect(StatusEffects.FIRE_RESISTANCE);
        if (fireResistance != null && isArmorEffect(fireResistance, 0)) {
            player.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        }

        // Remove custom effects (always safe to remove as they're mod-only)
        if (player.hasStatusEffect(ModEffects.SILENT_STEP_ENTRY)) {
            player.removeStatusEffect(ModEffects.SILENT_STEP_ENTRY);
        }

        if (player.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
            player.removeStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY);
        }
    }

    /**
     * Remove all negative effects (for Ruby Armor)
     */
    private static void removeAllNegativeEffects(PlayerEntity player) {
        java.util.List<net.minecraft.registry.entry.RegistryEntry<StatusEffect>> effectsToRemove =
                new java.util.ArrayList<>();

        for (StatusEffectInstance activeEffect : player.getStatusEffects()) {
            StatusEffect statusEffect = activeEffect.getEffectType().value();

            if (statusEffect.getCategory() == StatusEffectCategory.HARMFUL) {
                effectsToRemove.add(activeEffect.getEffectType());
            }
        }

        for (net.minecraft.registry.entry.RegistryEntry<StatusEffect> effectToRemove : effectsToRemove) {
            player.removeStatusEffect(effectToRemove);
        }
    }

    private static boolean hasAnyRubyArmor(PlayerEntity player) {
        for (ItemStack armorStack : player.getArmorItems()) {
            Item armorItem = armorStack.getItem();
            if (armorItem == ModItems.RUBY_HELMET ||
                    armorItem == ModItems.RUBY_CHESTPLATE ||
                    armorItem == ModItems.RUBY_LEGGINGS ||
                    armorItem == ModItems.RUBY_BOOTS) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAnyModArmor(PlayerEntity player) {
        for (ItemStack armorStack : player.getArmorItems()) {
            Item armorItem = armorStack.getItem();

            if (armorItem == ModItems.EMERALD_HELMET ||
                    armorItem == ModItems.EMERALD_CHESTPLATE ||
                    armorItem == ModItems.EMERALD_LEGGINGS ||
                    armorItem == ModItems.EMERALD_BOOTS) {
                return true;
            }

            if (armorItem == ModItems.RUBY_HELMET ||
                    armorItem == ModItems.RUBY_CHESTPLATE ||
                    armorItem == ModItems.RUBY_LEGGINGS ||
                    armorItem == ModItems.RUBY_BOOTS) {
                return true;
            }
        }
        return false;
    }

    public static void clearPlayerState(UUID playerUuid) {
        previousNegativeImmunityState.remove(playerUuid);
    }
}