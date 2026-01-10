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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handler untuk armor effects
 * ✅ UPDATED: Snow Powder Walker icon ALWAYS VISIBLE
 *
 * SIMPLE SOLUTION:
 * - Icon ALWAYS apply ketika wearing boots (tidak peduli effect ON/OFF)
 * - Functionality tetap toggleable dengan mixin check boots directly
 */
public class ArmorEffectsHandler {

    private static final Map<UUID, Boolean> previousArmorState = new HashMap<>();
    private static final Map<UUID, Boolean> previousNegativeImmunityState = new HashMap<>();

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            EffectStateManager stateManager = EffectStateManager.getServerState(server);

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID playerUuid = player.getUuid();
                boolean currentState = stateManager.isArmorEnabled(playerUuid);

                if (currentState) {
                    // ✅ Effect ON: Apply armor effects
                    boolean hasRubyArmor = hasAnyRubyArmor(player);
                    Boolean previousNegativeImmunity = previousNegativeImmunityState.get(playerUuid);

                    // Remove negative effects BEFORE applying armor effects
                    if (hasRubyArmor && (previousNegativeImmunity == null || !previousNegativeImmunity)) {
                        removeAllNegativeEffects(player);
                        EmeraldMod.LOGGER.info("Cleared all negative effects from player {} (Negative Immunity activated)",
                                player.getName().getString());
                    }

                    // Apply armor effects
                    applyArmorEffects(player);

                    // EXTRA SAFETY: Always remove negative effects every tick if has Ruby Armor
                    if (hasRubyArmor) {
                        removeAllNegativeEffects(player);
                    }

                    // Update state
                    previousNegativeImmunityState.put(playerUuid, hasRubyArmor);
                } else {
                    // ✅ Effect OFF: Remove toggleable effects only
                    removeToggleableArmorEffects(player);

                    // ⭐ KEEP Snow Powder Walker icon visible (apply visual-only)
                    applySnowWalkerIconOnly(player);

                    // Reset negative immunity state
                    previousNegativeImmunityState.put(playerUuid, false);
                }

