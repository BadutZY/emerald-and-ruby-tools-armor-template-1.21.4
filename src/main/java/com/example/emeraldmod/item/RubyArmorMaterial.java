package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
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

public class RubyArmorMaterial {
    public static final RegistryEntry<ArmorMaterial> RUBY_ARMOR_MATERIAL;
    public static final RegistryEntry<ArmorMaterial> BASIC_RUBY_ARMOR_MATERIAL;

    static {
        RUBY_ARMOR_MATERIAL = register("ruby",
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, 11);
                    map.put(ArmorItem.Type.CHESTPLATE, 16);
                    map.put(ArmorItem.Type.LEGGINGS, 15);
                    map.put(ArmorItem.Type.BOOTS, 13);
                    map.put(ArmorItem.Type.BODY, 16);
                }),
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 6);
                    map.put(ArmorItem.Type.LEGGINGS, 10);
                    map.put(ArmorItem.Type.CHESTPLATE, 12);
                    map.put(ArmorItem.Type.HELMET, 6);
                    map.put(ArmorItem.Type.BODY, 25);
                }),
                15,
                SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
                6.0F,
                0.4F,
                () -> Ingredient.ofItems(ModItems.RUBY)
        );

        BASIC_RUBY_ARMOR_MATERIAL = register("basic_ruby",
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, 11);
                    map.put(ArmorItem.Type.CHESTPLATE, 16);
                    map.put(ArmorItem.Type.LEGGINGS, 15);
                    map.put(ArmorItem.Type.BOOTS, 13);
                    map.put(ArmorItem.Type.BODY, 16);
                }),
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 5);
                    map.put(ArmorItem.Type.LEGGINGS, 8);
                    map.put(ArmorItem.Type.CHESTPLATE, 10);
                    map.put(ArmorItem.Type.HELMET, 5);
                    map.put(ArmorItem.Type.BODY, 22);
                }),
                10,
                SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
                5.0F,
                0.3F,
                () -> Ingredient.ofItems(ModItems.RUBY)
        );
    }

    private static RegistryEntry<ArmorMaterial> register(
            String id,
            EnumMap<ArmorItem.Type, Integer> durability,
            EnumMap<ArmorItem.Type, Integer> defense,
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
                new ArmorMaterial(durability, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );
    }

    public static void initialize() {
        EmeraldMod.LOGGER.info("Initializing Ruby Armor Materials");
        EmeraldMod.LOGGER.info("  - Base Durability: 1 (Will be overridden to UNBREAKABLE in item class)");
        EmeraldMod.LOGGER.info("  - Enchantability: 15 (Superior to Diamond/Emerald)");
        EmeraldMod.LOGGER.info("  - Toughness: 6.0 (Superior to Emerald 5.0)");
        EmeraldMod.LOGGER.info("  - Knockback Resistance: 0.4 (Superior to Emerald 0.3)");
        EmeraldMod.LOGGER.info("  - Protection: Superior to all existing armors");
    }
}