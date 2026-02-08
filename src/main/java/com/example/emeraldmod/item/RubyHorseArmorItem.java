package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.minecraft.entity.Entity;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

/**
 * Ruby Horse Armor — Fabric 1.21.
 *
 * Di MC 1.21, AnimalArmorItem extends ArmorItem.
 * Kedua class punya inner enum "Type" masing-masing,
 * dan getType() di ArmorItem return ArmorItem.Type.
 * javac tidak bisa resolve conflict ini pada ANY subclass dari AnimalArmorItem.
 * Error ini permanent dan tidak bisa di-fix dengan qualifier.
 *
 * Solusi: Jangan extend AnimalArmorItem.
 * Instantiate AnimalArmorItem langsung di registration, dan taruh
 * logic custom (unbreakable) di Item subclass yang extend Item saja.
 *
 * Untuk "unbreakable" behavior yang tetap jalan:
 *   - Kalau item di inventory player → inventoryTick() di sini yang handle.
 *   - Kalau item di-equip ke horse → pakai event handler (InventoryTickEvent)
 *     yang cek semua item di horse inventory dan panggil preventDamage().
 */
public class RubyHorseArmorItem extends Item {

    public RubyHorseArmorItem(Settings settings) {
        super(settings);
        EmeraldMod.LOGGER.info("Creating RubyHorseArmorItem");
        EmeraldMod.LOGGER.info("  - UNBREAKABLE - Will not take damage");
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        preventDamage(stack);
    }

    /**
     * Utility method — panggil dari event handler untuk items yang ada di
     * inventory entity manapun (misal horse yang equip armor ini).
     */
    public static void preventDamage(ItemStack stack) {
        if (stack.getDamage() > 0) {
            stack.setDamage(0);
        }
    }
}