package com.example.emeraldmod.item;

import com.example.emeraldmod.EmeraldMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    private static RegistryKey<Item> createRegistryKey(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(EmeraldMod.MOD_ID, name));
    }

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(EmeraldMod.MOD_ID, name), item);
    }

    // ============================================
    // RUBY ITEMS (NEW!)
    // ============================================
    public static final Item RUBY = registerItem("ruby",
            new Item(new Item.Settings().registryKey(createRegistryKey("ruby")).fireproof()));

    public static final Item RAW_RUBY = registerItem("raw_ruby",
            new Item(new Item.Settings().registryKey(createRegistryKey("raw_ruby")).fireproof()));

    public static final Item RUBY_INGOT = registerItem("ruby_ingot",
            new Item(new Item.Settings().registryKey(createRegistryKey("ruby_ingot")).fireproof()));

    public static final Item RUBY_SCRAP = registerItem("ruby_scrap",
            new Item(new Item.Settings().registryKey(createRegistryKey("ruby_scrap")).fireproof()));

    public static final Item RUBY_NUGGET = registerItem("ruby_nugget",
            new Item(new Item.Settings().registryKey(createRegistryKey("ruby_nugget")).fireproof()));

    // ============================================
    // RUBY TOOLS (UNBREAKABLE!)
    // ============================================
    public static final SwordItem RUBY_SWORD = registerItem("ruby_sword",
            new RubyToolItem.RubySwordItem(RubyToolMaterial.RUBY, 6, -1.5F,
                    new Item.Settings().registryKey(createRegistryKey("ruby_sword")).fireproof()));

    public static final PickaxeItem RUBY_PICKAXE = registerItem("ruby_pickaxe",
            new RubyToolItem.RubyPickaxeItem(RubyToolMaterial.RUBY, 4, -1.5F,
                    new Item.Settings().registryKey(createRegistryKey("ruby_pickaxe")).fireproof()));

    public static final AxeItem RUBY_AXE = registerItem("ruby_axe",
            new RubyToolItem.RubyAxeItem(RubyToolMaterial.RUBY, 8, -2.5F,
                    new Item.Settings().registryKey(createRegistryKey("ruby_axe")).fireproof()));

    public static final ShovelItem RUBY_SHOVEL = registerItem("ruby_shovel",
            new RubyToolItem.RubyShovelItem(RubyToolMaterial.RUBY, 4.5F, -3.0F,
                    new Item.Settings().registryKey(createRegistryKey("ruby_shovel")).fireproof()));

    public static final HoeItem RUBY_HOE = registerItem("ruby_hoe",
            new RubyToolItem.RubyHoeItem(RubyToolMaterial.RUBY, -1, 3.0F,
                    new Item.Settings().registryKey(createRegistryKey("ruby_hoe")).fireproof()));

    // ============================================
    // RUBY ARMOR (UNBREAKABLE!)
    // ============================================
    public static final RubyArmorItem RUBY_HELMET = registerItem("ruby_helmet",
            new RubyArmorItem(RubyArmorMaterial.RUBY_ARMOR_MATERIAL, EquipmentType.HELMET,
                    new Item.Settings().registryKey(createRegistryKey("ruby_helmet")).fireproof()));

    public static final RubyArmorItem RUBY_CHESTPLATE = registerItem("ruby_chestplate",
            new RubyArmorItem(RubyArmorMaterial.RUBY_ARMOR_MATERIAL, EquipmentType.CHESTPLATE,
                    new Item.Settings().registryKey(createRegistryKey("ruby_chestplate")).fireproof()));

    public static final RubyArmorItem RUBY_LEGGINGS = registerItem("ruby_leggings",
            new RubyArmorItem(RubyArmorMaterial.RUBY_ARMOR_MATERIAL, EquipmentType.LEGGINGS,
                    new Item.Settings().registryKey(createRegistryKey("ruby_leggings")).fireproof()));

    public static final RubyArmorItem RUBY_BOOTS = registerItem("ruby_boots",
            new RubyArmorItem(RubyArmorMaterial.RUBY_ARMOR_MATERIAL, EquipmentType.BOOTS,
                    new Item.Settings().registryKey(createRegistryKey("ruby_boots")).fireproof()));

    // ============================================
    // BASIC RUBY TOOLS (UNBREAKABLE!)
    // ============================================
    public static final SwordItem BASIC_RUBY_SWORD = registerItem("basic_ruby_sword",
            new RubyToolItem.RubySwordItem(RubyToolMaterial.BASIC_RUBY, 5, -1.5F,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_sword")).fireproof()));

    public static final PickaxeItem BASIC_RUBY_PICKAXE = registerItem("basic_ruby_pickaxe",
            new RubyToolItem.RubyPickaxeItem(RubyToolMaterial.BASIC_RUBY, 3, -1.5F,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_pickaxe")).fireproof()));

    public static final AxeItem BASIC_RUBY_AXE = registerItem("basic_ruby_axe",
            new RubyToolItem.RubyAxeItem(RubyToolMaterial.BASIC_RUBY, 7, -2.5F,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_axe")).fireproof()));

    public static final ShovelItem BASIC_RUBY_SHOVEL = registerItem("basic_ruby_shovel",
            new RubyToolItem.RubyShovelItem(RubyToolMaterial.BASIC_RUBY, 3.5F, -3.0F,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_shovel")).fireproof()));

    public static final HoeItem BASIC_RUBY_HOE = registerItem("basic_ruby_hoe",
            new RubyToolItem.RubyHoeItem(RubyToolMaterial.BASIC_RUBY, -1, 2.0F,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_hoe")).fireproof()));

    // ============================================
    // BASIC RUBY ARMOR (UNBREAKABLE!)
    // ============================================
    public static final RubyArmorItem BASIC_RUBY_HELMET = registerItem("basic_ruby_helmet",
            new RubyArmorItem(RubyArmorMaterial.BASIC_RUBY_ARMOR_MATERIAL, EquipmentType.HELMET,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_helmet")).fireproof()));

    public static final RubyArmorItem BASIC_RUBY_CHESTPLATE = registerItem("basic_ruby_chestplate",
            new RubyArmorItem(RubyArmorMaterial.BASIC_RUBY_ARMOR_MATERIAL, EquipmentType.CHESTPLATE,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_chestplate")).fireproof()));

    public static final RubyArmorItem BASIC_RUBY_LEGGINGS = registerItem("basic_ruby_leggings",
            new RubyArmorItem(RubyArmorMaterial.BASIC_RUBY_ARMOR_MATERIAL, EquipmentType.LEGGINGS,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_leggings")).fireproof()));

    public static final RubyArmorItem BASIC_RUBY_BOOTS = registerItem("basic_ruby_boots",
            new RubyArmorItem(RubyArmorMaterial.BASIC_RUBY_ARMOR_MATERIAL, EquipmentType.BOOTS,
                    new Item.Settings().registryKey(createRegistryKey("basic_ruby_boots")).fireproof()));

    // ============================================
    // RUBY HORSE ARMOR (UNBREAKABLE!)
    // ============================================
    public static final AnimalArmorItem RUBY_HORSE_ARMOR = registerItem("ruby_horse_armor",
            new RubyHorseArmorItem(
                    RubyArmorMaterial.RUBY_ARMOR_MATERIAL,
                    AnimalArmorItem.Type.EQUESTRIAN,
                    new Item.Settings().registryKey(createRegistryKey("ruby_horse_armor")).fireproof().maxCount(1)
            ));

    // ============================================
    // BASIC EMERALD TOOLS
    // ============================================
    public static final SwordItem BASIC_EMERALD_SWORD = registerItem("basic_emerald_sword",
            new SwordItem(ModToolMaterial.BASIC_EMERALD, 4, -0.5F,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_sword")).fireproof()));

    public static final PickaxeItem BASIC_EMERALD_PICKAXE = registerItem("basic_emerald_pickaxe",
            new PickaxeItem(ModToolMaterial.BASIC_EMERALD, 2, -0.5F,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_pickaxe")).fireproof()));

    public static final AxeItem BASIC_EMERALD_AXE = registerItem("basic_emerald_axe",
            new AxeItem(ModToolMaterial.BASIC_EMERALD, 6, -1.5F,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_axe")).fireproof()));

    public static final ShovelItem BASIC_EMERALD_SHOVEL = registerItem("basic_emerald_shovel",
            new ShovelItem(ModToolMaterial.BASIC_EMERALD, 2.5F, -2.0F,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_shovel")).fireproof()));

    public static final HoeItem BASIC_EMERALD_HOE = registerItem("basic_emerald_hoe",
            new HoeItem(ModToolMaterial.BASIC_EMERALD, -1, 1.0F,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_hoe")).fireproof()));

    // ============================================
    // BASIC EMERALD ARMOR
    // ============================================
    public static final EmeraldArmorItem BASIC_EMERALD_HELMET = registerItem("basic_emerald_helmet",
            new EmeraldArmorItem(ModArmorMaterial.BASIC_EMERALD_ARMOR_MATERIAL, EquipmentType.HELMET,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_helmet")).fireproof()));

    public static final EmeraldArmorItem BASIC_EMERALD_CHESTPLATE = registerItem("basic_emerald_chestplate",
            new EmeraldArmorItem(ModArmorMaterial.BASIC_EMERALD_ARMOR_MATERIAL, EquipmentType.CHESTPLATE,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_chestplate")).fireproof()));

    public static final EmeraldArmorItem BASIC_EMERALD_LEGGINGS = registerItem("basic_emerald_leggings",
            new EmeraldArmorItem(ModArmorMaterial.BASIC_EMERALD_ARMOR_MATERIAL, EquipmentType.LEGGINGS,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_leggings")).fireproof()));

    public static final EmeraldArmorItem BASIC_EMERALD_BOOTS = registerItem("basic_emerald_boots",
            new EmeraldArmorItem(ModArmorMaterial.BASIC_EMERALD_ARMOR_MATERIAL, EquipmentType.BOOTS,
                    new Item.Settings().registryKey(createRegistryKey("basic_emerald_boots")).fireproof()));

    // ============================================
    // EMERALD ITEMS - NEW!
    // ============================================
    public static final Item EMERALD_NUGGET = registerItem("emerald_nugget",
            new Item(new Item.Settings().registryKey(createRegistryKey("emerald_nugget")).fireproof()));

    public static final Item EMERALD_CLUSTER = registerItem("emerald_cluster",
            new Item(new Item.Settings().registryKey(createRegistryKey("emerald_cluster")).fireproof()));

    // ============================================
    // EMERALD TOOLS
    // ============================================
    public static final SwordItem EMERALD_SWORD = registerItem("emerald_sword",
            new SwordItem(ModToolMaterial.EMERALD, 5, -1.5F,
                    new Item.Settings().registryKey(createRegistryKey("emerald_sword")).fireproof()));

    public static final PickaxeItem EMERALD_PICKAXE = registerItem("emerald_pickaxe",
            new PickaxeItem(ModToolMaterial.EMERALD, 3, -1.5F,
                    new Item.Settings().registryKey(createRegistryKey("emerald_pickaxe")).fireproof()));

    public static final AxeItem EMERALD_AXE = registerItem("emerald_axe",
            new AxeItem(ModToolMaterial.EMERALD, 7, -2.5F,
                    new Item.Settings().registryKey(createRegistryKey("emerald_axe")).fireproof()));

    public static final ShovelItem EMERALD_SHOVEL = registerItem("emerald_shovel",
            new ShovelItem(ModToolMaterial.EMERALD, 3.5F, -3.0F,
                    new Item.Settings().registryKey(createRegistryKey("emerald_shovel")).fireproof()));

    public static final HoeItem EMERALD_HOE = registerItem("emerald_hoe",
            new HoeItem(ModToolMaterial.EMERALD, -1, 2.0F,
                    new Item.Settings().registryKey(createRegistryKey("emerald_hoe")).fireproof()));

    // ============================================
    // EMERALD ARMOR
    // ============================================
    public static final EmeraldArmorItem EMERALD_HELMET = registerItem("emerald_helmet",
            new EmeraldArmorItem(ModArmorMaterial.EMERALD_ARMOR_MATERIAL, EquipmentType.HELMET,
                    new Item.Settings().registryKey(createRegistryKey("emerald_helmet")).fireproof()));

    public static final EmeraldArmorItem EMERALD_CHESTPLATE = registerItem("emerald_chestplate",
            new EmeraldArmorItem(ModArmorMaterial.EMERALD_ARMOR_MATERIAL, EquipmentType.CHESTPLATE,
                    new Item.Settings().registryKey(createRegistryKey("emerald_chestplate")).fireproof()));

    public static final EmeraldArmorItem EMERALD_LEGGINGS = registerItem("emerald_leggings",
            new EmeraldArmorItem(ModArmorMaterial.EMERALD_ARMOR_MATERIAL, EquipmentType.LEGGINGS,
                    new Item.Settings().registryKey(createRegistryKey("emerald_leggings")).fireproof()));

    public static final EmeraldArmorItem EMERALD_BOOTS = registerItem("emerald_boots",
            new EmeraldArmorItem(ModArmorMaterial.EMERALD_ARMOR_MATERIAL, EquipmentType.BOOTS,
                    new Item.Settings().registryKey(createRegistryKey("emerald_boots")).fireproof()));

    // ============================================
    // EMERALD HORSE ARMOR
    // ============================================
    public static final AnimalArmorItem EMERALD_HORSE_ARMOR = registerItem("emerald_horse_armor",
            new AnimalArmorItem(
                    ModArmorMaterial.EMERALD_ARMOR_MATERIAL,
                    AnimalArmorItem.Type.EQUESTRIAN,
                    new Item.Settings().registryKey(createRegistryKey("emerald_horse_armor")).fireproof().maxCount(1)
            ));

    // ============================================
    // NETHERITE HORSE ARMOR
    // ============================================
    public static final AnimalArmorItem NETHERITE_HORSE_ARMOR = registerItem("netherite_horse_armor",
            new AnimalArmorItem(
                    NetheriteArmorMaterial.NETHERITE_HORSE_ARMOR_MATERIAL,
                    AnimalArmorItem.Type.EQUESTRIAN,
                    new Item.Settings().registryKey(createRegistryKey("netherite_horse_armor")).fireproof().maxCount(1)
            ));

    // ============================================
    // TOTEMS OF UNDYING (NEW!)
    // ============================================
    public static final Item EMERALD_TOTEM = registerItem("emerald_totem",
            new EmeraldTotemItem(new Item.Settings()
                    .registryKey(createRegistryKey("emerald_totem"))
                    .maxCount(16)
                    .fireproof()));

    public static final Item RUBY_TOTEM = registerItem("ruby_totem",
            new RubyTotemItem(new Item.Settings()
                    .registryKey(createRegistryKey("ruby_totem"))
                    .maxCount(16)
                    .fireproof()));

    public static final Item EMERALD_APPLE = registerItem("emerald_apple",
            new EmeraldAppleItem(new Item.Settings()
                    .registryKey(createRegistryKey("emerald_apple"))
                    .fireproof()));

    public static final Item ENCHANTED_EMERALD_APPLE = registerItem("enchanted_emerald_apple",
            new EnchantedEmeraldAppleItem(new Item.Settings()
                    .registryKey(createRegistryKey("enchanted_emerald_apple"))
                    .fireproof()));

    public static final Item RUBY_APPLE = registerItem("ruby_apple",
            new RubyAppleItem(new Item.Settings()
                    .registryKey(createRegistryKey("ruby_apple"))
                    .fireproof()));

    public static final Item ENCHANTED_RUBY_APPLE = registerItem("enchanted_ruby_apple",
            new EnchantedRubyAppleItem(new Item.Settings()
                    .registryKey(createRegistryKey("enchanted_ruby_apple"))
                    .fireproof()));

    // ============================================
    // SMITHING TEMPLATES
    // ============================================
    public static final Item EMERALD_UPGRADE_SMITHING_TEMPLATE = registerItem(
            "emerald_upgrade_smithing_template",
            new Item(new Item.Settings().registryKey(createRegistryKey("emerald_upgrade_smithing_template")).maxCount(64)));

    public static final Item RUBY_UPGRADE_SMITHING_TEMPLATE = registerItem(
            "ruby_upgrade_smithing_template",
            new Item(new Item.Settings().registryKey(createRegistryKey("ruby_upgrade_smithing_template")).maxCount(64)));

    public static void registerModItems() {
        EmeraldMod.LOGGER.info("Registering Mod Items for " + EmeraldMod.MOD_ID);

        // Initialize materials FIRST
        RubyToolMaterial.initialize();
        RubyArmorMaterial.initialize();
        ModToolMaterial.initialize();
        ModArmorMaterial.initialize();
        NetheriteArmorMaterial.initialize();


        // Add Ruby items to Ingredients tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(NETHERITE_HORSE_ARMOR);
        });

        EmeraldMod.LOGGER.info("✓ Successfully registered all Ruby items (UNBREAKABLE)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Ruby Scrap (Nether Drop)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Emerald Ingot (Nether Drop)");
        EmeraldMod.LOGGER.info("✓ Successfully registered all Emerald items");
        EmeraldMod.LOGGER.info("✓ Successfully registered Netherite Horse Armor");
        EmeraldMod.LOGGER.info("✓ Successfully registered Ruby Upgrade Smithing Template");
        EmeraldMod.LOGGER.info("✓ Successfully registered Emerald Totem (3 uses)");
        EmeraldMod.LOGGER.info("✓ Successfully registered Ruby Totem (5 uses)");
    }
}