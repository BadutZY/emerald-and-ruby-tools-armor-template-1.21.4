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
public class RubyArmorMaterial {
    // Set base durability ke 1 karena kita akan override di item class
    // Ini untuk menghindari durability bar yang sangat panjang
    public static final int BASE_DURABILITY = 1;

    public static final TagKey<Item> RUBY_REPAIR_INGREDIENT = TagKey.of(
            RegistryKeys.ITEM,
            Identifier.of(EmeraldMod.MOD_ID, "ruby_repair_ingredient")
    );

    // ✅ FIXED: Use Identifier for armor layers (1.21.3)
    public static final Identifier RUBY_LAYER = Identifier.of(EmeraldMod.MOD_ID, "ruby");
    public static final Identifier BASIC_RUBY_LAYER = Identifier.of(EmeraldMod.MOD_ID, "basic_ruby");

    // ✅ FIXED: Ruby Armor Material - SUPERIOR TO EMERALD
    // ArmorMaterial constructor for 1.21.3:
    // 1. durability multiplier (int)
    // 2. protection values (Map<EquipmentType, Integer>)
    // 3. enchantability (int)
    // 4. equip sound (RegistryEntry<SoundEvent>)
    // 5. toughness (float)
    // 6. knockback resistance (float)
    // 7. repair ingredient tag (TagKey<Item>)
    // 8. armor layer identifier (Identifier) - NOT RegistryKey!
    public static final ArmorMaterial RUBY_ARMOR_MATERIAL = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    EquipmentType.BOOTS, 6,        // Lebih tinggi dari Emerald (5)
                    EquipmentType.LEGGINGS, 10,    // Lebih tinggi dari Emerald (8)
                    EquipmentType.CHESTPLATE, 12,  // Lebih tinggi dari Emerald (10)
                    EquipmentType.HELMET, 6,       // Lebih tinggi dari Emerald (5)
                    EquipmentType.BODY, 25         // Lebih tinggi dari Emerald (22)
            ),
            15, // Enchantability (lebih baik dari Diamond/Emerald 10)
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            6.0F, // Toughness (lebih tinggi dari Emerald 5.0)
            0.4F, // Knockback resistance (lebih tinggi dari Emerald 0.3)
            RUBY_REPAIR_INGREDIENT,
            RUBY_LAYER  // ✅ FIXED: Use Identifier directly
    );

    public static final ArmorMaterial BASIC_RUBY_ARMOR_MATERIAL = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    EquipmentType.BOOTS, 5,        // Same as Emerald
                    EquipmentType.LEGGINGS, 8,     // Same as Emerald
                    EquipmentType.CHESTPLATE, 10,  // Same as Emerald
                    EquipmentType.HELMET, 5,       // Same as Emerald
                    EquipmentType.BODY, 22         // Same as Emerald
            ),
            10, // Enchantability (same as Diamond/Emerald)
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            5.0F, // Toughness (same as Emerald)
            0.3F, // Knockback resistance (same as Emerald)
            RUBY_REPAIR_INGREDIENT,
            BASIC_RUBY_LAYER  // ✅ FIXED: Use Identifier directly
    );

    public static void initialize() {
        EmeraldMod.LOGGER.info("Initializing Ruby Armor Materials");
        EmeraldMod.LOGGER.info("  - Base Durability: 1 (Will be overridden to UNBREAKABLE in item class)");
        EmeraldMod.LOGGER.info("  - Enchantability: 15 (Superior to Diamond/Emerald)");
        EmeraldMod.LOGGER.info("  - Toughness: 6.0 (Superior to Emerald 5.0)");
        EmeraldMod.LOGGER.info("  - Knockback Resistance: 0.4 (Superior to Emerald 0.3)");
        EmeraldMod.LOGGER.info("  - Protection: Superior to all existing armors");
        EmeraldMod.LOGGER.info("  - Armor Layer: " + RUBY_LAYER);
    }
}