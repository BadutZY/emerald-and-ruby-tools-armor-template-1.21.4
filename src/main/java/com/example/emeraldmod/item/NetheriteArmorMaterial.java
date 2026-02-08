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

public class NetheriteArmorMaterial {
    public static final RegistryEntry<ArmorMaterial> NETHERITE_HORSE_ARMOR_MATERIAL;

    static {
        NETHERITE_HORSE_ARMOR_MATERIAL = register("netherite",
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, 407);
                    map.put(ArmorItem.Type.CHESTPLATE, 592);
                    map.put(ArmorItem.Type.LEGGINGS, 555);
                    map.put(ArmorItem.Type.BOOTS, 481);
                    map.put(ArmorItem.Type.BODY, 592);
                }),
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 3);
                    map.put(ArmorItem.Type.LEGGINGS, 6);
                    map.put(ArmorItem.Type.CHESTPLATE, 8);
                    map.put(ArmorItem.Type.HELMET, 3);
                    map.put(ArmorItem.Type.BODY, 19);
                }),
                15,
                SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
                3.0F,
                0.1F,
                () -> Ingredient.ofItems(Items.NETHERITE_INGOT)
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
        EmeraldMod.LOGGER.info("Initializing Netherite Horse Armor Material (Vanilla Stats)");
    }
}