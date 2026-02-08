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
public class ModArmorMaterial {
    public static final int BASE_DURABILITY = 40;

    public static final TagKey<Item> EMERALD_REPAIR_INGREDIENT = TagKey.of(
            RegistryKeys.ITEM,
            Identifier.of(EmeraldMod.MOD_ID, "emerald_repair_ingredient")
    );

    // ✅ FIXED: Use Identifier for armor layers (1.21.3)
    public static final Identifier EMERALD_LAYER = Identifier.of(EmeraldMod.MOD_ID, "emerald");
    public static final Identifier BASIC_EMERALD_LAYER = Identifier.of(EmeraldMod.MOD_ID, "basic_emerald");

    // ✅ FIXED: ArmorMaterial constructor for 1.21.3:
    // 1. durability multiplier (int)
    // 2. protection values (Map<EquipmentType, Integer>)
    // 3. enchantability (int)
    // 4. equip sound (RegistryEntry<SoundEvent>)
    // 5. toughness (float)
    // 6. knockback resistance (float)
    // 7. repair ingredient tag (TagKey<Item>)
    // 8. armor layer identifier (Identifier) - NOT RegistryKey!
    public static final ArmorMaterial EMERALD_ARMOR_MATERIAL = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    EquipmentType.BOOTS, 5,
                    EquipmentType.LEGGINGS, 8,
                    EquipmentType.CHESTPLATE, 10,
                    EquipmentType.HELMET, 5,
                    EquipmentType.BODY, 22
            ),
            10, // Enchantability (Diamond = 10)
            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
            5.0F, // Toughness (Netherite level)
            0.3F, // Knockback resistance (higher than diamond)
            EMERALD_REPAIR_INGREDIENT,
            EMERALD_LAYER  // ✅ FIXED: Use Identifier directly
    );

    public static final ArmorMaterial BASIC_EMERALD_ARMOR_MATERIAL = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    EquipmentType.BOOTS, 4,
                    EquipmentType.LEGGINGS, 7,
                    EquipmentType.CHESTPLATE, 9,
                    EquipmentType.HELMET, 4,
                    EquipmentType.BODY, 21
            ),
            10, // Enchantability (Diamond = 10)
            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
            5.0F, // Toughness (Netherite level)
            0.3F, // Knockback resistance (higher than diamond)
            EMERALD_REPAIR_INGREDIENT,
            BASIC_EMERALD_LAYER  // ✅ FIXED: Use Identifier directly
    );

    public static void initialize() {
        EmeraldMod.LOGGER.info("Initializing Emerald Armor Materials");
        EmeraldMod.LOGGER.info("  - Base Durability: " + BASE_DURABILITY);
        EmeraldMod.LOGGER.info("  - Enchantability: 10 (Diamond tier)");
        EmeraldMod.LOGGER.info("  - Toughness: 5.0 (Netherite level)");
        EmeraldMod.LOGGER.info("  - Knockback Resistance: 0.3");
        EmeraldMod.LOGGER.info("  - Armor Layer: " + EMERALD_LAYER);
    }
}