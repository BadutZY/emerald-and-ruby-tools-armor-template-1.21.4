package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;

public class EmeraldArmorItem extends ArmorItem {
    private final ArmorItem.Type armorType;

    public EmeraldArmorItem(RegistryEntry<ArmorMaterial> material, ArmorItem.Type type, Settings settings) {
        // ✅ FIX: super() harus di statement pertama, gunakan inline expression
        super(material, type, settings.maxDamage(type.getMaxDamage(material.value().defense().getOrDefault(type, 0))));
        this.armorType = type;

        EmeraldMod.LOGGER.info("Creating EmeraldArmorItem: " + type.getName());
        EmeraldMod.LOGGER.info("  - Armor Type: " + type.getName());
        EmeraldMod.LOGGER.info("  - Max Damage: " + type.getMaxDamage(material.value().defense().getOrDefault(type, 0)));
    }

    public ArmorItem.Type getArmorType() {
        return this.armorType;
    }
}