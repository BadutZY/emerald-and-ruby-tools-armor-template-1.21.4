package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public class RubyArmorItem extends ArmorItem {
    private final ArmorItem.Type armorType;

    public RubyArmorItem(RegistryEntry<ArmorMaterial> material, ArmorItem.Type type, Settings settings) {
        super(material, type, settings);
        this.armorType = type;

        EmeraldMod.LOGGER.info("Creating RubyArmorItem: " + type.getName());
        EmeraldMod.LOGGER.info("  - Armor Type: " + type.getName());
        EmeraldMod.LOGGER.info("  - UNBREAKABLE - Will not take damage");
    }

    public ArmorItem.Type getArmorType() {
        return this.armorType;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (stack.getDamage() > 0) {
            stack.setDamage(0);
        }
    }
}