package com.example.emeraldmod.event;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.effect.ModEffects;
import com.example.emeraldmod.item.ModItems;
import com.example.emeraldmod.state.EffectStateManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * ✅ COMPLETE SOLUTION: Fire + Magic damage prevention
 * Blocks:
 * - Fire damage (with armor toggle)
 * - Magic damage from potions (Instant Damage/Harming) when has Negative Immunity
 * - Works for Player AND Horse
 *
 * NO EXTRA MIXIN NEEDED - Just update this file!
 */
public class DamagePreventionHandler {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            // ===== PLAYER DAMAGE PREVENTION =====
            if (entity instanceof ServerPlayerEntity player) {
                EffectStateManager stateManager = EffectStateManager.getServerState(player.getServer());

                // ✅ CHECK 1: Fire Damage Prevention (requires armor effect ON)
                if (stateManager.isArmorEnabled(player.getUuid())) {
                    if (hasModArmor(player) && isFireDamage(source)) {
                        EmeraldMod.LOGGER.debug("Cancelled fire damage for player {} (armor effect enabled)",
                                player.getName().getString());
                        return false; // Cancel fire damage
                    }
                }

                // ✅ CHECK 2: Magic Damage Prevention (Instant Damage from potions)
                // This works even if armor effect is OFF, as long as player has Negative Immunity effect
                if (player.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                    if (isMagicDamage(source)) {
                        EmeraldMod.LOGGER.info("✅ Cancelled magic damage for player {} (Negative Immunity)",
                                player.getName().getString());
                        return false; // Cancel magic damage (Instant Damage potion)
                    }

                    // ✅ CHECK 3: Generic harmful damage from unknown sources
                    // This catches edge cases like direct potion application
                    if (isGenericHarmfulDamage(source)) {
                        EmeraldMod.LOGGER.info("✅ Cancelled generic harmful damage for player {} (Negative Immunity)",
                                player.getName().getString());
                        return false;
                    }
                }
            }

            // ===== HORSE DAMAGE PREVENTION (Ruby Horse Armor) =====
            if (entity instanceof HorseEntity horse) {
                ItemStack horseArmor = horse.getBodyArmor();

                // Check if horse has Ruby Horse Armor
                if (!horseArmor.isEmpty() && horseArmor.getItem() == ModItems.RUBY_HORSE_ARMOR) {
                    // Block fire damage for horse
                    if (isFireDamage(source)) {
                        EmeraldMod.LOGGER.debug("Cancelled fire damage for horse (Ruby Horse Armor)");
                        return false;
                    }

                    // ✅ Block magic damage (Instant Damage potion) for horse
                    if (isMagicDamage(source)) {
                        EmeraldMod.LOGGER.info("✅ Cancelled magic damage for horse (Ruby Horse Armor)");
                        return false;
                    }

                    // ✅ Block generic harmful damage for horse
                    if (isGenericHarmfulDamage(source)) {
                        EmeraldMod.LOGGER.info("✅ Cancelled generic harmful damage for horse (Ruby Horse Armor)");
                        return false;
                    }
                }

                // Also check if horse has Negative Immunity effect (from Ruby Horse Armor)
                if (horse.hasStatusEffect(ModEffects.NEGATIVE_IMMUNITY_ENTRY)) {
                    if (isMagicDamage(source)) {
                        EmeraldMod.LOGGER.info("✅ Cancelled magic damage for horse (Negative Immunity effect)");
                        return false;
                    }

                    if (isGenericHarmfulDamage(source)) {
                        EmeraldMod.LOGGER.info("✅ Cancelled generic harmful damage for horse (Negative Immunity effect)");
                        return false;
                    }
                }
            }

