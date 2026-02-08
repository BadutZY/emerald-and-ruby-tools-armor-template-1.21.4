package com.example.emeraldmod.event;

import com.example.emeraldmod.effect.ModEffects;
import com.example.emeraldmod.item.ModItems;
import com.example.emeraldmod.state.EffectStateManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * ⭐ Tool effects are all CUSTOM effects - no conflict possible
 * Still use INFINITE but only apply when holding tool
 */
public class ToolEffectsHandler {

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            EffectStateManager stateManager = EffectStateManager.getServerState(server);

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (stateManager.isToolsEnabled(player.getUuid())) {
                    applyToolEffects(player);
                } else {
                    removeToolEffects(player);
                }
            }
        });
    }

    private static void applyToolEffects(PlayerEntity player) {
        ItemStack mainHandItem = player.getMainHandStack();
        ItemStack offHandItem = player.getOffHandStack();

        Set<net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect>> activeEffects = new HashSet<>();

        checkAndAddToolEffect(mainHandItem, activeEffects);
        checkAndAddToolEffect(offHandItem, activeEffects);

        // Apply effects
        for (var effect : activeEffects) {
            if (!player.hasStatusEffect(effect)) {
                player.addStatusEffect(new StatusEffectInstance(
                        effect,
                        StatusEffectInstance.INFINITE,
                        0, false, false, true
                ));
            }
        }

        // Remove inactive effects
        removeInactiveToolEffects(player, activeEffects);
    }

    private static void checkAndAddToolEffect(ItemStack itemStack, Set<net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect>> activeEffects) {
        if (itemStack.isEmpty()) return;

        // SWORD
        if (itemStack.getItem() == ModItems.EMERALD_SWORD || itemStack.getItem() == ModItems.RUBY_SWORD) {
            activeEffects.add(ModEffects.SHOCKWAVE_ENTRY);
            if (itemStack.getItem() == ModItems.RUBY_SWORD) {
                activeEffects.add(ModEffects.LIGHTING_SLASH_ENTRY);
            }
        }
        // PICKAXE
        else if (itemStack.getItem() == ModItems.EMERALD_PICKAXE || itemStack.getItem() == ModItems.RUBY_PICKAXE) {
            activeEffects.add(ModEffects.AUTO_SMELT_ENTRY);
            if (itemStack.getItem() == ModItems.RUBY_PICKAXE) {
                activeEffects.add(ModEffects.VEIN_MINING_ENTRY);
            }
        }
        // AXE
        else if (itemStack.getItem() == ModItems.EMERALD_AXE || itemStack.getItem() == ModItems.RUBY_AXE) {
            activeEffects.add(ModEffects.TREE_CHOPPING_ENTRY);
            if (itemStack.getItem() == ModItems.RUBY_AXE) {
                activeEffects.add(ModEffects.AUTO_PLACE_ENTRY);
            }
        }
        // SHOVEL
        else if (itemStack.getItem() == ModItems.EMERALD_SHOVEL || itemStack.getItem() == ModItems.RUBY_SHOVEL) {
            activeEffects.add(ModEffects.ANTI_GRAVITY_ENTRY);
            if (itemStack.getItem() == ModItems.RUBY_SHOVEL) {
                activeEffects.add(ModEffects.FAST_DIGGING_ENTRY);
            }
        }
        // HOE
        else if (itemStack.getItem() == ModItems.EMERALD_HOE || itemStack.getItem() == ModItems.RUBY_HOE) {
            activeEffects.add(ModEffects.AUTO_REPLANT_ENTRY);
            if (itemStack.getItem() == ModItems.RUBY_HOE) {
                activeEffects.add(ModEffects.MORE_HARVEST_ENTRY);
            }
        }
    }

    private static void removeInactiveToolEffects(PlayerEntity player, Set<net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect>> activeEffects) {
        var allToolEffects = new net.minecraft.registry.entry.RegistryEntry[]{
                ModEffects.SHOCKWAVE_ENTRY,
                ModEffects.AUTO_SMELT_ENTRY,
                ModEffects.TREE_CHOPPING_ENTRY,
                ModEffects.ANTI_GRAVITY_ENTRY,
                ModEffects.AUTO_REPLANT_ENTRY,
                ModEffects.VEIN_MINING_ENTRY,
                ModEffects.FAST_DIGGING_ENTRY,
                ModEffects.MORE_HARVEST_ENTRY,
                ModEffects.AUTO_PLACE_ENTRY,
                ModEffects.LIGHTING_SLASH_ENTRY
        };

        for (var effect : allToolEffects) {
            if (!activeEffects.contains(effect) && player.hasStatusEffect(effect)) {
                player.removeStatusEffect(effect);
            }
        }
    }

    private static void removeToolEffects(PlayerEntity player) {
        player.removeStatusEffect(ModEffects.SHOCKWAVE_ENTRY);
        player.removeStatusEffect(ModEffects.AUTO_SMELT_ENTRY);
        player.removeStatusEffect(ModEffects.TREE_CHOPPING_ENTRY);
        player.removeStatusEffect(ModEffects.ANTI_GRAVITY_ENTRY);
        player.removeStatusEffect(ModEffects.AUTO_REPLANT_ENTRY);
        player.removeStatusEffect(ModEffects.VEIN_MINING_ENTRY);
        player.removeStatusEffect(ModEffects.FAST_DIGGING_ENTRY);
        player.removeStatusEffect(ModEffects.MORE_HARVEST_ENTRY);
        player.removeStatusEffect(ModEffects.AUTO_PLACE_ENTRY);
        player.removeStatusEffect(ModEffects.LIGHTING_SLASH_ENTRY);
    }
}