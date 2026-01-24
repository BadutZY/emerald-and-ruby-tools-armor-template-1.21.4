package com.example.emeraldmod.event;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.item.EmeraldTotemItem;
import com.example.emeraldmod.item.RubyTotemItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

/**
 * Handler untuk Emerald dan Ruby Totem of Undying
 * Mencegah kematian dengan multi-pop system
 */
public class TotemDeathHandler {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            // Only process for players
            if (!(entity instanceof PlayerEntity player)) {
                return true; // Allow death for non-players
            }

            // Check for totems in hands
            ItemStack mainHand = player.getStackInHand(Hand.MAIN_HAND);
            ItemStack offHand = player.getStackInHand(Hand.OFF_HAND);

            ItemStack totemStack = null;
            Hand usedHand = null;

            // Priority: Ruby Totem > Emerald Totem > Vanilla Totem

            // Check main hand first
            if (RubyTotemItem.isRubyTotem(mainHand)) {
                totemStack = mainHand;
                usedHand = Hand.MAIN_HAND;
            } else if (EmeraldTotemItem.isEmeraldTotem(mainHand)) {
                totemStack = mainHand;
                usedHand = Hand.MAIN_HAND;
            } else if (mainHand.isOf(Items.TOTEM_OF_UNDYING)) {
                totemStack = mainHand;
                usedHand = Hand.MAIN_HAND;
            }

            // Check off hand if main hand doesn't have totem
            if (totemStack == null) {
                if (RubyTotemItem.isRubyTotem(offHand)) {
                    totemStack = offHand;
                    usedHand = Hand.OFF_HAND;
                } else if (EmeraldTotemItem.isEmeraldTotem(offHand)) {
                    totemStack = offHand;
                    usedHand = Hand.OFF_HAND;
                } else if (offHand.isOf(Items.TOTEM_OF_UNDYING)) {
                    totemStack = offHand;
                    usedHand = Hand.OFF_HAND;
                }
            }

            // No totem found, allow death
            if (totemStack == null || usedHand == null) {
                return true;
            }

            // Handle totem activation
            if (RubyTotemItem.isRubyTotem(totemStack)) {
                return handleRubyTotem(player, totemStack, usedHand);
            } else if (EmeraldTotemItem.isEmeraldTotem(totemStack)) {
                return handleEmeraldTotem(player, totemStack, usedHand);
            } else if (totemStack.isOf(Items.TOTEM_OF_UNDYING)) {
                // Let vanilla handle normal totem
                return true;
            }

            return true; // Default: allow death
        });

        EmeraldMod.LOGGER.info("✅ Totem Death Handler registered");
    }

    /**
     * Handle Ruby Totem activation (5 uses)
     */
    private static boolean handleRubyTotem(PlayerEntity player, ItemStack totemStack, Hand hand) {
        int remainingUses = RubyTotemItem.getRemainingUses(totemStack);

        if (remainingUses <= 0) {
            return true; // Allow death if no uses left
        }

        // Use one charge
        RubyTotemItem.useCharge(totemStack, player);
        int newRemaining = RubyTotemItem.getRemainingUses(totemStack);

        // Activate totem effects
        activateTotemEffects(player, hand, totemStack);

        // Send message to player
        if (newRemaining > 0) {
            player.sendMessage(
                    Text.literal("Ruby Totem saved you! ")
                            .formatted(Formatting.RED, Formatting.BOLD)
                            .append(Text.literal(newRemaining + "/" + 5 + " uses remaining")
                                    .formatted(Formatting.LIGHT_PURPLE)),
                    true // Action bar
            );

            EmeraldMod.LOGGER.info("Player {} used Ruby Totem ({}/5 remaining)",
                    player.getName().getString(), newRemaining);
        } else {
            player.sendMessage(
                    Text.literal("Ruby Totem depleted!")
                            .formatted(Formatting.RED, Formatting.BOLD),
                    true
            );

            EmeraldMod.LOGGER.info("Player {} depleted Ruby Totem",
                    player.getName().getString());
        }

        // Grant advancement if player is on server
        if (player instanceof ServerPlayerEntity serverPlayer) {
            Criteria.USED_TOTEM.trigger(serverPlayer, totemStack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(totemStack.getItem()));
        }

        return false; // Prevent death
    }

    /**
     * Handle Emerald Totem activation (3 uses)
     */
    private static boolean handleEmeraldTotem(PlayerEntity player, ItemStack totemStack, Hand hand) {
        int remainingUses = EmeraldTotemItem.getRemainingUses(totemStack);

        if (remainingUses <= 0) {
            return true; // Allow death if no uses left
        }

        // Use one charge
        EmeraldTotemItem.useCharge(totemStack, player);
        int newRemaining = EmeraldTotemItem.getRemainingUses(totemStack);

        // Activate totem effects
        activateTotemEffects(player, hand, totemStack);

        // Send message to player
        if (newRemaining > 0) {
            player.sendMessage(
                    Text.literal("Emerald Totem saved you! ")
                            .formatted(Formatting.GREEN, Formatting.BOLD)
                            .append(Text.literal(newRemaining + "/" + 3 + " uses remaining")
                                    .formatted(Formatting.AQUA)),
                    true // Action bar
            );

            EmeraldMod.LOGGER.info("Player {} used Emerald Totem ({}/3 remaining)",
                    player.getName().getString(), newRemaining);
        } else {
            player.sendMessage(
                    Text.literal("Emerald Totem depleted!")
                            .formatted(Formatting.GREEN, Formatting.BOLD),
                    true
            );

            EmeraldMod.LOGGER.info("Player {} depleted Emerald Totem",
                    player.getName().getString());
        }

        // Grant advancement if player is on server
        if (player instanceof ServerPlayerEntity serverPlayer) {
            Criteria.USED_TOTEM.trigger(serverPlayer, totemStack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(totemStack.getItem()));
        }

        return false; // Prevent death
    }

    /**
     * Activate totem effects (same as vanilla totem)
     */
    private static void activateTotemEffects(PlayerEntity player, Hand hand, ItemStack totemStack) {
        // Set health to 1 (like vanilla totem)
        player.setHealth(1.0F);

        // Clear harmful effects
        player.clearStatusEffects();

        // Apply beneficial effects (same as vanilla totem)
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1)); // 45 seconds Regeneration II
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));    // 5 seconds Absorption II
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0)); // 40 seconds Fire Resistance

        // Play totem sound and animation
        player.getWorld().sendEntityStatus(player, (byte) 35); // Totem animation
        player.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0F, 1.0F);
    }
}