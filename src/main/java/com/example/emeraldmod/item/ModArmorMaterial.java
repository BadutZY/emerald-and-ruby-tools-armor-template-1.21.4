package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterial {
    public static final RegistryEntry<ArmorMaterial> EMERALD_ARMOR_MATERIAL;
    public static final RegistryEntry<ArmorMaterial> BASIC_EMERALD_ARMOR_MATERIAL;

    static {
        // ✅ FIX: Gunakan defense value yang benar untuk mendapatkan durability yang diinginkan
        // Formula: durability = defense × multiplier
        // Multiplier: Helmet=11, Chestplate=16, Leggings=15, Boots=13, Body=16
        // Untuk mendapatkan durability yang diinginkan: defense = durability / multiplier

        EMERALD_ARMOR_MATERIAL = register("emerald",
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, 40);      // 440 / 11 = 40
                    map.put(ArmorItem.Type.CHESTPLATE, 40);  // 640 / 16 = 40
                    map.put(ArmorItem.Type.LEGGINGS, 40);    // 600 / 15 = 40
                    map.put(ArmorItem.Type.BOOTS, 40);       // 520 / 13 = 40
                    map.put(ArmorItem.Type.BODY, 40);        // 640 / 16 = 40
                }),
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 5);
                    map.put(ArmorItem.Type.LEGGINGS, 8);
                    map.put(ArmorItem.Type.CHESTPLATE, 10);
                    map.put(ArmorItem.Type.HELMET, 5);
                    map.put(ArmorItem.Type.BODY, 22);
                }),
                10,
                SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
                5.0F,
                0.3F,
                () -> Ingredient.ofItems(Items.EMERALD)
        );

        BASIC_EMERALD_ARMOR_MATERIAL = register("basic_emerald",
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, 40);      // 440 / 11 = 40
                    map.put(ArmorItem.Type.CHESTPLATE, 40);  // 640 / 16 = 40
                    map.put(ArmorItem.Type.LEGGINGS, 40);    // 600 / 15 = 40
                    map.put(ArmorItem.Type.BOOTS, 40);       // 520 / 13 = 40
                    map.put(ArmorItem.Type.BODY, 40);        // 640 / 16 = 40
                }),
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 4);
                    map.put(ArmorItem.Type.LEGGINGS, 7);
                    map.put(ArmorItem.Type.CHESTPLATE, 9);
                    map.put(ArmorItem.Type.HELMET, 4);
                    map.put(ArmorItem.Type.BODY, 21);
                }),
                10,
                SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
                5.0F,
                0.3F,
                () -> Ingredient.ofItems(Items.EMERALD)
        );
    }

    private static RegistryEntry<ArmorMaterial> register(
            String id,
            EnumMap<ArmorItem.Type, Integer> defense,
            EnumMap<ArmorItem.Type, Integer> protection,
            int enchantability,
            RegistryEntry<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient
    ) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(Identifier.of(EmeraldMod.MOD_ID, id))
        );

        return Registry.registerReference(
                Registries.ARMOR_MATERIAL,
                Identifier.of(EmeraldMod.MOD_ID, id),
                new ArmorMaterial(defense, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );
    }

    public static void initialize() {
        EmeraldMod.LOGGER.info("Initializing Emerald Armor Materials");
        EmeraldMod.LOGGER.info("  - Base Defense: 40 (same for all pieces)");
        EmeraldMod.LOGGER.info("  - Durability: Helmet=440, Chestplate=640, Leggings=600, Boots=520");
        EmeraldMod.LOGGER.info("  - Enchantability: 10 (Diamond tier)");
        EmeraldMod.LOGGER.info("  - Toughness: 5.0 (Netherite level)");
        EmeraldMod.LOGGER.info("  - Knockback Resistance: 0.3");
    }
}