            return true; // Allow damage
        });

        EmeraldMod.LOGGER.info("✅ Registered Damage Prevention Handler");
        EmeraldMod.LOGGER.info("  - Fire Damage: Blocked for Emerald + Ruby Armor (toggleable)");
        EmeraldMod.LOGGER.info("  - Magic Damage: Blocked when has Negative Immunity (Instant Damage potion)");
        EmeraldMod.LOGGER.info("  - Generic Harmful: Blocked when has Negative Immunity");
        EmeraldMod.LOGGER.info("  - Horse: Protected when wearing Ruby Horse Armor");
    }

    /**
     * ✅ Check if damage is from fire sources
     */
    private static boolean isFireDamage(DamageSource source) {
        return source.isIn(DamageTypeTags.IS_FIRE) ||
                source.isOf(DamageTypes.IN_FIRE) ||
                source.isOf(DamageTypes.ON_FIRE) ||
                source.isOf(DamageTypes.LAVA) ||
                source.isOf(DamageTypes.HOT_FLOOR);
    }

    /**
     * ✅ Check if damage is from magic sources (potions like Instant Damage/Harming)
     * This covers:
     * - Splash potions (thrown)
     * - Drinkable potions (bottle)
     * - Lingering potions (area effect cloud)
     */
    private static boolean isMagicDamage(DamageSource source) {
        // Check for magic damage type (used by Instant Damage potions)
        if (source.isOf(DamageTypes.MAGIC)) {
            return true;
        }

        // Check for indirect magic (splash/lingering potions)
        if (source.isOf(DamageTypes.INDIRECT_MAGIC)) {
            return true;
        }

        // Additional check: damage from any magic-related source
        if (source.isIn(DamageTypeTags.WITCH_RESISTANT_TO)) {
            return true;
        }

        return false;
    }

    /**
     * ✅ NEW: Check if damage is generic harmful damage that should be blocked
     * This catches edge cases like:
     * - Potion effects that bypass magic check
     * - Direct damage application from unknown sources
     * - Modded damage types
     */
    private static boolean isGenericHarmfulDamage(DamageSource source) {
        // Don't block these damage types (should be allowed)
        if (source.isOf(DamageTypes.FALL) ||
                source.isOf(DamageTypes.FLY_INTO_WALL) ||
                source.isOf(DamageTypes.DROWN) ||
                source.isOf(DamageTypes.STARVE) ||
                source.isOf(DamageTypes.GENERIC) ||
                source.isOf(DamageTypes.OUT_OF_WORLD) ||
                source.isOf(DamageTypes.GENERIC_KILL)) {
            return false; // These should still damage (environmental)
        }

        // Check if damage source has attacker but is not player-caused
        // This can catch potion damage that's applied differently
        if (source.getAttacker() == null && !source.isOf(DamageTypes.PLAYER_ATTACK)) {
            // Check if it's potentially from a potion or other harmful source
            DamageType damageType = source.getType();
            String damageTypeName = damageType.msgId();

            // Log for debugging
            if (!damageTypeName.contains("fall") &&
                    !damageTypeName.contains("drown") &&
                    !damageTypeName.contains("starve")) {
                EmeraldMod.LOGGER.debug("Checking generic damage type: {}", damageTypeName);

                // If it's not environmental damage, consider blocking it
                return !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY);
            }
        }

        return false;
    }

    /**
     * Check only for non-Basic armor items directly
     * Basic armor does NOT get fire protection
     */
    private static boolean hasModArmor(PlayerEntity player) {
        for (ItemStack armorStack : player.getArmorItems()) {
            Item armorItem = armorStack.getItem();

            // Check for non-Basic Emerald armor
            if (armorItem == ModItems.EMERALD_HELMET ||
                    armorItem == ModItems.EMERALD_CHESTPLATE ||
                    armorItem == ModItems.EMERALD_LEGGINGS ||
                    armorItem == ModItems.EMERALD_BOOTS) {
                return true;
            }

            // Check for non-Basic Ruby armor
            if (armorItem == ModItems.RUBY_HELMET ||
                    armorItem == ModItems.RUBY_CHESTPLATE ||
                    armorItem == ModItems.RUBY_LEGGINGS ||
                    armorItem == ModItems.RUBY_BOOTS) {
                return true;
            }
        }
        return false;
    }
}