                previousArmorState.put(playerUuid, currentState);
            }
        });

        EmeraldMod.LOGGER.info("✅ Registered Armor Effects Handler (Emerald + Ruby Armor ONLY - Toggleable)");
        EmeraldMod.LOGGER.info("  - Helmet: Water Breathing");
        EmeraldMod.LOGGER.info("  - Chestplate: Dolphin's Grace");
        EmeraldMod.LOGGER.info("  - Leggings: Silent Step");
        EmeraldMod.LOGGER.info("  - Boots: Powder Snow Walker (ICON ALWAYS VISIBLE)");
        EmeraldMod.LOGGER.info("  - All Armor: Fire Resistance");
        EmeraldMod.LOGGER.info("  - Ruby Armor: Negative Effect Immunity");
        EmeraldMod.LOGGER.info("  - Basic armor: NO effects");
        EmeraldMod.LOGGER.info("  ⭐ Snow Powder Walker icon will remain visible even when armor effect OFF");
    }

    private static void applyArmorEffects(PlayerEntity player) {
        // ===== HELMET: Water Breathing (Emerald OR Ruby - NON-BASIC) =====
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        Item helmetItem = helmet.getItem();
        if (helmetItem == ModItems.EMERALD_HELMET || helmetItem == ModItems.RUBY_HELMET) {
            if (!hasInfiniteEffect(player, StatusEffects.WATER_BREATHING)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.WATER_BREATHING,
                        StatusEffectInstance.INFINITE, 0, false, false, true
                ));
            }
        } else {
            if (player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                player.removeStatusEffect(StatusEffects.WATER_BREATHING);
            }
        }

        // ===== CHESTPLATE: Dolphin's Grace (Emerald OR Ruby - NON-BASIC) =====
        ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
        Item chestplateItem = chestplate.getItem();
        if (chestplateItem == ModItems.EMERALD_CHESTPLATE || chestplateItem == ModItems.RUBY_CHESTPLATE) {
            if (!hasInfiniteEffect(player, StatusEffects.DOLPHINS_GRACE)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.DOLPHINS_GRACE,
                        StatusEffectInstance.INFINITE, 0, false, false, true
                ));
            }
        } else {
            if (player.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                player.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
            }
        }

        // ===== LEGGINGS: Silent Step (Emerald OR Ruby - NON-BASIC) =====
        ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
        Item leggingsItem = leggings.getItem();
        if (leggingsItem == ModItems.EMERALD_LEGGINGS || leggingsItem == ModItems.RUBY_LEGGINGS) {
            if (!hasInfiniteEffect(player, ModEffects.SILENT_STEP_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.SILENT_STEP_ENTRY,
                        StatusEffectInstance.INFINITE, 0, false, false, true
                ));
            }
        } else {
            if (player.hasStatusEffect(ModEffects.SILENT_STEP_ENTRY)) {
                player.removeStatusEffect(ModEffects.SILENT_STEP_ENTRY);
            }
        }

        // ===== BOOTS: Powder Snow Walker (Emerald OR Ruby - NON-BASIC) =====
        // ⭐ ALWAYS apply with showIcon = TRUE
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        Item bootsItem = boots.getItem();

        boolean isModBoots = (bootsItem == ModItems.EMERALD_BOOTS || bootsItem == ModItems.RUBY_BOOTS);

        if (isModBoots) {
            if (!hasInfiniteEffect(player, ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.SNOW_POWDER_WALKER_ENTRY,
                        StatusEffectInstance.INFINITE,
                        0,      // amplifier
                        false,  // ambient
                        false,  // showParticles
                        true    // ⭐ showIcon - ALWAYS TRUE!
                ));
            }
        } else {
            if (player.hasStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
                player.removeStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY);
            }
        }

        // ===== FIRE RESISTANCE: Any Emerald OR Ruby Armor (NON-BASIC) =====
        if (hasAnyModArmor(player)) {
            if (!hasInfiniteEffect(player, StatusEffects.FIRE_RESISTANCE)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.FIRE_RESISTANCE,
                        StatusEffectInstance.INFINITE, 0, false, false, true
                ));
            }

            if (player.isOnFire()) {
                player.setFireTicks(0);
            }
        } else {
            if (player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                player.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
            }
        }

        // ===== NEGATIVE IMMUNITY: Any Ruby Armor (NON-BASIC) =====
        if (hasAnyRubyArmor(player)) {
            if (!hasInfiniteEffect(player, ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.NEGATIVE_IMMUNITY_ENTRY,
                        StatusEffectInstance.INFINITE, 0, false, false, true
                ));
            }
        } else {
            if (player.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                player.removeStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY);
            }
        }
    }

    /**
     * ⭐ NEW: Apply Snow Powder Walker icon ONLY (visual indicator ketika effect OFF)
     */
    private static void applySnowWalkerIconOnly(PlayerEntity player) {
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        Item bootsItem = boots.getItem();

        boolean isModBoots = (bootsItem == ModItems.EMERALD_BOOTS || bootsItem == ModItems.RUBY_BOOTS);

        if (isModBoots) {
            // Apply effect ONLY untuk show icon
            // Functionality sudah di-handle oleh mixin (check boots directly)
            if (!hasInfiniteEffect(player, ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.SNOW_POWDER_WALKER_ENTRY,
                        StatusEffectInstance.INFINITE,
                        0,      // amplifier
                        false,  // ambient
                        false,  // showParticles
                        true    // ⭐ showIcon - ALWAYS TRUE!
                ));
            }
        } else {
            // Not wearing boots → remove icon
            if (player.hasStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY)) {
                player.removeStatusEffect(ModEffects.SNOW_POWDER_WALKER_ENTRY);
            }
        }
    }

    /**
     * ✅ Remove toggleable armor effects (semua kecuali Snow Powder Walker)
     */
    private static void removeToggleableArmorEffects(PlayerEntity player) {
        // Remove ability effects
        if (player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
            player.removeStatusEffect(StatusEffects.WATER_BREATHING);
        }
        if (player.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
            player.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
        }
        if (player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            player.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
        }
        if (player.hasStatusEffect(ModEffects.SILENT_STEP_ENTRY)) {
            player.removeStatusEffect(ModEffects.SILENT_STEP_ENTRY);
        }
        if (player.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
            player.removeStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY);
        }

        // ⭐ NOTE: Snow Powder Walker NOT removed - icon tetap visible
        // Functionality di-handle oleh mixin yang check boots directly

        // Clear fire ticks
        if (player.getFireTicks() > 0) {
            player.setFireTicks(0);
        }
    }

    /**
     * Remove all negative effects from player
     * Only removes HARMFUL effects, leaves BENEFICIAL effects intact
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

    /**
     * Check if player is wearing ANY Ruby armor piece (NON-BASIC only)
     */
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

    /**
     * Check if player is wearing ANY mod armor (NON-BASIC only)
     */
    private static boolean hasAnyModArmor(PlayerEntity player) {
        for (ItemStack armorStack : player.getArmorItems()) {
            Item armorItem = armorStack.getItem();

            // Check non-Basic Emerald Armor
            if (armorItem == ModItems.EMERALD_HELMET ||
                    armorItem == ModItems.EMERALD_CHESTPLATE ||
                    armorItem == ModItems.EMERALD_LEGGINGS ||
                    armorItem == ModItems.EMERALD_BOOTS) {
                return true;
            }

            // Check non-Basic Ruby Armor
            if (armorItem == ModItems.RUBY_HELMET ||
                    armorItem == ModItems.RUBY_CHESTPLATE ||
                    armorItem == ModItems.RUBY_LEGGINGS ||
                    armorItem == ModItems.RUBY_BOOTS) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasInfiniteEffect(PlayerEntity player, net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect) {
        StatusEffectInstance instance = player.getStatusEffect(effect);
        if (instance == null) return false;
        return instance.isDurationBelow(0) || instance.getDuration() == StatusEffectInstance.INFINITE;
    }

    public static void clearPlayerState(UUID playerUuid) {
        previousArmorState.remove(playerUuid);
        previousNegativeImmunityState.remove(playerUuid);
    }
}