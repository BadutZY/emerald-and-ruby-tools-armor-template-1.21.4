package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Map;

/**
 * ✅ FIXED: Updated for Minecraft 1.21.3
 * - Removed EquipmentAsset and EquipmentAssetKeys (deprecated)
 * - ArmorMaterial now uses Identifier for layers
 */
public class NetheriteArmorMaterial {
    // Netherite durability multiplier base (sama seperti vanilla)
    public static final int BASE_DURABILITY = 37;

    // Tag untuk repair ingredient (Netherite Ingot)
    public static final TagKey<Item> NETHERITE_REPAIR_INGREDIENT = TagKey.of(
            RegistryKeys.ITEM,
            Identifier.of("minecraft", "netherite_ingots")
    );

    // ✅ FIXED: Use Identifier for armor layer (1.21.3)
    public static final Identifier NETHERITE_LAYER = Identifier.of(EmeraldMod.MOD_ID, "netherite");

    // ✅ FIXED: Netherite Horse Armor Material (vanilla stats) - Updated constructor
    public static final ArmorMaterial NETHERITE_HORSE_ARMOR_MATERIAL = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    EquipmentType.BODY, 19  // Horse armor protection value (vanilla netherite equivalent)
            ),
            15, // Enchantability (sama dengan netherite vanilla)
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            3.0F, // Toughness (sama dengan netherite vanilla)
            0.1F, // Knockback resistance (sama dengan netherite vanilla)
            NETHERITE_REPAIR_INGREDIENT,
            NETHERITE_LAYER  // ✅ FIXED: Use Identifier directly
    );

    public static void initialize() {
        EmeraldMod.LOGGER.info("Initializing Netherite Horse Armor Material (Vanilla Stats)");
        EmeraldMod.LOGGER.info("  - Armor Layer: " + NETHERITE_LAYER);
    }
}