package com.example.emeraldmod.client;

import com.example.emeraldmod.EmeraldMod;
import com.example.emeraldmod.item.EmeraldTotemItem;
import com.example.emeraldmod.item.RubyTotemItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

/**
 * Handler untuk animasi totem pop di client side
 * Memastikan texture yang benar ditampilkan saat totem activate
 */
public class TotemAnimationHandler {

    private static ItemStack lastUsedTotem = ItemStack.EMPTY;
    private static long lastTotemUseTime = 0;

    /**
     * Register client-side entity tracking untuk totem animation
     */
    public static void register() {
        // Track entity status changes (termasuk totem pop animation - status 35)
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof LivingEntity livingEntity) {
                trackTotemUsage(livingEntity);
            }
        });

        EmeraldMod.LOGGER.info("✅ Totem Animation Handler registered");
    }

    /**
     * Track totem usage untuk player
     */
    private static void trackTotemUsage(LivingEntity entity) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Hanya track untuk client player
        if (client.player != null && entity.equals(client.player)) {
            // Cek apakah player baru saja memakai totem
            ItemStack currentTotem = detectCurrentTotem(client.player);

            if (currentTotem != null && !currentTotem.isEmpty()) {
                long currentTime = System.currentTimeMillis();

                // Update last used totem (dengan cooldown 100ms untuk avoid duplicate)
                if (currentTime - lastTotemUseTime > 100) {
                    lastUsedTotem = currentTotem.copy();
                    lastTotemUseTime = currentTime;

                    EmeraldMod.LOGGER.info("[Client] Totem detected: {}",
                            currentTotem.getItem().toString());
                }
            }
        }
    }

    /**
     * Get last used totem (untuk dipakai di mixin)
     */
    public static ItemStack getLastUsedTotem() {
        // Return last used totem jika masih dalam window 5 detik
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTotemUseTime < 5000) {
            return lastUsedTotem.copy();
        }
        return ItemStack.EMPTY;
    }

    /**
     * Detect totem yang sedang di-hold player
     * Priority: Ruby > Emerald > Vanilla
     */
    private static ItemStack detectCurrentTotem(LivingEntity entity) {
        ItemStack mainHand = entity.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHand = entity.getStackInHand(Hand.OFF_HAND);

        // Priority 1: Ruby Totem
        if (RubyTotemItem.isRubyTotem(mainHand)) {
            return mainHand;
        }
        if (RubyTotemItem.isRubyTotem(offHand)) {
            return offHand;
        }

        // Priority 2: Emerald Totem
        if (EmeraldTotemItem.isEmeraldTotem(mainHand)) {
            return mainHand;
        }
        if (EmeraldTotemItem.isEmeraldTotem(offHand)) {
            return offHand;
        }

        // Priority 3: Vanilla Totem
        if (mainHand.isOf(Items.TOTEM_OF_UNDYING)) {
            return mainHand;
        }
        if (offHand.isOf(Items.TOTEM_OF_UNDYING)) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    /**
     * Reset totem tracking (untuk world change / disconnect)
     */
    public static void reset() {
        lastUsedTotem = ItemStack.EMPTY;
        lastTotemUseTime = 0;
        EmeraldMod.LOGGER.info("[Client] Totem animation tracking reset");
    }
}